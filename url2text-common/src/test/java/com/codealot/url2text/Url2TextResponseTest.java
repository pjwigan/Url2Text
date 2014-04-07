package com.codealot.url2text;

import static com.codealot.url2text.Constants.CONTENT_CHARSET;
import static com.codealot.url2text.Constants.CONTENT_LENGTH;
import static com.codealot.url2text.Constants.CONTENT_METADATA;
import static com.codealot.url2text.Constants.CONTENT_TYPE;
import static com.codealot.url2text.Constants.CONVERSION_TIME;
import static com.codealot.url2text.Constants.CONVERTED_TEXT;
import static com.codealot.url2text.Constants.ETAG;
import static com.codealot.url2text.Constants.FETCH_TIME;
import static com.codealot.url2text.Constants.INT_NOT_SET;
import static com.codealot.url2text.Constants.LANDING_PAGE;
import static com.codealot.url2text.Constants.LAST_MODIFIED;
import static com.codealot.url2text.Constants.LONG_NOT_SET;
import static com.codealot.url2text.Constants.STR_NOT_SET;
import static com.codealot.url2text.Constants.REQUEST_PAGE;
import static com.codealot.url2text.Constants.RESPONSE_HEADERS;
import static com.codealot.url2text.Constants.STATUS;
import static com.codealot.url2text.Constants.STATUS_MESSAGE;
import static com.codealot.url2text.Constants.TRANSACTION_METADATA;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import com.codealot.url2text.Constants.OutputFormat;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Url2TextResponseTest
{

    private ObjectMapper mapper = new ObjectMapper();
    private Url2TextResponse response = new Url2TextResponse();
    private ArrayList<NameAndValue> namesAndValues = new ArrayList<>();

    @Before
    public void setUp() throws Exception
    {
        this.namesAndValues.add(new NameAndValue("key1", "value1"));
    }

    @Test
    public void testAsFormat() throws Url2TextException
    {
        // just check the type returned
        String s = this.response.asFormat(OutputFormat.PLAIN);
        assertTrue(s.startsWith("####"));

        String j = this.response.asFormat(OutputFormat.JSON);
        assertTrue(j.startsWith("{\"Transaction"));
    }

    @Test
    public void testToJson() throws Exception
    {
        // check all fields are included
        this.response.setContentMetadata(namesAndValues);
        this.response.setResponseHeaders(namesAndValues);

        JsonNode root = mapper.readTree(this.response.toJson());
        // top level objects
        assertTrue(root.has(TRANSACTION_METADATA));
        assertTrue(root.has(RESPONSE_HEADERS));
        assertTrue(root.has(CONTENT_METADATA));
        assertTrue(root.has(CONVERTED_TEXT));

        JsonNode tm = root.get(TRANSACTION_METADATA);
        assertTrue(tm.has(REQUEST_PAGE));
        assertTrue(tm.has(LANDING_PAGE));
        assertTrue(tm.has(STATUS));
        assertTrue(tm.has(STATUS_MESSAGE));
        assertTrue(tm.has(FETCH_TIME));
        assertTrue(tm.has(CONTENT_TYPE));
        assertTrue(tm.has(CONTENT_CHARSET));
        assertTrue(tm.has(CONTENT_LENGTH));
        assertTrue(tm.has(ETAG));
        assertTrue(tm.has(LAST_MODIFIED));
        assertTrue(tm.has(CONVERSION_TIME));

        JsonNode rh = root.get(RESPONSE_HEADERS);
        assertTrue(rh.has("key1"));

        JsonNode cm = root.get(CONTENT_METADATA);
        assertTrue(cm.has("key1"));
    }

    @Test
    public void testFromJson() throws Exception
    {
        this.response.setContentCharset("charset");
        this.response.setContentLength(1000);
        this.response.setContentType("type");
        this.response.setConversionTime(100);
        this.response.setConvertedText("text");
        this.response.setEtag("etag");
        this.response.setFetchTime(100);
        this.response.setLandingPage("landing page");
        this.response.setLastModified("last modified");
        this.response.setRequestPage("request page");
        this.response.setStatus(200);
        this.response.setStatusMessage("message");
        this.response.setResponseHeaders(namesAndValues);
        this.response.setContentMetadata(namesAndValues);

        Url2TextResponse r2 = new Url2TextResponse(this.response.toJson());
        assertEquals(this.response, r2);
    }

    @Test
    public void testEquals()
    {
        Url2TextResponse r2 = new Url2TextResponse();
        assertEquals(this.response, r2);
        this.response.setContentLength(100);
        assertNotEquals(this.response, r2);
    }

    @Test
    public void testHashCode()
    {
        Url2TextResponse r2 = new Url2TextResponse();
        assertEquals(this.response.hashCode(), r2.hashCode());
        this.response.setContentLength(100);
        assertNotEquals(this.response.hashCode(), r2.hashCode());
    }

    @Test
    public void testToString() throws Exception
    {
        // check all fields are included
        this.response.setContentMetadata(namesAndValues);
        this.response.setResponseHeaders(namesAndValues);
        String jsonString = this.response.toJson();

        assertTrue(jsonString.contains(TRANSACTION_METADATA));
        assertTrue(jsonString.contains(RESPONSE_HEADERS));
        assertTrue(jsonString.contains(CONTENT_METADATA));
        assertTrue(jsonString.contains(CONVERTED_TEXT));
        assertTrue(jsonString.contains(REQUEST_PAGE));
        assertTrue(jsonString.contains(LANDING_PAGE));
        assertTrue(jsonString.contains(STATUS));
        assertTrue(jsonString.contains(STATUS_MESSAGE));
        assertTrue(jsonString.contains(FETCH_TIME));
        assertTrue(jsonString.contains(CONTENT_TYPE));
        assertTrue(jsonString.contains(CONTENT_CHARSET));
        assertTrue(jsonString.contains(CONTENT_LENGTH));
        assertTrue(jsonString.contains(ETAG));
        assertTrue(jsonString.contains(CONVERSION_TIME));
        assertTrue(jsonString.contains("key1"));
    }

    @Test
    public void testSetRequestPage()
    {
        // check initially STR_NOT_SET, then "", then "value".
        assertEquals(STR_NOT_SET, this.response.getRequestPage());
        this.response.setRequestPage(null);
        assertEquals("", this.response.getRequestPage());
        this.response.setRequestPage("");
        assertEquals("", this.response.getRequestPage());
        this.response.setRequestPage("value");
        assertEquals("value", this.response.getRequestPage());
    }

    @Test
    public void testSetLandingPage()
    {
        // check initially STR_NOT_SET, then "", then "value".
        assertEquals(STR_NOT_SET, this.response.getLandingPage());
        this.response.setLandingPage(null);
        assertEquals("", this.response.getLandingPage());
        this.response.setLandingPage("");
        assertEquals("", this.response.getLandingPage());
        this.response.setLandingPage("value");
        assertEquals("value", this.response.getLandingPage());
    }

    @Test
    public void testSetStatus()
    {
        // check initially INT_NOT_SET, then 0, then number
        assertEquals(INT_NOT_SET, this.response.getStatus());
        this.response.setStatus(0);
        assertEquals(0, this.response.getStatus());
        this.response.setStatus(1000);
        assertEquals(1000, this.response.getStatus());
    }

    @Test
    public void testSetStatusMessage()
    {
        // check initially STR_NOT_SET, then "", then "value".
        assertEquals(STR_NOT_SET, this.response.getStatusMessage());
        this.response.setStatusMessage(null);
        assertEquals("", this.response.getStatusMessage());
        this.response.setStatusMessage("");
        assertEquals("", this.response.getStatusMessage());
        this.response.setStatusMessage("value");
        assertEquals("value", this.response.getStatusMessage());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetFetchTimeNegative()
    {
        this.response.setFetchTime(-1);
    }

    @Test
    public void testSetFetchTime()
    {
        // check initially LONG_NOT_SET, then 0, then number
        assertEquals(LONG_NOT_SET, this.response.getFetchTime());
        this.response.setFetchTime(0);
        assertEquals(0, this.response.getFetchTime());
        this.response.setFetchTime(1000);
        assertEquals(1000, this.response.getFetchTime());
    }

    @Test
    public void testSetContentType()
    {
        // check initially STR_NOT_SET, then "", then "value".
        assertEquals(STR_NOT_SET, this.response.getContentType());
        this.response.setContentType(null);
        assertEquals("", this.response.getContentType());
        this.response.setContentType("");
        assertEquals("", this.response.getContentType());
        this.response.setContentType("value");
        assertEquals("value", this.response.getContentType());
    }

    @Test
    public void testSetContentCharset()
    {
        // check initially STR_NOT_SET, then "", then "value".
        assertEquals(STR_NOT_SET, this.response.getContentCharset());
        this.response.setContentCharset(null);
        assertEquals("", this.response.getContentCharset());
        this.response.setContentCharset("");
        assertEquals("", this.response.getContentCharset());
        this.response.setContentCharset("value");
        assertEquals("value", this.response.getContentCharset());
    }

    @Test
    public void testSetConversionTime()
    {
        // check initially 0L, then number
        assertEquals(0L, this.response.getConversionTime());
        this.response.setConversionTime(1000);
        assertEquals(1000, this.response.getConversionTime());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetConversionTimeNegative()
    {
        this.response.setConversionTime(-1);
    }

    @Test
    public void testSetEtag()
    {
        // check initially STR_NOT_SET, then "", then "value".
        assertEquals(STR_NOT_SET, this.response.getEtag());
        this.response.setEtag(null);
        assertEquals("", this.response.getEtag());
        this.response.setEtag("");
        assertEquals("", this.response.getEtag());
        this.response.setEtag("value");
        assertEquals("value", this.response.getEtag());
    }

    @Test
    public void testSetLastModified()
    {
        // check initially STR_NOT_SET, then "", then "value".
        assertEquals(STR_NOT_SET, this.response.getLastModified());
        this.response.setLastModified(null);
        assertEquals("", this.response.getLastModified());
        this.response.setLastModified("");
        assertEquals("", this.response.getLastModified());
        this.response.setLastModified("value");
        assertEquals("value", this.response.getLastModified());
    }

    @Test
    public void testSetContentLengthLong()
    {
        // check initially LONG_NOT_SET, then 0, then number
        assertEquals(LONG_NOT_SET, this.response.getContentLength());
        this.response.setContentLength(0);
        assertEquals(0, this.response.getContentLength());
        this.response.setContentLength(1000);
        assertEquals(1000, this.response.getContentLength());

    }

    @Test
    public void testSetContentLengthString()
    {
        // check null == "" == 0L, then number
        assertEquals(LONG_NOT_SET, this.response.getContentLength());
        this.response.setContentLength(null);
        assertEquals(0L, this.response.getContentLength());
        this.response.setContentLength("");
        assertEquals(0L, this.response.getContentLength());
        this.response.setContentLength("10000");
        assertEquals(10000L, this.response.getContentLength());
    }

    @Test
    public void testSetResponseHeaders()
    {
        // check null == empty list
        assertTrue(this.response.getResponseHeaders().size() == 0);
        this.response.setResponseHeaders(null);
        assertTrue(this.response.getResponseHeaders().size() == 0);
        this.response.setResponseHeaders(namesAndValues);
        assertTrue(namesAndValues == this.response.getResponseHeaders());
    }

    @Test
    public void testSetContentMetadata()
    {
        // check null == empty list
        assertTrue(this.response.getContentMetadata().size() == 0);
        this.response.setContentMetadata(null);
        assertTrue(this.response.getContentMetadata().size() == 0);
        this.response.setContentMetadata(namesAndValues);
        assertTrue(namesAndValues == this.response.getContentMetadata());
    }

    @Test
    public void testSetConvertedText()
    {
        // check STR_NOT_SET then value
        assertEquals(STR_NOT_SET, this.response.getConvertedText());
        this.response.setConvertedText("value");
        assertEquals("value", this.response.getConvertedText());
    }

}
