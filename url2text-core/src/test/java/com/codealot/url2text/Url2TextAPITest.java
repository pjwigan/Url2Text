package com.codealot.url2text;

import static com.codealot.url2text.Constants.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Properties;

import org.junit.Test;

public class Url2TextAPITest
{
    private final Url2Text fetcher;
    
    public Url2TextAPITest() throws Url2TextException 
    {
        fetcher = new Url2Text();
    }    
    
    @Test
    public void testPropertiesNull() throws Url2TextException 
    {
        new Url2Text(null);
    }
    
    @Test
    public void testPropertiesEmpty() throws Url2TextException 
    {
        assertEquals(fetcher, new Url2Text(null));
        assertEquals(fetcher, new Url2Text(new Properties()));
    }
    
    @Test
    public void testSystemPropertyOveride() throws Url2TextException 
    {
        final String testKey = KEY_ACTIVEX_NATIVE;
        try
        {
            System.setProperty(testKey, Boolean.TRUE.toString());

            final Url2Text fetcher = new Url2Text();
            assertTrue(fetcher.hasActiveXNative());
        }
        finally
        {
            System.getProperties().remove(testKey);
        }
    }
        
    @Test
    public void testSystemPropertiesOveride() throws Url2TextException 
    {
        final String testKey = KEY_ACTIVEX_NATIVE;
        try
        {
            System.setProperty(testKey, Boolean.TRUE.toString());

            final Properties properties = new Properties();
            properties.put(KEY_ACTIVEX_NATIVE, Boolean.FALSE.toString());

            final Url2Text fetcher = new Url2Text(properties);
            assertTrue(fetcher.hasActiveXNative());
        }
        finally
        {
            System.getProperties().remove(testKey);
        }
    }
    
    @Test(expected = Url2TextException.class)
    public void testPropertiesBadValue() throws Url2TextException 
    {
        final Properties properties = new Properties();
        properties.put(KEY_ACTIVEX_NATIVE, "bananas");

        new Url2Text(properties);
    }
    
    @Test
    public void testPropertiesMixedCaseKey() throws Url2TextException 
    {
        final Properties properties = new Properties();
        properties.put(KEY_ACTIVEX_NATIVE.toUpperCase(), "true");

        assertEquals("true", properties.getProperty(KEY_ACTIVEX_NATIVE.toUpperCase()));
        
        final Url2Text testFetcher = new Url2Text(properties);
        assertTrue(testFetcher.hasActiveXNative());
    }
    
    @Test
    public void testSystemPropertiesOverideMixedCase() throws Url2TextException 
    {
        final String testKey = KEY_ACTIVEX_NATIVE.toUpperCase();
        try
        {
            System.setProperty(testKey, Boolean.TRUE.toString());

            final Properties properties = new Properties();
            properties.put(KEY_ACTIVEX_NATIVE, Boolean.FALSE.toString());

            final Url2Text fetcher = new Url2Text(properties);
            assertTrue(fetcher.hasActiveXNative());
        }
        finally
        {
            System.getProperties().remove(testKey);
        }
    }
    
    @Test
    public void testDefaults() 
    {
        assertEquals(fetcher.hasActiveXNative(), false);
        assertEquals(fetcher.hasAppletEnabled(), false);
        assertEquals(fetcher.hasGeolocationEnabled(), false);
        assertEquals(fetcher.hasPopupBlockerEnabled(), true);
        assertEquals(fetcher.hasExceptionOnScriptError(), false);
        assertEquals(fetcher.hasExceptionOnFailingStatus(), false);
        assertEquals(fetcher.hasPrintContentOnFailingStatus(), false);
        assertEquals(fetcher.hasCssEnabled(), false);
        assertEquals(fetcher.hasDoNotTrackEnabled(), false);
        assertEquals(fetcher.hasJavascriptEnabled(), false);
        assertEquals(fetcher.hasUseInsecureSSL(), false);
        assertEquals(fetcher.hasRedirectEnabled(), true);
        assertEquals(fetcher.hasCookiesEnabled(), true);
        assertEquals(fetcher.hasClearCookies(), true);
        assertEquals(fetcher.hasClearExpiredCookies(), true);
        assertEquals(fetcher.hasIncludeHeaders(), false);
        assertEquals(fetcher.hasIncludeMetadata(), false);
        assertEquals(fetcher.getNetworkTimeout(), 90);
        assertEquals(fetcher.getJavascriptTimeout(), 20);
        assertEquals(fetcher.getMaxContentLength(), 1_024 * 1_024);
    }

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
        assertFalse(this.fetcher.hasExceptionOnFailingStatus());
        this.fetcher.setExceptionOnFailingStatus(true);
        assertTrue(this.fetcher.hasExceptionOnFailingStatus());
    }

    @Test
    public void testSetPrintContentOnFailingStatusCode()
    {
        // check default, then change
        assertFalse(this.fetcher.hasPrintContentOnFailingStatus());
        this.fetcher.setPrintContentOnFailingStatus(true);
        assertTrue(this.fetcher.hasPrintContentOnFailingStatus());
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
