package com.codealot.url2text;

import static com.codealot.url2text.Constants.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
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
@SuppressWarnings("serial")
public class Url2TextResponse implements Serializable
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

    // resulting text
    private String convertedText = STR_NOT_SET;

    // constructor
    public Url2TextResponse()
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
    public Url2TextResponse(final String json) throws JsonProcessingException,
            IOException
    {

        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode rootNode = mapper.readTree(json);

        final JsonNode transactionNode = rootNode.get(TRANSACTION_METADATA);
        this.requestPage = transactionNode.get(REQUEST_PAGE).textValue();
        this.landingPage = transactionNode.get(LANDING_PAGE).textValue();
        this.status = transactionNode.get(STATUS).asInt();
        this.statusMessage = transactionNode.get(STATUS_MESSAGE).textValue();
        this.fetchTime = transactionNode.get(FETCH_TIME).asLong();
        this.contentType = transactionNode.get(CONTENT_TYPE).textValue();
        this.contentCharset = transactionNode.get(CONTENT_CHARSET).textValue();
        this.contentLength = transactionNode.get(CONTENT_LENGTH).asLong();
        this.etag = transactionNode.get(ETAG).textValue();
        this.lastModified = transactionNode.get(LAST_MODIFIED).textValue();
        this.conversionTime = transactionNode.get(CONVERSION_TIME).asLong();

        final JsonNode headersNode = rootNode.get(RESPONSE_HEADERS);
        for (final Iterator<String> i = headersNode.fieldNames(); i.hasNext();)
        {
            final String key = i.next();
            final String value = headersNode.get(key).textValue();
            this.responseHeaders.add(new NameAndValue(key, value));
        }

        final JsonNode metadataNode = rootNode.get(CONTENT_METADATA);
        for (final Iterator<String> i = metadataNode.fieldNames(); i.hasNext();)
        {
            final String key = i.next();
            final String value = metadataNode.get(key).textValue();
            this.contentMetadata.add(new NameAndValue(key, value));
        }

        this.convertedText = rootNode.get(CONVERTED_TEXT).textValue();
    }

    /**
     * Renders this object in the given format.
     * 
     * @return
     * @throws Url2TextException
     */
    public String asFormat(final OutputFormat format) throws Url2TextException
    {
        if (format == OutputFormat.PLAIN)
        {
            return this.toString();
        }
        else if (format == OutputFormat.JSON)
        {
            return this.toJson();
        }
        throw new IllegalArgumentException("Format " + format
                + " not supported.");
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(
                this.status, 
                this.statusMessage, 
                this.fetchTime,
                this.contentLength, 
                this.conversionTime, 
                this.requestPage,
                this.landingPage, 
                this.contentType, 
                this.contentCharset,
                this.etag, 
                this.lastModified, 
                this.responseHeaders,
                this.contentMetadata, 
                this.convertedText);
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
        final Url2TextResponse test = (Url2TextResponse) obj;
        // all content is included in toString(), so this is safe.
        return test.toString().equals(this.toString());
    }

    /**
     * Renders this object as JSON.
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
            jsonGenerator.writeFieldName(TRANSACTION_METADATA);
            jsonGenerator.writeStartObject();

            jsonGenerator.writeStringField(REQUEST_PAGE, this.requestPage);
            jsonGenerator.writeStringField(LANDING_PAGE, this.landingPage);
            jsonGenerator.writeNumberField(STATUS, this.status);
            jsonGenerator.writeStringField(STATUS_MESSAGE, this.statusMessage);
            jsonGenerator.writeNumberField(FETCH_TIME, this.fetchTime);
            jsonGenerator.writeStringField(CONTENT_TYPE, this.contentType);
            jsonGenerator
                    .writeStringField(CONTENT_CHARSET, this.contentCharset);
            jsonGenerator.writeNumberField(CONTENT_LENGTH, this.contentLength);
            jsonGenerator.writeStringField(ETAG, this.etag);
            jsonGenerator.writeStringField(LAST_MODIFIED, this.lastModified);
            jsonGenerator
                    .writeNumberField(CONVERSION_TIME, this.conversionTime);

            jsonGenerator.writeEndObject();

            // response headers
            if (!this.responseHeaders.isEmpty())
            {
                outputNameAndValueArray(jsonGenerator, RESPONSE_HEADERS,
                        this.responseHeaders);
            }

            // content metadata
            if (!this.contentMetadata.isEmpty())
            {
                outputNameAndValueArray(jsonGenerator, CONTENT_METADATA,
                        this.contentMetadata);
            }

            // text
            jsonGenerator.writeStringField(CONVERTED_TEXT, this.convertedText);
        }
        catch (IOException e)
        {
            throw new Url2TextException("Error emitting JSON", e);
        }

        try
        {
            return destination.toString(UTF_8);
        }
        catch (UnsupportedEncodingException e)
        {
            throw new Url2TextException(e);
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
     */
    @Override
    public String toString()
    {
        final StringBuilder buffer = new StringBuilder(310);

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
        buffer.append(convertedText);
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

    public String getConvertedText()
    {
        return this.convertedText;
    }

    public void setConvertedText(final String convertedText)
    {
        this.convertedText = (convertedText == null) ? "" : convertedText;
    }

}
