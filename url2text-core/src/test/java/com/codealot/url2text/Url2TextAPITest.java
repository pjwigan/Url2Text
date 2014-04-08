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
        assertFalse(this.fetcher.hasActiveXNative());
        this.fetcher.setActiveXNative(true);
        assertTrue(this.fetcher.hasActiveXNative());
    }

    @Test
    public void testSetAppletEnabled()
    {
        // check default, then change
        assertFalse(this.fetcher.hasAppletEnabled());
        this.fetcher.setAppletEnabled(true);
        assertTrue(this.fetcher.hasAppletEnabled());
    }

    @Test
    public void testSetGeolocationEnabled()
    {
        // check default, then change
        assertFalse(this.fetcher.hasGeolocationEnabled());
        this.fetcher.setGeolocationEnabled(true);
        assertTrue(this.fetcher.hasGeolocationEnabled());
    }

    @Test
    public void testSetExceptionOnScriptError()
    {
        // check default, then change
        assertFalse(this.fetcher.hasExceptionOnScriptError());
        this.fetcher.setExceptionOnScriptError(true);
        assertTrue(this.fetcher.hasExceptionOnScriptError());
    }

    @Test
    public void testSetExceptionOnFailingStatusCode()
    {
        // check default, then change
        assertFalse(this.fetcher.hasExceptionOnFailingStatusCode());
        this.fetcher.setExceptionOnFailingStatusCode(true);
        assertTrue(this.fetcher.hasExceptionOnFailingStatusCode());
    }

    @Test
    public void testSetPrintContentOnFailingStatusCode()
    {
        // check default, then change
        assertFalse(this.fetcher.hasPprintContentOnFailingStatusCode());
        this.fetcher.setPrintContentOnFailingStatusCode(true);
        assertTrue(this.fetcher.hasPprintContentOnFailingStatusCode());
    }

    @Test
    public void testSetCssEnabled()
    {
        // check default, then change
        assertFalse(this.fetcher.hasCssEnabled());
        this.fetcher.setCssEnabled(true);
        assertTrue(this.fetcher.hasCssEnabled());
    }

    @Test
    public void testSetDoNotTrackEnabled()
    {
        // check default, then change
        assertFalse(this.fetcher.hasDoNotTrackEnabled());
        this.fetcher.setDoNotTrackEnabled(true);
        assertTrue(this.fetcher.hasDoNotTrackEnabled());
    }

    @Test
    public void testSetJavascriptEnabled()
    {
        // check default, then change
        assertFalse(this.fetcher.hasJavascriptEnabled());
        this.fetcher.setJavascriptEnabled(true);
        assertTrue(this.fetcher.hasJavascriptEnabled());
    }

    @Test
    public void testSetPopupBlockerEnabled()
    {
        // check default, then change
        assertTrue(this.fetcher.hasPopupBlockerEnabled());
        this.fetcher.setPopupBlockerEnabled(false);
        assertFalse(this.fetcher.hasPopupBlockerEnabled());
    }

    @Test
    public void testSetRedirectEnabled()
    {
        // check default, then change
        assertTrue(this.fetcher.hasRedirectEnabled());
        this.fetcher.setRedirectEnabled(false);
        assertFalse(this.fetcher.hasRedirectEnabled());
    }

    @Test
    public void testSetUseInsecureSSL()
    {
        // check default, then change
        assertFalse(this.fetcher.hasUseInsecureSSL());
        this.fetcher.setUseInsecureSSL(true);
        assertTrue(this.fetcher.hasUseInsecureSSL());
    }

    @Test
    public void testSetCookiesEnabled()
    {
        // check default, then change
        assertTrue(this.fetcher.hasCookiesEnabled());
        this.fetcher.setCookiesEnabled(false);
        assertFalse(this.fetcher.hasCookiesEnabled());
    }

    @Test
    public void testSetClearCookies()
    {
        // check default, then change
        assertTrue(this.fetcher.hasClearCookies());
        this.fetcher.setClearCookies(false);
        assertFalse(this.fetcher.hasClearCookies());
    }

    @Test
    public void testSetClearExpiredCookies()
    {
        // check default, then change
        assertTrue(this.fetcher.hasClearExpiredCookies());
        this.fetcher.setClearExpiredCookies(false);
        assertFalse(this.fetcher.hasClearExpiredCookies());
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
        assertFalse(this.fetcher.hasIncludeHeaders());
        this.fetcher.setIncludeHeaders(true);
        assertTrue(this.fetcher.hasIncludeHeaders());
    }

    @Test
    public void testSetIncludeMetadata()
    {
        // check default, then change
        assertFalse(this.fetcher.hasIncludeMetadata());
        this.fetcher.setIncludeMetadata(true);
        assertTrue(this.fetcher.hasIncludeMetadata());
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
