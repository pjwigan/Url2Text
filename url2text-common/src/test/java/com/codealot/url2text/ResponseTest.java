package com.codealot.url2text;

import static com.codealot.url2text.Constants.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ResponseTest
{

    private final ObjectMapper mapper = new ObjectMapper();
    // private final Response response = new Response();
    private final List<NameAndValue> namesAndValues = new ArrayList<>();

    @Before
    public void setUp() throws Exception
    {
        this.namesAndValues.add(new NameAndValue("key1", "value1"));
    }

    @Test
    public void testAsFormat() throws Url2TextException, IOException
    {
        try (final Response response = new Response())
        {
            // just check the type returned
            final String s = response.asFormat(OutputFormat.PLAIN);
            assertTrue(s.startsWith("####"));

            final String j = response.asFormat(OutputFormat.JSON);
            assertTrue(j.startsWith("{\"Transaction"));
        }
    }

    @Test
    public void testToJson() throws Exception
    {
        try (final Response response = new Response())
        {
            // check all fields are included
            response.setContentMetadata(namesAndValues);
            response.setResponseHeaders(namesAndValues);
            
            String jsonString = response.toJson();

            final JsonNode root = mapper.readTree(jsonString);

            // top level objects
            assertTrue(root.has(HDR_TRANSACTION_METADATA));
            assertTrue(root.has(HDR_RESPONSE_HEADERS));
            assertTrue(root.has(HDR_CONTENT_METADATA));
            assertTrue(root.has(HDR_CONVERTED_TEXT));

            final JsonNode tm = root.get(HDR_TRANSACTION_METADATA);
            assertTrue(tm.has(HDR_REQUEST_PAGE));
            assertTrue(tm.has(HDR_LANDING_PAGE));
            assertTrue(tm.has(HDR_STATUS));
            assertTrue(tm.has(HDR_STATUS_MESSAGE));
            assertTrue(tm.has(HDR_FETCH_TIME));
            assertTrue(tm.has(HDR_CONTENT_TYPE));
            assertTrue(tm.has(HDR_CONTENT_CHARSET));
            assertTrue(tm.has(HDR_CONTENT_LENGTH));
            assertTrue(tm.has(HDR_ETAG));
            assertTrue(tm.has(HDR_LAST_MODIFIED));
            assertTrue(tm.has(HDR_CONVERSION_TIME));

            final JsonNode rh = root.get(HDR_RESPONSE_HEADERS);
            assertTrue(rh.has("key1"));

            final JsonNode cm = root.get(HDR_CONTENT_METADATA);
            assertTrue(cm.has("key1"));
        }
    }

    @Test
    public void testFromJson() throws Exception
    {
        try (final Response response = new Response())
        {
            response.setContentCharset("charset");
            response.setContentLength(1000);
            response.setContentType("type");
            response.setConversionTime(100);
            response.setTextReader(new StringReader("text"));
            response.setEtag("etag");
            response.setFetchTime(100);
            response.setLandingPage("landing page");
            response.setLastModified("last modified");
            response.setRequestPage("request page");
            response.setStatus(200);
            response.setStatusMessage("message");
            response.setResponseHeaders(namesAndValues);
            response.setContentMetadata(namesAndValues);

            try (final Response r2 = new Response(
                    response.toJson()))
            {
                assertEquals(response, r2);
            }
        }
    }

    @Test
    public void testEquals() throws IOException
    {
        try (final Response response = new Response();
                final Response r2 = new Response())
        {
            assertEquals(response, r2);
            response.setContentLength(100);
            assertNotEquals(response, r2);
        }
    }

    @Test
    public void testHashCode() throws IOException
    {
        try (final Response response = new Response();
                final Response r2 = new Response())
        {
            assertEquals(response.hashCode(), r2.hashCode());
            response.setContentLength(100);
            assertNotEquals(response.hashCode(), r2.hashCode());
        }
    }

    @Test
    public void testToString() throws Exception
    {
        try (final Response response = new Response())
        {
            // check all fields are included
            response.setContentMetadata(namesAndValues);
            response.setResponseHeaders(namesAndValues);
            final String string = response.toString();

            assertTrue(string.contains("################ TRANSACTION METADATA ################"));
            assertTrue(string.contains("################ RESPONSE HEADERS ####################"));
            assertTrue(string.contains("################ CONTENT METADATA ####################"));
            assertTrue(string.contains("################ CONVERTED TEXT ######################"));
            assertTrue(string.contains("Request page"));
            assertTrue(string.contains("Landing page"));
            assertTrue(string.contains("Status"));
            assertTrue(string.contains("Fetch time"));
            assertTrue(string.contains("Content type"));
            assertTrue(string.contains("Content charset"));
            assertTrue(string.contains("Content length"));
            assertTrue(string.contains("Etag"));
            assertTrue(string.contains("Convert time"));
            assertTrue(string.contains("key1"));
        }
    }

    @Test
    public void testSetRequestPage() throws IOException
    {
        try (final Response response = new Response())
        {
            // check initially STR_NOT_SET, then "", then "value".
            assertEquals(STR_NOT_SET, response.getRequestPage());
            response.setRequestPage(null);
            assertEquals("", response.getRequestPage());
            response.setRequestPage("");
            assertEquals("", response.getRequestPage());
            response.setRequestPage("value");
            assertEquals("value", response.getRequestPage());
        }
    }

    @Test
    public void testSetLandingPage() throws IOException
    {
        try (final Response response = new Response())
        {
            // check initially STR_NOT_SET, then "", then "value".
            assertEquals(STR_NOT_SET, response.getLandingPage());
            response.setLandingPage(null);
            assertEquals("", response.getLandingPage());
            response.setLandingPage("");
            assertEquals("", response.getLandingPage());
            response.setLandingPage("value");
            assertEquals("value", response.getLandingPage());
        }
    }

    @Test
    public void testSetStatus() throws IOException
    {
        try (final Response response = new Response())
        {
            // check initially INT_NOT_SET, then 0, then number
            assertEquals(INT_NOT_SET, response.getStatus());
            response.setStatus(0);
            assertEquals(0, response.getStatus());
            response.setStatus(1000);
            assertEquals(1000, response.getStatus());
        }
    }

    @Test
    public void testSetStatusMessage() throws IOException
    {
        try (final Response response = new Response())
        {
            // check initially STR_NOT_SET, then "", then "value".
            assertEquals(STR_NOT_SET, response.getStatusMessage());
            response.setStatusMessage(null);
            assertEquals("", response.getStatusMessage());
            response.setStatusMessage("");
            assertEquals("", response.getStatusMessage());
            response.setStatusMessage("value");
            assertEquals("value", response.getStatusMessage());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetFetchTimeNegative() throws IOException
    {
        try (final Response response = new Response())
        {
            response.setFetchTime(-1);
        }
    }

    @Test
    public void testSetFetchTime() throws IOException
    {
        try (final Response response = new Response())
        {
            // check initially LONG_NOT_SET, then 0, then number
            assertEquals(LONG_NOT_SET, response.getFetchTime());
            response.setFetchTime(0);
            assertEquals(0, response.getFetchTime());
            response.setFetchTime(1000);
            assertEquals(1000, response.getFetchTime());
        }
    }

    @Test
    public void testSetContentType() throws IOException
    {
        try (final Response response = new Response())
        {
            // check initially STR_NOT_SET, then "", then "value".
            assertEquals(STR_NOT_SET, response.getContentType());
            response.setContentType(null);
            assertEquals("", response.getContentType());
            response.setContentType("");
            assertEquals("", response.getContentType());
            response.setContentType("value");
            assertEquals("value", response.getContentType());
        }
    }

    @Test
    public void testSetContentCharset() throws IOException
    {
        try (final Response response = new Response())
        {
            // check initially STR_NOT_SET, then "", then "value".
            assertEquals(STR_NOT_SET, response.getContentCharset());
            response.setContentCharset(null);
            assertEquals("", response.getContentCharset());
            response.setContentCharset("");
            assertEquals("", response.getContentCharset());
            response.setContentCharset("value");
            assertEquals("value", response.getContentCharset());
        }
    }

    @Test
    public void testSetConversionTime() throws IOException
    {
        try (final Response response = new Response())
        {
            // check initially 0L, then number
            assertEquals(0L, response.getConversionTime());
            response.setConversionTime(1000);
            assertEquals(1000, response.getConversionTime());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetConversionTimeNegative() throws IOException
    {
        try (final Response response = new Response())
        {
            response.setConversionTime(-1);
        }
    }

    @Test
    public void testSetEtag() throws IOException
    {
        try (final Response response = new Response())
        {
            // check initially STR_NOT_SET, then "", then "value".
            assertEquals(STR_NOT_SET, response.getEtag());
            response.setEtag(null);
            assertEquals("", response.getEtag());
            response.setEtag("");
            assertEquals("", response.getEtag());
            response.setEtag("value");
            assertEquals("value", response.getEtag());
        }
    }

    @Test
    public void testSetLastModified() throws IOException
    {
        try (final Response response = new Response())
        {
            // check initially STR_NOT_SET, then "", then "value".
            assertEquals(STR_NOT_SET, response.getLastModified());
            response.setLastModified(null);
            assertEquals("", response.getLastModified());
            response.setLastModified("");
            assertEquals("", response.getLastModified());
            response.setLastModified("value");
            assertEquals("value", response.getLastModified());
        }
    }

    @Test
    public void testSetContentLengthLong() throws IOException
    {
        try (final Response response = new Response())
        {
            // check initially LONG_NOT_SET, then 0, then number
            assertEquals(LONG_NOT_SET, response.getContentLength());
            response.setContentLength(0);
            assertEquals(0, response.getContentLength());
            response.setContentLength(1000);
            assertEquals(1000, response.getContentLength());
        }

    }

    @Test
    public void testSetContentLengthString() throws IOException
    {
        try (final Response response = new Response())
        {
            // check null == "" == 0L, then number
            assertEquals(LONG_NOT_SET, response.getContentLength());
            response.setContentLength(null);
            assertEquals(LONG_NOT_SET, response.getContentLength());
            response.setContentLength("");
            assertEquals(LONG_NOT_SET, response.getContentLength());
            response.setContentLength(0L);
            assertEquals(0L, response.getContentLength());
            response.setContentLength("10000");
            assertEquals(10000L, response.getContentLength());
        }
    }

    @Test
    public void testSetResponseHeaders() throws IOException
    {
        try (final Response response = new Response())
        {
            // check null == empty list
            assertEquals(0, response.getResponseHeaders().size());
            response.setResponseHeaders(null);
            assertEquals(0, response.getResponseHeaders().size());
            response.setResponseHeaders(namesAndValues);
            assertTrue(namesAndValues == response.getResponseHeaders());
        }
    }

    @Test
    public void testSetContentMetadata() throws IOException
    {
        try (final Response response = new Response())
        {
            // check null == empty list
            assertEquals(0, response.getContentMetadata().size());
            response.setContentMetadata(null);
            assertEquals(0, response.getContentMetadata().size());
            response.setContentMetadata(namesAndValues);
            assertTrue(namesAndValues == response.getContentMetadata());
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testSetTextRepeatReader() throws IOException, Url2TextException
    {
        try (final Response response = new Response())
        {
            response.setTextReader(new StringReader("value"));
            response.setTextReader(new StringReader("repeat"));
        }
    }

    @Test(expected = NullPointerException.class)
    public void testSetTextReaderNull() throws IOException, Url2TextException
    {
        try (final Response response = new Response())
        {
            response.setTextReader(null);
        }
    }
    
    @Test
    public void testSetTextReader() throws IOException, Url2TextException
    {
        try (final Response response = new Response())
        {
            response.setTextReader(new StringReader("value"));
            assertEquals("value", response.getText());
        }
    }
    
    @Test(expected = IllegalStateException.class)
    public void testSetTextReaderRepeatReader() throws IOException, Url2TextException
    {
        try (final Response response = new Response())
        {
            response.setTextReader(new StringReader("value"));
            response.setTextReader(new StringReader("repeat"));
        }
    }

}
