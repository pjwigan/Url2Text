package com.codealot.url2text;

import static org.junit.Assert.*;

import org.junit.Test;

public class Url2TextAPITest
{

    private Url2Text fetcher = new Url2Text();

    @Test
    public void testSetActiveXNative()
    {
        // check default, then change
        assertFalse(this.fetcher.activeXNative());
        this.fetcher.setActiveXNative(true);
        assertTrue(this.fetcher.activeXNative());
    }

    @Test
    public void testSetAppletEnabled()
    {
        // check default, then change
        assertFalse(this.fetcher.appletEnabled());
        this.fetcher.setAppletEnabled(true);
        assertTrue(this.fetcher.appletEnabled());
    }

    @Test
    public void testSetGeolocationEnabled()
    {
        // check default, then change
        assertFalse(this.fetcher.geolocationEnabled());
        this.fetcher.setGeolocationEnabled(true);
        assertTrue(this.fetcher.geolocationEnabled());
    }

    @Test
    public void testSetExceptionOnScriptError()
    {
        // check default, then change
        assertFalse(this.fetcher.exceptionOnScriptError());
        this.fetcher.setExceptionOnScriptError(true);
        assertTrue(this.fetcher.exceptionOnScriptError());
    }

    @Test
    public void testSetExceptionOnFailingStatusCode()
    {
        // check default, then change
        assertFalse(this.fetcher.exceptionOnFailingStatusCode());
        this.fetcher.setExceptionOnFailingStatusCode(true);
        assertTrue(this.fetcher.exceptionOnFailingStatusCode());
    }

    @Test
    public void testSetPrintContentOnFailingStatusCode()
    {
        // check default, then change
        assertFalse(this.fetcher.printContentOnFailingStatusCode());
        this.fetcher.setPrintContentOnFailingStatusCode(true);
        assertTrue(this.fetcher.printContentOnFailingStatusCode());
    }

    @Test
    public void testSetCssEnabled()
    {
        // check default, then change
        assertFalse(this.fetcher.cssEnabled());
        this.fetcher.setCssEnabled(true);
        assertTrue(this.fetcher.cssEnabled());
    }

    @Test
    public void testSetDoNotTrackEnabled()
    {
        // check default, then change
        assertFalse(this.fetcher.doNotTrackEnabled());
        this.fetcher.setDoNotTrackEnabled(true);
        assertTrue(this.fetcher.doNotTrackEnabled());
    }

    @Test
    public void testSetJavascriptEnabled()
    {
        // check default, then change
        assertFalse(this.fetcher.javascriptEnabled());
        this.fetcher.setJavascriptEnabled(true);
        assertTrue(this.fetcher.javascriptEnabled());
    }

    @Test
    public void testSetPopupBlockerEnabled()
    {
        // check default, then change
        assertTrue(this.fetcher.popupBlockerEnabled());
        this.fetcher.setPopupBlockerEnabled(false);
        assertFalse(this.fetcher.popupBlockerEnabled());
    }

    @Test
    public void testSetRedirectEnabled()
    {
        // check default, then change
        assertTrue(this.fetcher.redirectEnabled());
        this.fetcher.setRedirectEnabled(false);
        assertFalse(this.fetcher.redirectEnabled());
    }

    @Test
    public void testSetUseInsecureSSL()
    {
        // check default, then change
        assertFalse(this.fetcher.useInsecureSSL());
        this.fetcher.setUseInsecureSSL(true);
        assertTrue(this.fetcher.useInsecureSSL());
    }

    @Test
    public void testSetCookiesEnabled()
    {
        // check default, then change
        assertTrue(this.fetcher.cookiesEnabled());
        this.fetcher.setCookiesEnabled(false);
        assertFalse(this.fetcher.cookiesEnabled());
    }

    @Test
    public void testSetClearCookies()
    {
        // check default, then change
        assertTrue(this.fetcher.clearCookies());
        this.fetcher.setClearCookies(false);
        assertFalse(this.fetcher.clearCookies());
    }

    @Test
    public void testSetClearExpiredCookies()
    {
        // check default, then change
        assertTrue(this.fetcher.clearExpiredCookies());
        this.fetcher.setClearExpiredCookies(false);
        assertFalse(this.fetcher.clearExpiredCookies());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetHttpTimeoutNegative()
    {
        this.fetcher.setNetworkTimeout(-1);
    }

    @Test
    public void testSetHttpTimeout()
    {
        // test default, then 0, then number
        assertEquals(90, this.fetcher.getNetworkTimeout());
        this.fetcher.setNetworkTimeout(0);
        assertEquals(0, this.fetcher.getNetworkTimeout());
        this.fetcher.setNetworkTimeout(100);
        assertEquals(100, this.fetcher.getNetworkTimeout());
    }

    @Test
    public void testSetIncludeHeaders()
    {
        // check default, then change
        assertFalse(this.fetcher.includeHeaders());
        this.fetcher.setIncludeHeaders(true);
        assertTrue(this.fetcher.includeHeaders());
    }

    @Test
    public void testSetIncludeMetadata()
    {
        // check default, then change
        assertFalse(this.fetcher.includeMetadata());
        this.fetcher.setIncludeMetadata(true);
        assertTrue(this.fetcher.includeMetadata());
    }

    @Test
    public void testSetJavascriptTimeout()
    {
        // test default, then 0, then number
        assertEquals(20, this.fetcher.getJavascriptTimeout());
        this.fetcher.setJavascriptTimeout(0);
        assertEquals(0, this.fetcher.getJavascriptTimeout());
        this.fetcher.setJavascriptTimeout(-1);
        assertEquals(-1, this.fetcher.getJavascriptTimeout());
        this.fetcher.setJavascriptTimeout(100);
        assertEquals(100, this.fetcher.getJavascriptTimeout());
    }

    @Test
    public void testSetMaxLength()
    {
        // test default, then 0, then number
        assertEquals(1024 * 1024, this.fetcher.getMaxContentLength());
        this.fetcher.setMaxContentLength(0);
        assertEquals(0, this.fetcher.getMaxContentLength());
        this.fetcher.setMaxContentLength(-1);
        assertEquals(-1, this.fetcher.getMaxContentLength());
        this.fetcher.setMaxContentLength(100);
        assertEquals(100, this.fetcher.getMaxContentLength());
    }

}
