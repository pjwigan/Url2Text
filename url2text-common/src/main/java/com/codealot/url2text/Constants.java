package com.codealot.url2text;

/**
 * Constants for static import.
 * <p>
 * There is no behaviour here, so an interface isn't appropriate.
 * 
 * @author jacobsp
 * 
 * <p>Copyright (C) 2014 Codealot Limited.
 * 
 * <p>Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class Constants {
    
    public static final String APP_VERSION = "0.5.0";

    // STR_NOT_SET indicators, used as initialisers.
    public static final String STR_NOT_SET = "*not-set*";
    public static final int INT_NOT_SET = Integer.MIN_VALUE;
    public static final long LONG_NOT_SET = Long.MIN_VALUE;
    
    // Header-Like labels
    public static final String CONTENT_CHARSET = "Content-Charset";
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String CONTENT_METADATA = "Content-Metadata";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONVERSION_TIME = "Conversion-Time";
    public static final String CONVERTED_TEXT = "Converted-Text";
    public static final String ETAG = "ETag";
    public static final String FETCH_TIME = "Fetch-Time";
    public static final String IF_MODIFIED_SINCE = "If-Modified-Since";
    public static final String IF_NONE_MATCH = "If-None-Match";
    public static final String LANDING_PAGE = "Landing-Page";
    public static final String LAST_MODIFIED = "Last-Modified";
    public static final String REQUEST_PAGE = "Request-Page";
    public static final String RESPONSE_HEADERS = "Response-Headers";
    public static final String STATUS = "Status";
    public static final String STATUS_MESSAGE = "Status-Message";
    public static final String TRANSACTION_METADATA = "Transaction-Metadata";
    
    // other constants
    public static final String UTF_8 = "UTF-8";

    // Supported output formats
    public enum OutputFormat {PLAIN, JSON};
    
}
