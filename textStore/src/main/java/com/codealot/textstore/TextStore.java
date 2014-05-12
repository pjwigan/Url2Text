package com.codealot.textstore;

import java.io.IOException;
import java.io.Reader;
import java.util.Date;

/**
 * Interface to decouple the storage and retrieval of text from the medium.
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
public interface TextStore
{
    
    // FUTURE store charset
    // FUTURE store language
    // FUTURE make NO_CONTENT localisable
    
    public static final String NO_CONTENT = "no content";

    /**
     * Fetch the text represented by an id.
     * 
     * @param hash
     * @return full text
     * @throws IOException
     */
    public String getText(String id) throws IOException;
    
    /**
     * Returns the size of the uncompressed text represented by the supplied id.
     * 
     * @param id
     * @return length of uncompressed text
     */
    public long getLength(String id) throws IOException;
    
    /**
     * Returns the Date (in UTC) that the text of the given id was stored.
     * 
     * @param id
     * @return
     * @throws IOException 
     */
    public Date getStoreDate(String id) throws IOException;
    
    /**
     * Returns a Reader on the stored text.
     * <p>
     * Implementors should be aware of how this may be a blocking operation.
     * 
     * @param id
     * @return
     */
    public Reader getTextReader(String id) throws IOException;
    
    /**
     * Store the text and return an id by which it can be retrieved or deleted.
     * <p>
     * This method is idempotent; i.e. it will return the same id if given an
     * existing text to store.
     * 
     * @param text
     * @return id of the stored text
     * @throws IOException
     */
    public String storeText(String text) throws IOException;
    
    /**
     * Store the text and return an id by which it can be retrieved or deleted.
     * As it has been consumed, the provided Reader should be closed.
     * <p>
     * This method is idempotent; i.e. it will return the same id if given an
     * existing text to store.
     * 
     * @param reader source of text
     * @return id of the stored text
     * @throws IOException
     */
    public String storeText(Reader reader) throws IOException;
    
    /**
     * Delete the text represented by id from the store.
     * <p>
     * It is up to the implementation to decide if this operation is idempotent.
     * 
     * @param id
     * @return true if the text was deleted
     * @throws IOException
     */
    public boolean deleteText(String id) throws IOException;
}
