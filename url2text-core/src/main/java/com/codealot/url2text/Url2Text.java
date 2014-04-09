package com.codealot.url2text;

import static com.codealot.url2text.Constants.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.HttpHeaders;
import org.apache.tika.metadata.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DocumentType;

import com.gargoylesoftware.htmlunit.BinaryPage;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.JavaScriptPage;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebClientOptions;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.gargoylesoftware.htmlunit.xml.XmlPage;

/**
 * Command line utility and class to fetch the content of a URL as text, with
 * optional metadata, in plain text, JSON, or XML format.
 * <p>
 * Sits atop the awesome HtmlUnit and Apache Tika.
 * <p>
 * Note: not thread safe. Intended to be used in a single thread.
 * <p>
 * Basic use is: <code>
    import com.codealot.url2text.Url2Text;
    import com.codealot.url2text.Url2TextException;
    import com.codealot.url2text.Url2TextResponse;    
    ...
    Url2Text fetch = new Url2Text();
    try {        
        fetch.setJavascriptEnabled(true);
        fetch.setIncludeHeaders(true);
        fetch.setIncludeMetadata(true);
        ...
        Url2TextResponse response = fetch.contentAsText("http://example.com");
        if (response.getStatus() == 200) {
            return response.toJson();
        }
    } catch (Url2TextException e) {
        ...
    }
 * </code>
 * <p>
 * The `Url2TextResponse` object is a POJO encapsulating the fetched text,
 * metadata, headers, etc.
 * <p>
 * Currently Url2Text emulates FireFox. (This project was inspired by a need to
 * act as a proxy for a human user).
 * <p>
 * Most of the HtmlUnit
 * 
 * <pre>
 * WebClientOptions
 * </pre>
 * 
 * and
 * 
 * <pre>
 * CookieManager
 * </pre>
 * 
 * features are exposed as properties of the Url2Text class. Headers can also be
 * added to the
 * 
 * <pre>
 * WebRequest
 * </pre>.
 * <p>
 * No transient state is stored in the
 * 
 * <pre>
 * Url2Text
 * </pre>
 * 
 * instance, so they can be reused safely.
 * 
 * @author jacobsp
 *         <p>
 *         Copyright (C) 2014 Codealot Limited.
 *         <p>
 *         Licensed under the Apache License, Version 2.0 (the "License"); you
 *         may not use this file except in compliance with the License. You may
 *         obtain a copy of the License at
 *         http://www.apache.org/licenses/LICENSE-2.0
 *         <p>
 *         Unless required by applicable law or agreed to in writing, software
 *         distributed under the License is distributed on an "AS IS" BASIS,
 *         WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *         implied. See the License for the specific language governing
 *         permissions and limitations under the License.
 */
public class Url2Text
{

    // FUTURE suppress 'enable javascript' and 'enable cookies' messages when
    // those options have been specified (but might be an HtmlUnit bug)

    // FUTURE timeout for text conversion

    // FUTURE add http proxy support

    // FUTURE add credential support

    // SLF4J logger instance
    private static final Logger LOG = LoggerFactory.getLogger(Url2Text.class);

    // ######################
    // ##### PROPERTIES #####
    // ######################

    // FUTURE use reflection to ensure all properties are tested.

    // Number of configurable properties
    private static int PROPERTY_COUNT = 20;

    // HtmlUnit WebClientOptions (not set by the CLI)
    private boolean activeXNative = false;
    private boolean appletEnabled = false;
    private boolean geolocationEnabled = false;
    private boolean popupBlockerEnabled = true;
    private boolean exceptionOnScriptError = false;
    private boolean exceptionOnFailingStatusCode = false;
    private boolean printContentOnFailingStatusCode = false;

    // HtmlUnit WebClientOptions (set by the CLI)
    private boolean cssEnabled = false;
    private boolean doNotTrackEnabled = false;
    private boolean javascriptEnabled = false;
    private boolean useInsecureSSL = false;
    private boolean redirectEnabled = true;

    // HtmlUnit CookieManager options
    private boolean cookiesEnabled = true;
    private boolean clearCookies = true;
    private boolean clearExpiredCookies = true;

    // output modifying options
    private boolean includeHeaders = false;
    private boolean includeMetadata = false;

    // Transaction timeouts, in seconds.
    private int networkTimeout = 90;
    private int javascriptTimeout = 20;

    // Max Content Length
    private long maxContentLength = 1_024 * 1_024;

    // ########################
    // ##### CONSTRUCTORS #####
    // ########################
    /**
     * Default constructor, enabling bean-hood.
     */
    public Url2Text()
    {
        // default
    };

    /**
     * Constructor that accepts a Properties instance to configure the defaults.
     * <p>
     * Only settings that vary from the defaults need to be present.
     * <p>
     * An empty Properties instance is valid but a null is not.  
     * 
     * @param properties
     */
    public Url2Text(Properties properties)
    {
        Objects.requireNonNull(properties, "No Properties supplied.");

        if (properties.size() > PROPERTY_COUNT)
        {
            throw new IllegalArgumentException("Too many properties. Expected "
                    + PROPERTY_COUNT + " but have " + properties.size());
        }

        String activeXNative = properties.getProperty(ACTIVEX_NATIVE, Boolean
                .valueOf(this.activeXNative).toString());
        String appletEnabled = properties.getProperty(APPLET_ENABLED, Boolean
                .valueOf(this.appletEnabled).toString());
        String geolocationEnabled = properties.getProperty(GEOLOCATION_ENABLED,
                Boolean.valueOf(this.geolocationEnabled).toString());
        String popupBlockerEnabled = properties.getProperty(
                POPUP_BLOCKER_ENABLED, Boolean
                        .valueOf(this.popupBlockerEnabled).toString());
        String exceptionOnScriptError = properties.getProperty(
                EXCEPTION_ON_SCRIPT_ERROR,
                Boolean.valueOf(this.exceptionOnScriptError).toString());
        String exceptionOnFailingStatusCode = properties.getProperty(
                EXCEPTION_ON_FAILING_STATUS,
                Boolean.valueOf(this.exceptionOnFailingStatusCode).toString());
        String printContentOnFailingStatusCode = properties.getProperty(
                PRINT_CONTENT_ON_FAILING_STATUS,
                Boolean.valueOf(this.printContentOnFailingStatusCode)
                        .toString());
        String cssEnabled = properties.getProperty(CSS_ENABLED, Boolean
                .valueOf(this.cssEnabled).toString());
        String doNotTrackEnabled = properties.getProperty(DO_NOT_TRACK_ENABLED,
                Boolean.valueOf(this.doNotTrackEnabled).toString());
        String javascriptEnabled = properties.getProperty(JAVASCRIPT_ENABLED,
                Boolean.valueOf(this.javascriptEnabled).toString());
        String useInsecureSSL = properties.getProperty(USE_INSECURE_SSL,
                Boolean.valueOf(this.useInsecureSSL).toString());
        String redirectEnabled = properties.getProperty(REDIRECT_ENABLED,
                Boolean.valueOf(this.redirectEnabled).toString());
        String cookiesEnabled = properties.getProperty(COOKIES_ENABLED, Boolean
                .valueOf(this.cookiesEnabled).toString());
        String clearCookies = properties.getProperty(CLEAR_COOKIES, Boolean
                .valueOf(this.clearCookies).toString());
        String clearExpiredCookies = properties.getProperty(
                CLEAR_EXPIRED_COOKIES, Boolean
                        .valueOf(this.clearExpiredCookies).toString());
        String includeHeaders = properties.getProperty(INCLUDE_HEADERS, Boolean
                .valueOf(this.includeHeaders).toString());
        String includeMetadata = properties.getProperty(INCLUDE_METADATA,
                Boolean.valueOf(this.includeMetadata).toString());
        String networkTimeout = properties.getProperty(NETWORK_TIMEOUT, Integer
                .valueOf(this.networkTimeout).toString());
        String javascriptTimeout = properties.getProperty(JAVASCRIPT_TIMEOUT,
                Integer.valueOf(this.javascriptTimeout).toString());
        String maxContentLength = properties.getProperty(MAX_CONTENT_LENGTH,
                Long.valueOf(this.maxContentLength).toString());

        setActiveXNative(Boolean.valueOf(activeXNative));
        setAppletEnabled(Boolean.valueOf(appletEnabled));
        setGeolocationEnabled(Boolean.valueOf(geolocationEnabled));
        setPopupBlockerEnabled(Boolean.valueOf(popupBlockerEnabled));
        setExceptionOnScriptError(Boolean.valueOf(exceptionOnScriptError));
        setExceptionOnFailingStatusCode(Boolean
                .valueOf(exceptionOnFailingStatusCode));
        setPrintContentOnFailingStatusCode(Boolean
                .valueOf(printContentOnFailingStatusCode));
        setCssEnabled(Boolean.valueOf(cssEnabled));
        setDoNotTrackEnabled(Boolean.valueOf(doNotTrackEnabled));
        setJavascriptEnabled(Boolean.valueOf(javascriptEnabled));
        setUseInsecureSSL(Boolean.valueOf(useInsecureSSL));
        setRedirectEnabled(Boolean.valueOf(redirectEnabled));
        setCookiesEnabled(Boolean.valueOf(cookiesEnabled));
        setClearCookies(Boolean.valueOf(clearCookies));
        setClearExpiredCookies(Boolean.valueOf(clearExpiredCookies));
        setIncludeHeaders(Boolean.valueOf(includeHeaders));
        setIncludeMetadata(Boolean.valueOf(includeMetadata));
        setNetworkTimeout(Integer.valueOf(networkTimeout));
        setJavascriptTimeout(Integer.valueOf(javascriptTimeout));
        setMaxContentLength(Long.valueOf(maxContentLength));
    }

    // ###################
    // ##### METHODS #####
    // ###################

    /**
     * Returns a Properties object, encapsulating the current config.
     * 
     * @return encapsulation of current config
     */
    public Properties configAsProperties()
    {
        Properties properties = new Properties();

        properties.setProperty(ACTIVEX_NATIVE,
                Boolean.valueOf(this.activeXNative).toString());
        properties.setProperty(APPLET_ENABLED,
                Boolean.valueOf(this.appletEnabled).toString());
        properties.setProperty(GEOLOCATION_ENABLED,
                Boolean.valueOf(this.geolocationEnabled).toString());
        properties.setProperty(POPUP_BLOCKER_ENABLED,
                Boolean.valueOf(this.popupBlockerEnabled).toString());
        properties.setProperty(EXCEPTION_ON_SCRIPT_ERROR,
                Boolean.valueOf(this.exceptionOnScriptError).toString());
        properties.setProperty(EXCEPTION_ON_FAILING_STATUS,
                Boolean.valueOf(this.exceptionOnFailingStatusCode).toString());
        properties.setProperty(PRINT_CONTENT_ON_FAILING_STATUS, Boolean
                .valueOf(this.printContentOnFailingStatusCode).toString());
        properties.setProperty(CSS_ENABLED, Boolean.valueOf(this.cssEnabled)
                .toString());
        properties.setProperty(DO_NOT_TRACK_ENABLED,
                Boolean.valueOf(this.doNotTrackEnabled).toString());
        properties.setProperty(JAVASCRIPT_ENABLED,
                Boolean.valueOf(this.javascriptEnabled).toString());
        properties.setProperty(USE_INSECURE_SSL,
                Boolean.valueOf(this.useInsecureSSL).toString());
        properties.setProperty(REDIRECT_ENABLED,
                Boolean.valueOf(this.redirectEnabled).toString());
        properties.setProperty(COOKIES_ENABLED,
                Boolean.valueOf(this.cookiesEnabled).toString());
        properties.setProperty(CLEAR_COOKIES, Boolean
                .valueOf(this.clearCookies).toString());
        properties.setProperty(CLEAR_EXPIRED_COOKIES,
                Boolean.valueOf(this.clearExpiredCookies).toString());
        properties.setProperty(INCLUDE_HEADERS,
                Boolean.valueOf(this.includeHeaders).toString());
        properties.setProperty(INCLUDE_METADATA,
                Boolean.valueOf(this.includeMetadata).toString());
        properties.setProperty(NETWORK_TIMEOUT,
                Integer.valueOf(this.networkTimeout).toString());
        properties.setProperty(JAVASCRIPT_TIMEOUT,
                Integer.valueOf(this.javascriptTimeout).toString());
        properties.setProperty(MAX_CONTENT_LENGTH,
                Long.valueOf(this.maxContentLength).toString());

        if (properties.size() != PROPERTY_COUNT)
        {
            throw new IllegalStateException(
                    "Incorrect property count. Expected " + PROPERTY_COUNT
                            + " but have " + properties.size());
        }
        return properties;
    }
    
    @Override
    public int hashCode()
    {
        return Objects.hash(
                this.activeXNative, 
                this.appletEnabled, 
                this.clearCookies,
                this.clearExpiredCookies, 
                this.cookiesEnabled, 
                this.cssEnabled,
                this.doNotTrackEnabled, 
                this.exceptionOnFailingStatusCode, 
                this.exceptionOnScriptError,
                this.geolocationEnabled, 
                this.includeHeaders, 
                this.includeMetadata,
                this.javascriptEnabled, 
                this.popupBlockerEnabled,
                this.printContentOnFailingStatusCode,
                this.redirectEnabled,
                this.useInsecureSSL,
                this.networkTimeout,
                this.javascriptTimeout,
                this.maxContentLength);
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass())
        {
            return false;
        }
        final Url2Text test = (Url2Text) obj;
        // all content is included in properties, so this is safe.
        return test.configAsProperties().equals(this.configAsProperties());
    }

    /**
     * Configure a WebClient using internal state.
     * 
     * @return the configured WebClient
     */
    private WebClient prepareWebClient()
    {
        WebClient client = new WebClient(BrowserVersion.FIREFOX_24);

        if (this.javascriptTimeout > 0)
        {
            client.waitForBackgroundJavaScript(this.javascriptTimeout * 1_000);
        }
        final WebClientOptions options = client.getOptions();

        // configure web client
        options.setActiveXNative(this.activeXNative);
        options.setAppletEnabled(this.appletEnabled);
        options.setCssEnabled(this.cssEnabled);
        options.setDoNotTrackEnabled(this.doNotTrackEnabled);
        options.setGeolocationEnabled(this.geolocationEnabled);
        options.setJavaScriptEnabled(this.javascriptEnabled);
        options.setPopupBlockerEnabled(this.popupBlockerEnabled);
        options.setPrintContentOnFailingStatusCode(this.printContentOnFailingStatusCode);
        options.setRedirectEnabled(this.redirectEnabled);
        options.setThrowExceptionOnFailingStatusCode(this.exceptionOnFailingStatusCode);
        options.setThrowExceptionOnScriptError(this.exceptionOnScriptError);
        options.setUseInsecureSSL(this.useInsecureSSL);
        options.setTimeout(this.networkTimeout * 1_000);

        // configure cookies
        final CookieManager co = client.getCookieManager();
        co.setCookiesEnabled(this.cookiesEnabled);
        if (this.cookiesEnabled)
        {
            if (this.clearCookies)
            {
                co.clearCookies();
            }
            else if (this.clearExpiredCookies)
            {
                co.clearExpired(new Date());
            }
        }

        return client;
    }

    /**
     * Determine if a page contains docbook content.
     * 
     * @param page
     * @return boolean flag
     */
    private boolean isDocbook(final Page page)
    {
        boolean isDocbook = false;

        if (page instanceof XmlPage)
        {
            final XmlPage xmlPage = (XmlPage) page;

            // test for docBook
            String nameSpace = xmlPage.getXmlDocument().getDocumentElement()
                    .getNamespaceURI();
            DocumentType documentType = xmlPage.getXmlDocument().getDoctype();
            if (documentType != null)
            {
                final String publicId = documentType.getPublicId();
                final String systemId = documentType.getSystemId();

                nameSpace = "" + nameSpace + publicId + systemId; // ns might be
                                                                  // null,
                // hence the ""+
            }
            if (nameSpace != null && nameSpace.length() > 0)
            {
                isDocbook = nameSpace.toLowerCase(Locale.ENGLISH).contains(
                        "docbook");
            }
        }
        return isDocbook;
    }

    /**
     * Convenience method, which calls {@link #contentAsText(URL, Map)}.
     * 
     * @param requestUrl
     * @param additionalHeaders
     * @return a response object
     * @throws Url2TextException
     */
    public Url2TextResponse contentAsText(final String requestUrl,
            final Map<String, String> additionalHeaders)
            throws Url2TextException
    {
        try
        {
            final URL url = new URL(requestUrl);
            return contentAsText(url, additionalHeaders);
        }
        catch (MalformedURLException e)
        {
            throw new Url2TextException(e);
        }
    }

    /**
     * Construct a WebRequest and add any additional headers.
     * 
     * @param requestUrl
     * @param additionalHeaders
     * @return the configured WebRequest
     */
    private WebRequest prepareRequest(final URL requestUrl,
            final Map<String, String> additionalHeaders)
    {
        WebRequest request = new WebRequest(requestUrl, HttpMethod.GET);

        request.setCharset(UTF_8);

        if (additionalHeaders != null && !additionalHeaders.isEmpty())
        {
            request.setAdditionalHeaders(additionalHeaders);
        }
        return request;
    }

    /**
     * Fetch the requestUrl content, convert it to text, and return a
     * Url2TextResponse.
     * <p>
     * The requestUrl can be of any protocol supported by HtmlUnit, including
     * file://, http://, and https://.
     * <p>
     * Extra headers, such as If-Modified-Since or If-None-Match, can be
     * supplied in Map form.
     * 
     * @param requestUrl
     *            URL to fetch
     * @param additionalHeaders
     *            extra request headers
     * @return the data generated by the fetch operation
     * @throws Url2TextException
     */
    public Url2TextResponse contentAsText(final URL requestUrl,
            final Map<String, String> additionalHeaders)
            throws Url2TextException
    {
        // check params
        Objects.requireNonNull(requestUrl, "No URL available to be fetched.");

        final WebRequest request = prepareRequest(requestUrl, additionalHeaders);
        final WebClient client = prepareWebClient();

        // fetch page
        LOG.debug("Fetching page {}", requestUrl.toExternalForm());
        Page page = null;
        try
        {
            page = client.getPage(request);
        }
        catch (FailingHttpStatusCodeException | IOException e)
        {
            throw new Url2TextException("Failed to fetch page.", e);
        }

        // grab metadata from the fetch transaction
        final Url2TextResponse response = buildResponse(requestUrl, page,
                this.includeHeaders);

        // check content length
        if (this.maxContentLength > 0
                && response.getContentLength() > this.maxContentLength)
        {
            throw new Url2TextException("Content too long.  Limit is "
                    + this.maxContentLength + ", actual is "
                    + response.getContentLength());
        }

        // temp var to hold interim text
        String text = "";

        // discover if content is DocBook
        final boolean isDocBook = isDocbook(page);

        // retrieve metadata, and/or binary content using Tika
        if (this.includeMetadata || isDocBook || page instanceof BinaryPage
                || page instanceof UnexpectedPage)
        {
            try
            {
                final long convertStart = new Date().getTime();

                final Metadata metadata = new Metadata();
                final Tika tika = new Tika();
                tika.setMaxStringLength(-1); // unlimited length response

                final String contentType = response.getContentType();
                if (contentType != null && contentType.length() > 0)
                {
                    metadata.add(HttpHeaders.CONTENT_TYPE, contentType);
                }
                text = tika.parseToString(page.getWebResponse()
                        .getContentAsStream(), metadata);

                response.setConversionTime(new Date().getTime() - convertStart);

                if (this.includeMetadata)
                {
                    // response.setConversionMetadata(metadata);
                    addMetadataToResponse(metadata, response);
                }
            }
            catch (IOException | TikaException e)
            {
                throw new Url2TextException(
                        "Failed to convert text (content encrypted)?", e);
            }
        }

        // retrieve text content, if not already determined above
        if (page.isHtmlPage())
        {
            // use HtmlUnit's DOM for JavaScript execution artifacts
            final HtmlPage source = (HtmlPage) page;
            text = source.asText();
        }
        else if (page instanceof TextPage)
        {

            final TextPage source = (TextPage) page;
            text = source.getContent();
        }
        else if (page instanceof XmlPage && !isDocBook)
        {
            // Return the unaltered document (XHtml is dealt with above).
            text = page.getWebResponse().getContentAsString();
        }
        else if (page instanceof JavaScriptPage)
        {

            final JavaScriptPage source = (JavaScriptPage) page;
            text = source.getContent();
        }

        // record the converted text
        response.setConvertedText(text);
        LOG.debug(response.toString());

        return response;
    }

    /**
     * Add the Tika metadata into the response object.
     * 
     * @param tikaMetadata
     * @param response
     */
    private void addMetadataToResponse(final Metadata tikaMetadata,
            final Url2TextResponse response)
    {
        final List<NameAndValue> localHeaders = new ArrayList<>();
        final String[] headers = tikaMetadata.names();
        for (final String header : headers)
        {

            final String[] values = tikaMetadata.getValues(header);
            if (values.length == 0)
            {
                localHeaders.add(new NameAndValue(header, ""));
                continue;
            }
            for (final String value : values)
            {
                localHeaders.add(new NameAndValue(header, value));
            }
        }
        response.setContentMetadata(localHeaders);
    }

    /**
     * Build a response object with transaction metadata.
     * 
     * @param requestUrl
     * @param page
     * @param includeHeaders
     * @return populated response
     */
    private Url2TextResponse buildResponse(final URL requestUrl,
            final Page page, final boolean includeHeaders)
    {
        final Url2TextResponse response = new Url2TextResponse();

        // capture the request URL
        response.setRequestPage(requestUrl.toExternalForm());

        // capture some page details
        response.setLandingPage(page.getUrl().toExternalForm());

        // capture some response details
        final WebResponse webResponse = page.getWebResponse();

        response.setStatus(webResponse.getStatusCode());
        response.setStatusMessage(webResponse.getStatusMessage());
        response.setFetchTime(webResponse.getLoadTime());
        response.setContentType(webResponse.getContentType());
        response.setContentCharset(webResponse.getContentCharset());
        response.setEtag(webResponse.getResponseHeaderValue(ETAG));
        response.setLastModified(webResponse
                .getResponseHeaderValue(LAST_MODIFIED));
        response.setContentLength(webResponse
                .getResponseHeaderValue(CONTENT_LENGTH));

        // add headers, if asked for.
        if (includeHeaders)
        {
            // convert HtmlUnit NameValuePair to ours, to avoid dependency.
            final List<NameAndValue> localHeaders = new ArrayList<>();
            for (final NameValuePair nvp : webResponse.getResponseHeaders())
            {
                localHeaders
                        .add(new NameAndValue(nvp.getName(), nvp.getValue()));
            }
            response.setResponseHeaders(localHeaders);
        }
        return response;
    }

    public boolean hasActiveXNative()
    {
        return this.activeXNative;
    }

    public void setActiveXNative(final boolean activeXNative)
    {
        this.activeXNative = activeXNative;
        LOG.debug("ActiveX enabled: {}", activeXNative);
    }

    public boolean hasAppletEnabled()
    {
        return appletEnabled;
    }

    public void setAppletEnabled(final boolean appletEnabled)
    {
        this.appletEnabled = appletEnabled;
        LOG.debug("Applets enabled: {}", appletEnabled);
    }

    public boolean hasGeolocationEnabled()
    {
        return this.geolocationEnabled;
    }

    public void setGeolocationEnabled(final boolean geolocationEnabled)
    {
        this.geolocationEnabled = geolocationEnabled;
        LOG.debug("Geolocation enabled: {}", geolocationEnabled);
    }

    public boolean hasExceptionOnScriptError()
    {
        return this.exceptionOnScriptError;
    }

    public void setExceptionOnScriptError(final boolean exceptionOnScriptError)
    {
        this.exceptionOnScriptError = exceptionOnScriptError;
        LOG.debug("Exception on script error: {}", exceptionOnScriptError);
    }

    public boolean hasExceptionOnFailingStatusCode()
    {
        return this.exceptionOnFailingStatusCode;
    }

    public void setExceptionOnFailingStatusCode(
            final boolean exceptionOnFailingStatusCode)
    {
        this.exceptionOnFailingStatusCode = exceptionOnFailingStatusCode;
        LOG.debug("Exception on failing status code: {}",
                exceptionOnFailingStatusCode);
    }

    public boolean hasPrintContentOnFailingStatusCode()
    {
        return this.printContentOnFailingStatusCode;
    }

    public void setPrintContentOnFailingStatusCode(
            final boolean printContentOnFailingStatusCode)
    {
        this.printContentOnFailingStatusCode = printContentOnFailingStatusCode;
        LOG.debug("Print content on failing status code: {}",
                printContentOnFailingStatusCode);
    }

    public boolean hasCssEnabled()
    {
        return this.cssEnabled;
    }

    public void setCssEnabled(final boolean cssEnabled)
    {
        this.cssEnabled = cssEnabled;
        LOG.debug("CSS enabled: {}", cssEnabled);
    }

    public boolean hasDoNotTrackEnabled()
    {
        return this.doNotTrackEnabled;
    }

    public void setDoNotTrackEnabled(final boolean doNotTrackEnabled)
    {
        this.doNotTrackEnabled = doNotTrackEnabled;
        LOG.debug("Do not track enabled: {}", doNotTrackEnabled);
    }

    public boolean hasJavascriptEnabled()
    {
        return this.javascriptEnabled;
    }

    public void setJavascriptEnabled(final boolean javascriptEnabled)
    {
        this.javascriptEnabled = javascriptEnabled;
        LOG.debug("Javascript enabled: {}", javascriptEnabled);
    }

    public boolean hasPopupBlockerEnabled()
    {
        return this.popupBlockerEnabled;
    }

    public void setPopupBlockerEnabled(final boolean popupBlockerEnabled)
    {
        this.popupBlockerEnabled = popupBlockerEnabled;
        LOG.debug("Popup blocker enabled: {}", popupBlockerEnabled);
    }

    public boolean hasRedirectEnabled()
    {
        return this.redirectEnabled;
    }

    public void setRedirectEnabled(final boolean redirectEnabled)
    {
        this.redirectEnabled = redirectEnabled;
        LOG.debug("Redirect enabled: {}", redirectEnabled);
    }

    public boolean hasUseInsecureSSL()
    {
        return this.useInsecureSSL;
    }

    public void setUseInsecureSSL(final boolean useInsecureSSL)
    {
        this.useInsecureSSL = useInsecureSSL;
        LOG.debug("Insecure SSL: {}", useInsecureSSL);
    }

    public boolean hasCookiesEnabled()
    {
        return this.cookiesEnabled;
    }

    public void setCookiesEnabled(final boolean cookiesEnabled)
    {
        this.cookiesEnabled = cookiesEnabled;
        LOG.debug("Cookies enabled: {}", cookiesEnabled);
    }

    public boolean hasClearCookies()
    {
        return this.clearCookies;
    }

    public void setClearCookies(final boolean clearCookies)
    {
        this.clearCookies = clearCookies;
        LOG.debug("Clear all cookies: {}", clearCookies);
    }

    public boolean hasClearExpiredCookies()
    {
        return this.clearExpiredCookies;
    }

    /**
     * Note that current datetime is always applied.
     * 
     * @param clearExpiredCookies
     */
    public void setClearExpiredCookies(final boolean clearExpiredCookies)
    {
        this.clearExpiredCookies = clearExpiredCookies;
        LOG.debug("Clear expired cookies: {}", clearExpiredCookies);
    }

    public int getNetworkTimeout()
    {
        return this.networkTimeout;
    }

    /**
     * HTTP transaction timeout, in seconds. If download has not completed in
     * twice this number of seconds, it will be aborted. Default is 90.
     * <p>
     * Use zero to set infinite timeout.
     * 
     * @param timeout
     */
    public void setNetworkTimeout(final int seconds)
    {
        if (seconds < 0)
        {
            throw new IllegalArgumentException("Timeout cannot be negative.");
        }
        if (seconds == 0)
        {
            LOG.warn("Infinite timeout set.");
        }
        this.networkTimeout = seconds;
        LOG.debug("Timeout (seconds): {}", seconds);
    }

    public boolean hasIncludeHeaders()
    {
        return this.includeHeaders;
    }

    /**
     * Include a copy of the HTTP response headers in the output.
     * 
     * @param includeHeaders
     */
    public void setIncludeHeaders(final boolean includeHeaders)
    {

        this.includeHeaders = includeHeaders;
        LOG.debug("Include headers: {}", includeHeaders);
    }

    public boolean hasIncludeMetadata()
    {
        return this.includeMetadata;
    }

    /**
     * Include HTTP transaction and Tika-generated content metadata in the
     * output.
     * 
     * @param includeMetadata
     */
    public void setIncludeMetadata(final boolean includeMetadata)
    {

        this.includeMetadata = includeMetadata;
        LOG.debug("Include metadata: {}", includeMetadata);
    }

    public int getJavascriptTimeout()
    {
        return this.javascriptTimeout;
    }

    /**
     * Period to wait for the completion of JavaScript execution, in seconds.
     * Any function not returning in this period will be aborted. Default is 20.
     * A zero or negative number disables the application of any timeout.
     * 
     * @param javascriptTimeout
     */
    public void setJavascriptTimeout(final int seconds)
    {
        this.javascriptTimeout = seconds;
    }

    public long getMaxContentLength()
    {
        return this.maxContentLength;
    }

    /**
     * Sets a limit to the Content-Length that will be downloaded. Default is
     * 1MiB. A warning is issued if the value is less than 10,000. Zero or
     * negative values disable length checking.
     * 
     * @param maxLength
     */
    public void setMaxContentLength(final long maxLength)
    {
        if (maxLength <= 0L)
        {
            LOG.warn("Max length checking disabled.");
        }
        if (maxLength < 10_000L)
        {
            LOG.warn("Max length set to {}.", maxLength);
        }
        this.maxContentLength = maxLength;
    }

}
