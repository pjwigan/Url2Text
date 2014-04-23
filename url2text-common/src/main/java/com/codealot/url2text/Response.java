package com.codealot.url2text;

import static com.codealot.url2text.Constants.*;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Class to encapsulate the result of fetching and converting a url's content.
 * Consists of a bunch of properties plus asFormat(), toString() and toJson()
 * methods.
 * 
 * <p>
 * Note: getters will never return null.
 * 
 * <p>
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
public class Response implements Closeable, AutoCloseable
{
    // transaction metadata
    private String requestPage = STR_NOT_SET;
    private String landingPage = STR_NOT_SET;
    private int status = INT_NOT_SET;
    private String statusMessage = STR_NOT_SET;
    private long fetchTime = LONG_NOT_SET;
    private String contentType = STR_NOT_SET;
    private String contentCharset = STR_NOT_SET;
    private long contentLength = LONG_NOT_SET;
    private String etag = STR_NOT_SET;
    private String lastModified = STR_NOT_SET;
    private long conversionTime = 0L;

    // optional content
    private List<NameAndValue> responseHeaders = new ArrayList<>();
    private List<NameAndValue> contentMetadata = new ArrayList<>();

    // text vars
    private Reader textReader = new StringReader(STR_NOT_SET);
    private String text = null; // used to make getText(), toString() and
                                // toJson() repeatable.

    // operational flag
    private boolean textSupplied = false;

    // constructor
    public Response()
    {
        // default
    }

    /**
     * Constructor to build an instance from the output of {@link #toJson()}.
     * 
     * @param json
     * @throws IOException
     * @throws JsonProcessingException
     */
    public Response(final String json) throws JsonProcessingException,
            IOException
    {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode rootNode = mapper.readTree(json);

        final JsonNode transactionNode = rootNode.get(HDR_TRANSACTION_METADATA);
        this.requestPage = transactionNode.get(HDR_REQUEST_PAGE).textValue();
        this.landingPage = transactionNode.get(HDR_LANDING_PAGE).textValue();
        this.status = transactionNode.get(HDR_STATUS).asInt();
        this.statusMessage = transactionNode.get(HDR_STATUS_MESSAGE)
                .textValue();
        this.fetchTime = transactionNode.get(HDR_FETCH_TIME).asLong();
        this.contentType = transactionNode.get(HDR_CONTENT_TYPE).textValue();
        this.contentCharset = transactionNode.get(HDR_CONTENT_CHARSET)
                .textValue();
        this.contentLength = transactionNode.get(HDR_CONTENT_LENGTH).asLong();
        this.etag = transactionNode.get(HDR_ETAG).textValue();
        this.lastModified = transactionNode.get(HDR_LAST_MODIFIED).textValue();
        this.conversionTime = transactionNode.get(HDR_CONVERSION_TIME).asLong();

        final JsonNode headersNode = rootNode.get(HDR_RESPONSE_HEADERS);
        for (final Iterator<String> i = headersNode.fieldNames(); i.hasNext();)
        {
            final String key = i.next();
            final String value = headersNode.get(key).textValue();
            this.responseHeaders.add(new NameAndValue(key, value));
        }

        final JsonNode metadataNode = rootNode.get(HDR_CONTENT_METADATA);
        for (final Iterator<String> i = metadataNode.fieldNames(); i.hasNext();)
        {
            final String key = i.next();
            final String value = metadataNode.get(key).textValue();
            this.contentMetadata.add(new NameAndValue(key, value));
        }

        this.textReader = new StringReader(rootNode.get(HDR_CONVERTED_TEXT)
                .textValue());
    }

    /**
     * Renders this object in the given format.
     * 
     * @return
     * @throws Url2TextException
     */
    public String asFormat(final OutputFormat format) throws Url2TextException
    {
        String result = null;
        if (format == OutputFormat.PLAIN)
        {
            result = this.toString();
        }
        else if (format == OutputFormat.JSON)
        {
            result = this.toJson();
        }
        else
        {
            throw new IllegalArgumentException("Format " + format
                    + " not supported.");
        }
        return result;
    }

    /**
     * Closes the text Reader.
     * 
     * @throws IOException
     */
    @Override
    public void close() throws IOException
    {
        if (this.textReader != null)
        {
            this.textReader.close();
        }
    }

    @Override
    public int hashCode()
    {
        try
        {
            getTextFromReader();
        }
        catch (Url2TextException e)
        {
            throw new RuntimeException(e);
        }
        return Objects.hash(this.status, this.statusMessage, this.fetchTime,
                this.contentLength, this.conversionTime, this.requestPage,
                this.landingPage, this.contentType, this.contentCharset,
                this.etag, this.lastModified, this.responseHeaders,
                this.contentMetadata, this.text);
    }

    @Override
    public boolean equals(final Object obj)
    {
        boolean result = false;
        if (this == obj)
        {
            result = true;
        }
        else if (obj == null || obj.getClass() != this.getClass())
        {
            result = false;
        }
        else
        {
            final Response test = (Response) obj;
            // all content is included in toString(), so this is safe.
            result = test.toString().equals(this.toString());
        }
        return result;
    }

    /**
     * Renders this object as JSON.
     * <p>
     * Beware. This method consumes the internal Reader, creating a buffer of
     * unlimited size.
     * 
     * @return
     * @throws Url2TextException
     */
    public String toJson() throws Url2TextException
    {
        final JsonFactory jFactory = new JsonFactory();
        final ByteArrayOutputStream destination = new ByteArrayOutputStream();

        try (final JsonGenerator jsonGenerator = jFactory
                .createGenerator(destination);)
        {
            jsonGenerator.writeStartObject();

            // transaction metadata
            jsonGenerator.writeFieldName(HDR_TRANSACTION_METADATA);
            jsonGenerator.writeStartObject();

            jsonGenerator.writeStringField(HDR_REQUEST_PAGE, this.requestPage);
            jsonGenerator.writeStringField(HDR_LANDING_PAGE, this.landingPage);
            jsonGenerator.writeNumberField(HDR_STATUS, this.status);
            jsonGenerator.writeStringField(HDR_STATUS_MESSAGE,
                    this.statusMessage);
            jsonGenerator.writeNumberField(HDR_FETCH_TIME, this.fetchTime);
            jsonGenerator.writeStringField(HDR_CONTENT_TYPE, this.contentType);
            jsonGenerator.writeStringField(HDR_CONTENT_CHARSET,
                    this.contentCharset);
            jsonGenerator.writeNumberField(HDR_CONTENT_LENGTH,
                    this.contentLength);
            jsonGenerator.writeStringField(HDR_ETAG, this.etag);
            jsonGenerator
                    .writeStringField(HDR_LAST_MODIFIED, this.lastModified);
            jsonGenerator.writeNumberField(HDR_CONVERSION_TIME,
                    this.conversionTime);

            jsonGenerator.writeEndObject();

            // response headers
            if (!this.responseHeaders.isEmpty())
            {
                outputNameAndValueArray(jsonGenerator, HDR_RESPONSE_HEADERS,
                        this.responseHeaders);
            }

            // content metadata
            if (!this.contentMetadata.isEmpty())
            {
                outputNameAndValueArray(jsonGenerator, HDR_CONTENT_METADATA,
                        this.contentMetadata);
            }

            // text
            jsonGenerator.writeStringField(HDR_CONVERTED_TEXT, this.getText());
            jsonGenerator.writeEndObject();
            jsonGenerator.close();

            String result = destination.toString(UTF_8);
            return result;
        }
        catch (IOException e)
        {
            throw new Url2TextException("Error emitting JSON", e);
        }
    }

    /**
     * Convenience method for writing header and metadata lists.
     * 
     * @param jsonGenerator
     * @param header
     * @param array
     * @throws JsonGenerationException
     * @throws IOException
     */
    private void outputNameAndValueArray(final JsonGenerator jsonGenerator,
            final String header, final List<NameAndValue> array)
            throws JsonGenerationException, IOException
    {
        jsonGenerator.writeFieldName(header);
        jsonGenerator.writeStartObject();

        for (final NameAndValue nameAndValue : array)
        {
            jsonGenerator.writeStringField(nameAndValue.getName(),
                    nameAndValue.getValue());
        }
        jsonGenerator.writeEndObject();
    }

    /**
     * Full dump of the response content in plain text format.
     * <p>
     * Beware. This method consumes the internal Reader, creating a buffer of
     * unlimited size.
     */
    @Override
    public String toString()
    {
        final StringBuilder buffer = new StringBuilder(350);

        buffer.append("################ TRANSACTION METADATA ################");
        buffer.append("\nRequest page   : ").append(this.requestPage);
        buffer.append("\nLanding page   : ").append(this.landingPage);
        buffer.append("\nStatus         : ").append(this.status).append(' ')
                .append(this.statusMessage);
        buffer.append("\nFetch time     : ").append(this.fetchTime)
                .append(" ms");
        buffer.append("\nContent type   : ").append(this.contentType);
        buffer.append("\nContent charset: ").append(this.contentCharset);
        buffer.append("\nContent length : ").append(this.contentLength);
        buffer.append("\nEtag           : ").append(this.etag);
        buffer.append("\nLast Modified  : ").append(this.lastModified);
        buffer.append("\nConvert time   : ").append(this.conversionTime)
                .append(" ms\n\n");

        if (!responseHeaders.isEmpty())
        {
            buffer.append("################ RESPONSE HEADERS ####################\n");
            for (final NameAndValue kvp : responseHeaders)
            {
                buffer.append(kvp.getName()).append(" = ")
                        .append(kvp.getValue()).append('\n');
            }
            buffer.append('\n');
        }

        if (!contentMetadata.isEmpty())
        {
            buffer.append("################ CONTENT METADATA ####################\n");

            for (final NameAndValue kvp : contentMetadata)
            {
                buffer.append(kvp.getName()).append(" = ")
                        .append(kvp.getValue()).append('\n');
            }
            buffer.append('\n');
        }
        buffer.append("################ CONVERTED TEXT ######################\n");
        try
        {
            buffer.append(this.getText());
        }
        catch (Url2TextException e)
        {
            throw new RuntimeException(e);
        }
        buffer.append('\n');

        return buffer.toString();
    }

    public String getRequestPage()
    {
        return this.requestPage;
    }

    public void setRequestPage(final String requestPage)
    {
        this.requestPage = (requestPage == null) ? "" : requestPage;
    }

    public String getLandingPage()
    {
        return this.landingPage;
    }

    public void setLandingPage(final String landingPage)
    {
        this.landingPage = (landingPage == null) ? "" : landingPage;
    }

    public int getStatus()
    {
        return this.status;
    }

    public void setStatus(final int status)
    {
        this.status = status;
    }

    public String getStatusMessage()
    {
        return this.statusMessage;
    }

    public void setStatusMessage(final String statusMessage)
    {
        this.statusMessage = (statusMessage == null) ? "" : statusMessage;
    }

    public long getFetchTime()
    {
        return this.fetchTime;
    }

    public void setFetchTime(final long fetchTime)
    {
        if (fetchTime < 0L)
        {
            throw new IllegalArgumentException("Fetch time cannot be negative.");
        }
        this.fetchTime = fetchTime;
    }

    public String getContentType()
    {
        return this.contentType;
    }

    public void setContentType(final String contentType)
    {
        this.contentType = (contentType == null) ? "" : contentType;
    }

    public String getContentCharset()
    {
        return this.contentCharset;
    }

    public void setContentCharset(final String contentCharset)
    {
        this.contentCharset = (contentCharset == null) ? "" : contentCharset;
    }

    public long getConversionTime()
    {
        return this.conversionTime;
    }

    public void setConversionTime(final long conversionTime)
    {
        if (conversionTime < 0L)
        {
            throw new IllegalArgumentException(
                    "Conversion time cannot be negative.");
        }
        this.conversionTime = conversionTime;
    }

    public String getEtag()
    {
        return this.etag;
    }

    public void setEtag(final String etag)
    {
        this.etag = (etag == null) ? "" : etag;
    }

    public String getLastModified()
    {
        return lastModified;
    }

    public void setLastModified(final String lastModified)
    {
        this.lastModified = (lastModified == null) ? "" : lastModified;
    }

    public long getContentLength()
    {
        return this.contentLength;
    }

    public void setContentLength(final long contentLength)
    {
        this.contentLength = contentLength;
    }

    /**
     * Convenience method which interprets null and empty string as zero.
     * 
     * @param contentLength
     */
    public void setContentLength(final String contentLength)
    {
        if (contentLength == null || contentLength.length() == 0)
        {
            this.setContentLength(0L);
        }
        else
        {
            this.setContentLength(Long.parseLong(contentLength));
        }
    }

    public List<NameAndValue> getResponseHeaders()
    {
        return this.responseHeaders;
    }

    public void setResponseHeaders(final List<NameAndValue> responseHeaders)
    {
        this.responseHeaders = (responseHeaders == null) ? new ArrayList<NameAndValue>()
                : responseHeaders;
    }

    public List<NameAndValue> getContentMetadata()
    {
        return this.contentMetadata;
    }

    public void setContentMetadata(final List<NameAndValue> conversionMetadata)
    {
        this.contentMetadata = (conversionMetadata == null) ? new ArrayList<NameAndValue>()
                : conversionMetadata;
    }

    /**
     * Consumes the Reader, which is then closed.
     * <p>
     * Beware. This method consumes the internal Reader, creating a buffer of
     * unlimited size.
     * 
     * @return
     * @throws Url2TextException
     */
    public String getText() throws Url2TextException
    {
        if (this.text == null)
        {
            this.getTextFromReader();
        }
        return this.text;
    }

    public Reader getTextReader()
    {
        if (this.text == null)
        {
            return this.textReader;
        }
        else
        {
            return new StringReader(this.text);
        }
    }

    public void setTextReader(final Reader reader)
    {
        Objects.requireNonNull(reader, "No Reader supplied.");
        if (this.textSupplied)
        {
            throw new IllegalStateException("Text or Reader already supplied.");
        }
        this.textReader = reader;
        this.text = null;
        this.textSupplied = true;
    }

    private void getTextFromReader() throws Url2TextException
    {
        if (this.textReader == null)
        {
            return;
        }
        final char[] arr = new char[8 * 1024]; // 8K at a time
        final StringBuilder buf = new StringBuilder();
        int numChars;

        try
        {
            while ((numChars = textReader.read(arr, 0, arr.length)) > 0)
            {
                buf.append(arr, 0, numChars);
            }
            this.text = buf.toString();
            this.textReader.close();
            this.textReader = null;
        }
        catch (IOException e)
        {
            throw new Url2TextException(e);
        }
    }

}
