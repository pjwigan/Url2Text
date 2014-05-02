package com.codealot.url2text;

import static com.codealot.url2text.Constants.*;

import java.io.Serializable;
import java.util.Objects;

/**
 * Avoids a heavyweight dependency.
 * <p>
 * The getters will never return null. If name or value have not been set, their
 * getters will return Contants.STR_NOT_SET.
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
@SuppressWarnings("serial")
public class NameAndValue implements Serializable
{
    private String name = STR_NOT_SET;
    private String value = STR_NOT_SET;

    
    public NameAndValue()
    {
        // default
    }

    public NameAndValue(final String name, final String value)
    {
        this.setName(name);
        this.setValue(value);
    }

    /**
     * Note: null is never returned but the empty string might be.
     * 
     * @return the name
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Note that null is converted to the empty string.
     * 
     * @param name
     */
    public void setName(final String name)
    {
        this.name = (name == null) ? "" : name;
    }

    /**
     * Note: null is never returned but the empty string might be.
     * 
     * @return the value
     */
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

    @Override
    public int hashCode()
    {
        return Objects.hash(name, value);
    }

    @Override
    public boolean equals(final Object obj)
    {
        boolean result = false;
        if (this == obj)
        {
            result = true;
        }
        else if (obj == null || obj.getClass() != this.getClass())
        {
            result = false;
        }
        else
        {
            final NameAndValue test = (NameAndValue) obj;
            // all content is included in toString(), so this is safe.
            result = test.toString().equals(this.toString());
        }
        return result;
    }

    @Override
    public String toString()
    {
        return name + " : " + value;
    }

    
}
