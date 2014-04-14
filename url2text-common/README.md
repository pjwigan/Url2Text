Url2Text-common
===============
Classes to support the other Url2Text modules.

See [url2text-parent] (https://github.com/pjwigan/Url2Text/tree/master/url2text-parent) for an overview of capability.

See [url2text-core](https://github.com/pjwigan/Url2Text/tree/master/url2text-core) for the primary class.


Installation
------------
Once it is stable, this project will be offered to Maven Central.  In the meantime it has to be built and installed manually.

### Build
Java 7 or later is required.

The cloned code can either be built using `mvn package` or imported into Eclipse (Kepler J2EE edition is the development environment).


Usage
-----
### Class: Constants
Static Strings and enums referenced by the other classes.

### Class: NameAndValue
A simple `<String, String>` implementation that allows duplicate names in lists..

### Class: Url2TextException
Url2Text classes only throw instances of this exception.  

### Class: Url2TextResponse
Encapsulates the response data, plus `toJson()` and `toString()` methods for JSON and plain text output respectively.

To reduce memory usage, a Reader is used internally as the text provider.  Thus instances of this class are both Closeable and AutoCloseable.


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
