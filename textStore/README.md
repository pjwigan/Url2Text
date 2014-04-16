Text Store
==========
An interface to abstract the storage of text on disparate media.

See `FileStore` for an implementation using a [FileSystem](http://docs.oracle.com/javase/7/docs/api/java/nio/file/FileSystems.html).


storeText(Reader)
-----------------
Store the text and return an `id` by which it can be retrieved or deleted.

As it has been consumed, the provided `Reader` should be closed by the implementation.


storeText(String)
-----------------
Convenience method which takes a `String` instead of a `Reader`.


getText(String)
---------------
Given an `id`, will return the associated text.


getTextReader(String)
---------------------
Given an `id`, will return a `Reader` for the associated text.

Implementors should be aware of the possibility of this being a blocking operation.


deleteText(String)
------------------
Given an `id`, will attempt to delete the associated text from the store.  A boolean `true` is returned if deletion occurs.

It is up to the implementation to decide if this operation is idempotent.


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
