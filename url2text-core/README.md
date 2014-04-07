Url2Text-core
=============
Java class to fetch the content of a URL, convert it to text, with optional metadata, and produce either plain or JSON output.
 
An integration of the awesome [HtmlUnit](http://htmlunit.sourceforge.net/) and [Apache Tika](https://tika.apache.org/) projects.

See [url2text-parent] (https://github.com/pjwigan/Url2Text/tree/master/url2text-parent) for an overview of capability.

See [url2text-common](https://github.com/pjwigan/Url2Text/tree/master/url2text-common) for the support classes.


Installation
------------
Once it is stable, this project will be offered to Maven Central.  In the meantime it has to be built and installed manually.

### Build
Java 7 or later is required.

The cloned code can either be built using `mvn package` or imported into Eclipse (Kepler J2EE edition is the development environment).

For testing only, it is necessary for `python` to be on the path.


Usage
-----
Basic use is:

    import com.codealot.url2text.Url2Text;
    import com.codealot.url2text.Url2TextResponse;    
    ...
    try {
      Url2Text fetch = new Url2Text();
      // configure fetch here ... 
      Url2TextResponse response = fetch.contentAsText("http://example.com");
      if (response.getStatus() == 200) {
        return response.toJson();
      }
    } catch (Url2TextException e) {
        ...
    }

The `Url2TextResponse` object is a POJO encapsulating the fetched text, metadata, headers, etc.  

*Currently Url2Text emulates FireFox.  (This project was inspired by a need to act as a proxy for a human user).  This may be made configurable in a future version.*

Most of the HtmlUnit `WebClientOptions` and `CookieManager` features are exposed as properties of the Url2Text class.  Headers can also be added to the `WebRequest`.

No transient state is stored in the `Url2Text` instance, so they can be reused safely.


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
