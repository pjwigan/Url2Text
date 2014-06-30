package com.codealot.url2text;

import static com.codealot.url2text.Constants.HDR_CONTENT_METADATA;
import static com.codealot.url2text.Constants.HDR_IF_MODIFIED_SINCE;
import static com.codealot.url2text.Constants.HDR_IF_NONE_MATCH;
import static com.codealot.url2text.Constants.HDR_RESPONSE_HEADERS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

@SuppressWarnings("restriction")
public class Url2TextFetchTextTest
{

    private static final String LOCAL_HOST = "http://localhost:8000/";
    private static final String REMOTE_HOST = "http://example.com";

    private static HttpServer server;

    private final ObjectMapper mapper;
    private final Url2Text fetcher;

    public Url2TextFetchTextTest() throws Url2TextException
    {
        mapper = new ObjectMapper();
        fetcher = new Url2Text();
    }
    
    static class FileHandler implements HttpHandler
    {
        String contentType;
        Path filePath;
        
        FileHandler(String filename, String mimeType)
        {
            contentType = mimeType;
            filePath = Paths.get("src/test/resources/" + filename);
        }
        
        @Override
        public void handle(HttpExchange exchange) throws IOException
        {
            InputStream is = exchange.getRequestBody();
            while(is.read() > -1);
            Headers responseHeaders = exchange.getResponseHeaders();
            responseHeaders.add("Content-Type", contentType);
            byte[] response = Files.readAllBytes(filePath);
            exchange.sendResponseHeaders(200, response.length);
            OutputStream os = exchange.getResponseBody();
            os.write(response);
            exchange.close();
        }
    }

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/plain-text.txt", new FileHandler("plain-text.txt", "text/plain"));
        server.createContext("/html-4-JS.html", new FileHandler("html-4-JS.html", "text/html"));
        server.createContext("/example.wsdl", new FileHandler("example.wsdl", "application/wsdl+xml"));
        server.createContext("/encrypted.odt", new FileHandler("encrypted.odt", "application/vnd.oasis.opendocument.text "));
        server.createContext("/empty.doc", new FileHandler("empty.doc", "text/plain "));    // kludge to avoid Tika being invoked
        server.createContext("/docbook5.xml", new FileHandler("docbook5.xml", "application/xml"));
        server.createContext("/docbook.xml", new FileHandler("docbook.xml", "application/xml"));
        server.createContext("/binary.odt", new FileHandler("binary.odt", "application/vnd.oasis.opendocument.text "));
        server.setExecutor(null);
        server.start();
        // give it a moment to fire up
        Thread.sleep(500);
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception
    {
        server.stop(0);
    }

    @Test
    public void test404() throws Url2TextException, IOException
    {
        try (final Response response = this.fetcher.contentAsText(
                LOCAL_HOST + "not-exists.html", null))
        {
            assertEquals(404, response.getStatus());
        }
    }

    @Test
    public void testLastModified() throws Url2TextException, IOException
    {
        try (Response response = this.fetcher.contentAsText(
                REMOTE_HOST, null))
        {
            final HashMap<String, String> additionalHeaders = new HashMap<>();
            additionalHeaders.put(HDR_IF_MODIFIED_SINCE,
                    response.getLastModified());

            try (Response r2 = this.fetcher.contentAsText(
                    "http://example.com", additionalHeaders))
            {
                assertEquals(304, r2.getStatus());
            }
        }
    }

    @Test
    public void testEtag() throws Url2TextException, IOException
    {
        try (Response response = this.fetcher.contentAsText(
                REMOTE_HOST, null))
        {
            assertEquals(200, response.getStatus());
            final HashMap<String, String> additionalHeaders = new HashMap<>();
            additionalHeaders.put(HDR_IF_NONE_MATCH, response.getEtag());

            try (Response r2 = this.fetcher.contentAsText(
                    "http://example.com", additionalHeaders))
            {
                assertEquals(304, r2.getStatus());
            }
        }
    }

    @Test(expected = Url2TextException.class)
    public void testMaxLength() throws Url2TextException
    {
        this.fetcher.setMaxContentLength(10);
        this.fetcher.contentAsText(LOCAL_HOST + "plain-text.txt", null);
    }

    @Test(expected = Url2TextException.class)
    public void testEncryptedOdt() throws Url2TextException, IOException
    {
        try (final Response response = this.fetcher.contentAsText(
                LOCAL_HOST + "encrypted.odt", null))
        {
            // have to consume the response reader to trigger the exception
            response.getText();
        }
    }

    @Test
    public void testIncludeHeaders() throws Exception
    {
        this.fetcher.setIncludeHeaders(true);
        try(final Response response = this.fetcher.contentAsText(LOCAL_HOST
                + "plain-text.txt", null)) {
            assertEquals(200, response.getStatus());
            final JsonNode root = this.mapper.readTree(response.toJson());
            assertTrue(root.has(HDR_RESPONSE_HEADERS));
        }
    }

    @Test
    public void testIncludeMetadata() throws Exception
    {
        this.fetcher.setIncludeMetadata(true);
        try (final Response response = this.fetcher.contentAsText(
                LOCAL_HOST + "binary.odt", null))
        {
            assertEquals(200, response.getStatus());
            final JsonNode root = this.mapper.readTree(response.toJson());
            assertTrue(root.has(HDR_CONTENT_METADATA));
        }
    }

    @Test
    public void testFetchTextPlain() throws Url2TextException, IOException
    {
        try (final Response response = this.fetcher.contentAsText(
                LOCAL_HOST + "plain-text.txt", null))
        {
            assertEquals(200, response.getStatus());
            assertTrue(response.getText().contains("Just a plain text file."));
        }
    }

    @Test
    public void testFetchEmptyFile() throws Url2TextException, IOException
    {
        try (final Response response = this.fetcher.contentAsText(
                LOCAL_HOST + "empty.doc", null))
        {
            assertEquals(200, response.getStatus());
            assertTrue(response.getText().contains(""));
        }
    }

    @Test
    public void testFetchTextHTML() throws Url2TextException, IOException
    {
        try (final Response response = this.fetcher.contentAsText(
                LOCAL_HOST + "html-4-JS.html", null))
        {
            assertEquals(200, response.getStatus());
            assertTrue(response.getText().contains("The date and time are:"));
            assertEquals(response.getContentTitle(), "Page Title");
        }
    }

    @Test
    public void testFetchTextHTMLwithJS() throws Url2TextException, IOException
    {
        this.fetcher.setJavascriptEnabled(true);
        try (final Response response = this.fetcher.contentAsText(
                LOCAL_HOST + "html-4-JS.html", null))
        {
            assertEquals(200, response.getStatus());
            assertTrue(response.getText().contains("INSERTED DATE: "));
        }
    }

    @Test
    public void testFetchTextXML() throws Url2TextException, IOException
    {
        try (final Response response = this.fetcher.contentAsText(
                LOCAL_HOST + "example.wsdl", null))
        {
            assertEquals(200, response.getStatus());
            assertTrue(response.getText().startsWith("<?xml version=\"1.0\"?>"));
        }
    }

    @Test
    public void testFetchTextDocBook4() throws Url2TextException, IOException
    {
        try (final Response response = this.fetcher.contentAsText(
                LOCAL_HOST + "docbook.xml", null))
        {
            assertEquals(200, response.getStatus());
            assertFalse(response.getText()
                    .startsWith("<?xml version=\"1.0\"?>"));
            assertTrue(response.getText().contains("Test file."));
        }
    }

    @Test
    public void testFetchTextDocBook5() throws Url2TextException, IOException
    {
        try (final Response response = this.fetcher.contentAsText(
                LOCAL_HOST + "docbook5.xml", null))
        {
            assertEquals(200, response.getStatus());
            assertFalse(response.getText()
                    .startsWith("<?xml version=\"1.0\"?>"));
            assertTrue(response.getText().contains("Test file."));
        }
    }

    @Test
    public void testFetchTextBinary() throws Url2TextException, IOException
    {
        this.fetcher.setIncludeMetadata(true);
        try (final Response response = this.fetcher.contentAsText(
                LOCAL_HOST + "binary.odt", null))
        {
            assertEquals(200, response.getStatus());
            assertTrue(response.getText().contains("Test binary doc."));
        }
    }

}
