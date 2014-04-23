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
    public static final String APP_VERSION = "0.5.1";

    // *_NOT_SET indicators, used as initialisers.
    public static final String STR_NOT_SET = "*not-set*";
    public static final int INT_NOT_SET = Integer.MIN_VALUE;
    public static final long LONG_NOT_SET = Long.MIN_VALUE;

    // Header-Like labels
    public static final String HDR_CONTENT_CHARSET = "Content-Charset";
    public static final String HDR_CONTENT_LENGTH = "Content-Length";
    public static final String HDR_CONTENT_METADATA = "Content-Metadata";
    public static final String HDR_CONTENT_TYPE = "Content-Type";
    public static final String HDR_CONVERSION_TIME = "Conversion-Time";
    public static final String HDR_CONVERTED_TEXT = "Converted-Text";
    public static final String HDR_ETAG = "ETag";
    public static final String HDR_FETCH_TIME = "Fetch-Time";
    public static final String HDR_IF_MODIFIED_SINCE = "If-Modified-Since";
    public static final String HDR_IF_NONE_MATCH = "If-None-Match";
    public static final String HDR_LANDING_PAGE = "Landing-Page";
    public static final String HDR_LAST_MODIFIED = "Last-Modified";
    public static final String HDR_REQUEST_PAGE = "Request-Page";
    public static final String HDR_RESPONSE_HEADERS = "Response-Headers";
    public static final String HDR_STATUS = "Status";
    public static final String HDR_STATUS_MESSAGE = "Status-Message";
    public static final String HDR_TRANSACTION_METADATA = "Transaction-Metadata";

    // Url2Text.class System property keys. Also used as GET params (length
    // irrelevant as not intended to be typed).
    public static final String KEY_ACTIVEX_NATIVE = "url2text.activexnative";
    public static final String KEY_APPLET_ENABLED = "url2text.appletenabled";
    public static final String KEY_GEOLOCATION_ENABLED = "url2text.geolocationenabled";
    public static final String KEY_POPUP_BLOCKER_ENABLED = "url2text.popupblockerenabled";
    public static final String KEY_EXCEPTION_ON_SCRIPT_ERROR = "url2text.exceptiononscripterror";
    public static final String KEY_EXCEPTION_ON_FAILING_STATUS = "url2text.exceptiononfailingstatus";
    public static final String KEY_PRINT_CONTENT_ON_FAILING_STATUS = "url2text.printcontentonfailingstatus";
    public static final String KEY_CSS_ENABLED = "url2text.cssenabled";
    public static final String KEY_DO_NOT_TRACK_ENABLED = "url2text.donottrackenabled";
    public static final String KEY_JAVASCRIPT_ENABLED = "url2text.javascriptenabled";
    public static final String KEY_USE_INSECURE_SSL = "url2text.useinsecuressl";
    public static final String KEY_REDIRECT_ENABLED = "url2text.redirectenabled";
    public static final String KEY_COOKIES_ENABLED = "url2text.cookiesenabled";
    public static final String KEY_CLEAR_COOKIES = "url2text.clearcookies";
    public static final String KEY_CLEAR_EXPIRED_COOKIES = "url2text.clearexpiredcookies";
    public static final String KEY_INCLUDE_HEADERS = "url2text.includeheaders";
    public static final String KEY_INCLUDE_METADATA = "url2text.includemetadata";
    public static final String KEY_NETWORK_TIMEOUT = "url2text.networktimeout";
    public static final String KEY_JAVASCRIPT_TIMEOUT = "url2text.javascripttimeout";
    public static final String KEY_MAX_CONTENT_LENGTH = "url2text.maxcontentlength";
    // GET only params
    public static final String KEY_REQUEST_URL = "url2text.requesturl";
    public static final String KEY_LAST_MODIFIED = "url2text.lastmodified";
    public static final String KEY_ETAG = "url2text.etag";

    // Collection of Url2Text.class property labels (i.e. GET params excluded)
    public static final String[] URL2TEXT_PROPERTY_KEYS = 
          { KEY_ACTIVEX_NATIVE,
            KEY_APPLET_ENABLED, KEY_GEOLOCATION_ENABLED,
            KEY_POPUP_BLOCKER_ENABLED, KEY_EXCEPTION_ON_SCRIPT_ERROR,
            KEY_EXCEPTION_ON_FAILING_STATUS,
            KEY_PRINT_CONTENT_ON_FAILING_STATUS, KEY_CSS_ENABLED,
            KEY_DO_NOT_TRACK_ENABLED, KEY_JAVASCRIPT_ENABLED,
            KEY_USE_INSECURE_SSL, KEY_REDIRECT_ENABLED, KEY_COOKIES_ENABLED,
            KEY_CLEAR_COOKIES, KEY_CLEAR_EXPIRED_COOKIES, KEY_INCLUDE_HEADERS,
            KEY_INCLUDE_METADATA, KEY_NETWORK_TIMEOUT, KEY_JAVASCRIPT_TIMEOUT,
            KEY_MAX_CONTENT_LENGTH 
          };

    // Default name of property file. Also used as System property key.
    public static final String URL2TEXT_PROPERTIES = "url2text.properties";

    // other constants
    public static final String UTF_8 = "UTF-8";

    // Supported output formats
    public enum OutputFormat
    {
        PLAIN, JSON
    };

}
