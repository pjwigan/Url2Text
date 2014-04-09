package com.codealot.url2text;

/**
 * Constants for static import, plus an enum of the supported output formats.
 * <p>
 * There is no behaviour here, so an interface isn't appropriate.
 * 
 * @author jacobsp
 * 
 *         <p>
 *         Copyright (C) 2014 Codealot Limited.
 * 
 *         <p>
 *         Licensed under the Apache License, Version 2.0 (the "License"); you
 *         may not use this file except in compliance with the License. You may
 *         obtain a copy of the License at
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *         <p>
 *         Unless required by applicable law or agreed to in writing, software
 *         distributed under the License is distributed on an "AS IS" BASIS,
 *         WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *         implied. See the License for the specific language governing
 *         permissions and limitations under the License.
 */
public class Constants
{
    public static final String APP_VERSION = "0.5.0";

    // *_NOT_SET indicators, used as initialisers.
    public static final String STR_NOT_SET = "*not-set*";
    public static final int INT_NOT_SET = Integer.MIN_VALUE;
    public static final long LONG_NOT_SET = Long.MIN_VALUE;

    // Header-Like labels
    public static final String CONTENT_CHARSET = "Content-Charset";
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String CONTENT_METADATA = "Content-Metadata";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONVERSION_TIME = "Conversion-Time";
    public static final String CONVERTED_TEXT = "Converted-Text";
    public static final String ETAG = "ETag";
    public static final String FETCH_TIME = "Fetch-Time";
    public static final String IF_MODIFIED_SINCE = "If-Modified-Since";
    public static final String IF_NONE_MATCH = "If-None-Match";
    public static final String LANDING_PAGE = "Landing-Page";
    public static final String LAST_MODIFIED = "Last-Modified";
    public static final String REQUEST_PAGE = "Request-Page";
    public static final String RESPONSE_HEADERS = "Response-Headers";
    public static final String STATUS = "Status";
    public static final String STATUS_MESSAGE = "Status-Message";
    public static final String TRANSACTION_METADATA = "Transaction-Metadata";

    // Url2Text.class System property labels.  Also used as GET params (length
    // irrelevant as not intended to be typed).
    public static final String ACTIVEX_NATIVE = "url2text.activeXNative";
    public static final String APPLET_ENABLED = "url2text.appletEnabled";
    public static final String GEOLOCATION_ENABLED = "url2text.geolocationEnabled";
    public static final String POPUP_BLOCKER_ENABLED = "url2text.popupBlockerEnabled";
    public static final String EXCEPTION_ON_SCRIPT_ERROR = "url2text.exceptionOnScriptError";
    public static final String EXCEPTION_ON_FAILING_STATUS = "url2text.exceptionOnFailingStatus";
    public static final String PRINT_CONTENT_ON_FAILING_STATUS = "url2text.printContentOnFailingStatus";
    public static final String CSS_ENABLED = "url2text.cssEnabled";
    public static final String DO_NOT_TRACK_ENABLED = "url2text.doNotTrackEnabled";
    public static final String JAVASCRIPT_ENABLED = "url2text.javascriptEnabled";
    public static final String USE_INSECURE_SSL = "url2text.useInsecureSSL";
    public static final String REDIRECT_ENABLED = "url2text.redirectEnabled";
    public static final String COOKIES_ENABLED = "url2text.cookiesEnabled";
    public static final String CLEAR_COOKIES = "url2text.clearCookies";
    public static final String CLEAR_EXPIRED_COOKIES = "url2text.clearExpiredCookies";
    public static final String INCLUDE_HEADERS = "url2text.includeHeaders";
    public static final String INCLUDE_METADATA = "url2text.includeMetadata";
    public static final String NETWORK_TIMEOUT = "url2text.networkTimeout";
    public static final String JAVASCRIPT_TIMEOUT = "url2text.javascriptTimeout";
    public static final String MAX_CONTENT_LENGTH = "url2text.maxContentLength";
    // GET only param
    public static final String REQUEST_URL = "url2text.requestUrl";
    
    // Collection of Url2Text.class property labels (i.e. GET params excluded)
    public static final String[] URL2TEXT_PROPERTY_KEYS = {
        ACTIVEX_NATIVE,
        APPLET_ENABLED,
        GEOLOCATION_ENABLED,
        POPUP_BLOCKER_ENABLED,
        EXCEPTION_ON_SCRIPT_ERROR,
        EXCEPTION_ON_FAILING_STATUS,
        PRINT_CONTENT_ON_FAILING_STATUS,
        CSS_ENABLED,
        DO_NOT_TRACK_ENABLED,
        JAVASCRIPT_ENABLED,
        USE_INSECURE_SSL,
        REDIRECT_ENABLED,
        COOKIES_ENABLED,
        CLEAR_COOKIES,
        CLEAR_EXPIRED_COOKIES,
        INCLUDE_HEADERS,
        INCLUDE_METADATA,
        NETWORK_TIMEOUT,
        JAVASCRIPT_TIMEOUT,
        MAX_CONTENT_LENGTH
    };
    
    // Default name of property file.  Also used as System property key.
    public static final String URL2TEXT_PROPERTIES = "url2text.properties";
    
    // other constants
    public static final String UTF_8 = "UTF-8";

    // Supported output formats
    public enum OutputFormat
    {
        PLAIN, JSON
    };

}
