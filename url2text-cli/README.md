Url2Text-cli
============
Command line wrapper for the Url2Text Java class.  

See [url2text-parent] (https://github.com/pjwigan/Url2Text/tree/master/url2text-parent) for an overview of capability.

See [url2text-core](https://github.com/pjwigan/Url2Text/tree/master/url2text-core) for the primary class.

See [url2text-common](https://github.com/pjwigan/Url2Text/tree/master/url2text-common) for the support classes.


Installation
------------
Once it is stable, this project will be offered to Maven Central.  In the meantime it has to be built and installed manually.

### Build
Java 7 or later is required.

The cloned code can either be built using `mvn package` or imported into Eclipse (Kepler J2EE edition is the development environment).


Usage
-----
Basic usage is of the form:
    
    java [JVM options] -jar Url2Text.jar [options] url

The following command line options are available:

    Option                                  Description                        
    ------                                  -----------                        
    -?, -h, --help                          show help                          
    --css                                   Enable CSS support                 
    --do-not-track                          Enable Do Not Track support        
    --http-timeout <Integer: seconds>       HTTP transaction timeout
    --include-headers                       Include HTTP response headers      
    --include-metadata                      Include content metadata
    --insecure-ssl                          Ignore server certificates         
    --javascript                            Enable Javascript        
    --javascript-timeout <Integer: seconds> Javascript execution timeout          
    --max-length                            Maximum Content-Length
    --no-cookies                            Disable cookie support             
    --no-redirect                           Disable redirection                
    --output-file <File: file>              File to receive output               
    --output-format                         One of PLAIN, JSON                     
    --version                               Print version to stdout            

Note the `http-timeout` is applied twice; once for connection and separately for data retrieval.  Thus the actual delay before aborting a slow transaction could be up to twice the period given.  Default is 90 seconds.  Use zero for infinite timeout.

*There is currently no timeout for text conversion, which could theoretically hang.  This is considered a medium priority bug.*

`max-length` applies a limit to the Content-Length.  Default is 1MiB

### Logging
The SLF4J SimpleLogger is used, with a default level of 'info'.  Messages are output to stderr.  To change the logger configuration, use system properties as documented [here](http://www.slf4j.org/api/org/slf4j/impl/SimpleLogger.html) with the `-D` command line switch: e.g.:

    java -Dorg.slf4j.simpleLogger.defaultLogLevel=debug -jar Url2Text.jar http://example.com


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
