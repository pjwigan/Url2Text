package com.codealot.url2text;

import static com.codealot.url2text.Constants.CONTENT_METADATA;
import static com.codealot.url2text.Constants.IF_MODIFIED_SINCE;
import static com.codealot.url2text.Constants.IF_NONE_MATCH;
import static com.codealot.url2text.Constants.RESPONSE_HEADERS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.util.HashMap;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Url2TextFetchTextTest
{

    private static final String LOCAL_HOST = "http://localhost:8000/";
    private static final String REMOTE_HOST = "http://example.com";

    private static Process httpServer;

    private final ObjectMapper mapper = new ObjectMapper();
    private final Url2Text fetcher = new Url2Text();

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        final ProcessBuilder process = new ProcessBuilder("python", "-m",
                "SimpleHTTPServer", "8000");
        process.directory(new File("src/test/resources"));
        httpServer = process.start();
        // give it a moment to fire up
        Thread.sleep(1500);
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception
    {
        httpServer.destroy();
        // wait for stop
        Thread.sleep(1200);
    }

    @Test
    public void test404() throws Url2TextException
    {
        Url2TextResponse response = this.fetcher.contentAsText(LOCAL_HOST
                + "not-exists.html", null);
        assertEquals(404, response.getStatus());
    }

    @Test
    public void testLastModified() throws Url2TextException
    {
        Url2TextResponse response = this.fetcher.contentAsText(
                REMOTE_HOST, null);
        HashMap<String, String> additionalHeaders = new HashMap<>();
        additionalHeaders.put(IF_MODIFIED_SINCE, response.getLastModified());
        response = this.fetcher.contentAsText("http://example.com", additionalHeaders);
        assertEquals(304, response.getStatus());
    }

    @Test
    public void testEtag() throws Url2TextException
    {
        Url2TextResponse response = this.fetcher.contentAsText(
                REMOTE_HOST, null);
        assertEquals(200, response.getStatus());
        HashMap<String, String> additionalHeaders = new HashMap<>();
        additionalHeaders.put(IF_NONE_MATCH, response.getEtag());
        response = this.fetcher.contentAsText(REMOTE_HOST, additionalHeaders);
        assertEquals(304, response.getStatus());
    }

    @Test(expected = Url2TextException.class)
    public void testMaxLength() throws Url2TextException
    {
        this.fetcher.setMaxContentLength(10);
        this.fetcher.contentAsText(LOCAL_HOST + "plain-text.txt", null);
    }

    @Test(expected = Url2TextException.class)
    public void testEncryptedOdt() throws Url2TextException
    {
        this.fetcher.contentAsText(LOCAL_HOST + "encrypted.odt", null);
    }

    @Test
    public void testIncludeHeaders() throws Exception
    {
        this.fetcher.setIncludeHeaders(true);
        Url2TextResponse response = this.fetcher.contentAsText(LOCAL_HOST
                + "plain-text.txt", null);
        assertEquals(200, response.getStatus());
        JsonNode root = this.mapper.readTree(response.toJson());
        assertTrue(root.has(RESPONSE_HEADERS));
    }

    @Test
    public void testIncludeMetadata() throws Exception
    {
        this.fetcher.setIncludeMetadata(true);
        Url2TextResponse response = this.fetcher.contentAsText(LOCAL_HOST
                + "binary.odt", null);
        assertEquals(200, response.getStatus());
        JsonNode root = this.mapper.readTree(response.toJson());
        assertTrue(root.has(CONTENT_METADATA));
    }

    @Test
    public void testFetchTextPlain() throws Url2TextException
    {
        Url2TextResponse response = this.fetcher.contentAsText(LOCAL_HOST
                + "plain-text.txt", null);
        assertEquals(200, response.getStatus());
        assertTrue(response.getConvertedText().contains(
                "Just a plain text file."));
    }

    @Test
    public void testFetchTextHTML() throws Url2TextException
    {
        Url2TextResponse response = this.fetcher.contentAsText(LOCAL_HOST
                + "html-4-JS.html", null);
        assertEquals(200, response.getStatus());
        assertTrue(response.getConvertedText().contains(
                "The date and time are:"));
    }

    @Test
    public void testFetchTextHTMLwithJS() throws Url2TextException
    {
        this.fetcher.setJavascriptEnabled(true);
        Url2TextResponse response = this.fetcher.contentAsText(LOCAL_HOST
                + "html-4-JS.html", null);
        assertEquals(200, response.getStatus());
        assertTrue(response.getConvertedText().contains("INSERTED DATE: "));
    }

    @Test
    public void testFetchTextXML() throws Url2TextException
    {
        Url2TextResponse response = this.fetcher.contentAsText(LOCAL_HOST
                + "example.wsdl", null);
        assertEquals(200, response.getStatus());
        assertTrue(response.getConvertedText().startsWith(
                "<?xml version=\"1.0\"?>"));
    }

    @Test
    public void testFetchTextDocBook4() throws Url2TextException
    {
        Url2TextResponse response = this.fetcher.contentAsText(LOCAL_HOST
                + "docbook.xml", null);
        assertEquals(200, response.getStatus());
        assertFalse(response.getConvertedText().startsWith(
                "<?xml version=\"1.0\"?>"));
        assertTrue(response.getConvertedText().contains("Test file."));
    }

    @Test
    public void testFetchTextDocBook5() throws Url2TextException
    {
        Url2TextResponse response = this.fetcher.contentAsText(LOCAL_HOST
                + "docbook5.xml", null);
        assertEquals(200, response.getStatus());
        assertFalse(response.getConvertedText().startsWith(
                "<?xml version=\"1.0\"?>"));
        assertTrue(response.getConvertedText().contains("Test file."));
    }

    @Test
    public void testFetchTextBinary() throws Url2TextException
    {
        this.fetcher.setIncludeMetadata(true);
        Url2TextResponse response = this.fetcher.contentAsText(LOCAL_HOST
                + "binary.odt", null);
        assertEquals(200, response.getStatus());
        assertTrue(response.getConvertedText().contains("Test binary doc."));
    }

}
