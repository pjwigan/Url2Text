Url2Text
========
Fetches the content of a URL then converts it to text, optionally with metadata, and produces a plain or JSON output.

An integration of the awesome [HtmlUnit](http://htmlunit.sourceforge.net/) and [Apache Tika](https://tika.apache.org/) projects.

See [url2text-common](https://github.com/pjwigan/Url2Text/tree/master/url2text-common) for the classes shared by the `url2text-core` and `url2text-cli` modules.

See [url2text-core](https://github.com/pjwigan/Url2Text/tree/master/url2text-core) for the `Url2Text` Java class.

See [url2text-cli] (https://github.com/pjwigan/Url2Text/tree/master/url2text-cli) for a command line wrapper of the above modules.


Content Fetching
----------------
Target URLs can be of any protocol supported by HtmlUnit, including `file` and `http[s]`.  There are options to execute any JavaScript and apply CSS as appropriate.

*Currently Url2Text emulates FireFox.  (This project was inspired by a need to act as a proxy for a human user).  This may be made configurable in a future version.*

Options enable the inclusion of response headers and content metadata in the output.


Content Conversion
------------------
Content is usually converted to plain text.  (XML other than DocBook is an exception: see below.)  The default charset is `UTF-8`.  If the response headers specify one, or if Tika detects a more appropriate one, that will be used instead.

### HTML
Optionally JavaScript can be executed and CSS applied before the text representation of the DOM is produced.  Cookies are handled by default, but can be disabled.

*Note that as DOM node visibility is not considered, messages concerning cookies being disallowed and JavaScript being disabled often appear in the converted text, even though the cookies have been accepted and the JavaScript has been executed.*

### DocBook
When DocBook source is detected, it is fed to Tika for conversion to text.

### Other XML
XML content is not processed in any way; i.e. the source document is passed directly to the output stream.

*One exception: when XML is packaged in a zip or similar format, it will be converted by Tika.  The result is often nonsensical.*

### Other Formats
These are fed to Tika for conversion to text.  See [Supported Formats](https://tika.apache.org/1.5/formats.html) for an overview.


Response Headers
----------------
The output can optionally include a copy of the response headers, if any.  Duplicates are allowed; e.g. multiple `Set-Cookie` lines.


Content Metadata
----------------
The output can optionally include metadata extracted by Tika from the content.

### Transaction Metadata
Several of the header fields are reproduced here for convenience.

*   **Request-Page:**
    The external form of the given URL.

*   **Landing-Page:** 
    External form of the URL from which the content was fetched.  This can differ from the request page due to normalisation or redirection.

*   **Status:**
    The HTTP status code.

*   **Status-Message:**
    The HTTP message that accompanied the status code.

*   **Fetch-Time:**
    The number of milliseconds it took to fetch the content from the server.

*   **Content-Type:**
    Reported content type of the fetched content.

*   **Content-Charset:**
    Reported charset of the fetched content.

*   **Content-Disposition:**
    Reported disposition, if any, of the fetched content.

*   **Content-Length:**
    Reported length of the fetched content.

*   **ETag:**
    Reported ETag, if any, of the fetched content.

*   **Last-Modified:**
    Reported Last-Modified, if any, of the fetched content.
    
*   **Conversion-Time:**
    The number of milliseconds it took Tika to process the content.

### Content Metadata
Whatever metadata is produced by Tika when converting the content to text; e.g. [Dublin Core](http://dublincore.org/documents/usageguide/); is included.


Output Formats
--------------
*Earlier versions included an XML option.  This has been removed until the code base stabilises.*

### Plain
Intended for human consumption.  For example (with headers and metadata included):

    ################ TRANSACTION METADATA ################
    Request page  : http://example.com
    Landing page  : http://example.com/
    Status        : 200 OK
    Fetch time    : 281 ms
    Content type  : text/html
    Content char  : UTF-8
    Content length: 1270
    Etag          : "359670651"
    Last Modified : Fri, 09 Aug 2013 23:54:35 GMT
    Convert time  : 351 ms

    ################ RESPONSE HEADERS ####################
    Accept-Ranges = bytes
    Cache-Control = max-age=604800
    Content-Type = text/html
    Date = Thu, 03 Apr 2014 12:37:17 GMT
    Etag = "359670651"
    Expires = Thu, 10 Apr 2014 12:37:17 GMT
    Last-Modified = Fri, 09 Aug 2013 23:54:35 GMT
    Server = ECS (iad/19AB)
    X-Cache = HIT
    x-ec-custom-error = 1
    Content-Length = 1270

    ################ CONTENT METADATA ####################
    viewport = width=device-width, initial-scale=1
    title = Example Domain
    Content-Encoding = UTF-8
    Content-Type = text/html; charset=utf-8
    dc:title = Example Domain

    ################ CONVERTED TEXT ######################
    Example Domain
    Example Domain
    This domain is established to be used for illustrative examples in 
    documents. You may use this domain in examples without prior coordination 
    or asking for permission.
    More information...


### JSON
Intended for machine reading.  The following example has been pretty-printed for readability: 

    {"Transaction-Metadata":{
        "Request-Page":"http://example.com",
        "Landing-Page":"http://example.com/",
        "Status":200,
        "Status-Message":"OK",
        "Fetch-Time":274,
        "Content-Type":"text/html",
        "Content-Charset":"UTF-8",
        "Content-Length":1270,
        "ETag":"\"359670651\"",
        "Last-Modified":"Fri, 09 Aug 2013 23:54:35 GMT",
        "Conversion-Time":353},
     "Response-Headers":{
        "Accept-Ranges":"bytes",
        "Cache-Control":"max-age=604800",
        "Content-Type":"text/html",
        "Date":"Thu, 03 Apr 2014 12:39:26 GMT",
        "Etag":"\"359670651\"",
        "Expires":"Thu, 10 Apr 2014 12:39:26 GMT",
        "Last-Modified":"Fri, 09 Aug 2013 23:54:35 GMT",
        "Server":"ECS (iad/19AB)",
        "X-Cache":"HIT",
        "x-ec-custom-error":"1",
        "Content-Length":"1270"},
     "Content-Metadata":{
        "viewport":"width=device-width, initial-scale=1",
        "title":"Example Domain",
        "Content-Encoding":"UTF-8",
        "Content-Type":"text/html; charset=utf-8",
        "dc:title":"Example Domain"},
     "Converted-Text":
        "Example Domain\nExample Domain\nThis domain is established to be used
        for illustrative examples in documents. You may use this domain in
        examples without prior coordination or asking for permission.\nMore
        information..."
    }

Installation
------------
Once it is stable, this project will be offered to Maven Central.  In the meantime it has to be built and installed manually.

### Build
Java 7 or later is required.

The cloned code can either be built using `mvn package` or imported into Eclipse (Kepler J2EE edition is the development environment).

For testing of `url2text-core` only, it is necessary for python to be on the path.


LICENSE
-------
Copyright 2014 Codealot Limited.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

<http://www.apache.org/licenses/LICENSE-2.0>

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
