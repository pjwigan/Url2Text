package com.codealot.url2text;

import static com.codealot.url2text.Constants.*;

/**
 * Avoids a heavyweight dependency.
 * <p>
 * The getters will never return null. If name or value have not been set, their
 * getters will return "*not-set*".
 * <p>
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
public class NameAndValue
{

    private String name = STR_NOT_SET;
    private String value = STR_NOT_SET;

    public NameAndValue()
    {
        // default
    }

    public NameAndValue(final String key, final String value)
    {
        this.setKey(key);
        this.setValue(value);
    }

    public String getKey()
    {
        return this.name;
    }

    /**
     * Note that null is converted to the empty string.
     * 
     * @param name
     */
    public void setKey(final String key)
    {
        this.name = (key == null) ? "" : key;
    }

    public String getValue()
    {
        return this.value;
    }

    /**
     * Note that null is converted to the empty string.
     * 
     * @param value
     */
    public void setValue(final String value)
    {
        this.value = (value == null) ? "" : value;
    }

}
