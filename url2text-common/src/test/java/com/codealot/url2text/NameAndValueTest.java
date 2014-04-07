package com.codealot.url2text;

import static org.junit.Assert.*;
import static com.codealot.url2text.Constants.*;
import org.junit.Test;

public class NameAndValueTest {
    
    private NameAndValue namesAndValues = new NameAndValue();

    @Test
    public void testNameAndValue() {
        // make sure no nulls are returned
        
        assertEquals(STR_NOT_SET, this.namesAndValues.getKey());
        assertEquals(STR_NOT_SET, this.namesAndValues.getValue());
    }

    @Test
    public void testNameAndValueStringString() {
        // make sure no nulls are returned
        assertEquals(STR_NOT_SET, this.namesAndValues.getKey());
        assertEquals(STR_NOT_SET, this.namesAndValues.getValue());
    }

    @Test
    public void testSetName() {
        // make sure no nulls are returned
        this.namesAndValues.setKey(null);
        assertEquals("", this.namesAndValues.getKey());
    }

    @Test
    public void testSetValue() {
        // make sure no nulls are returned
        this.namesAndValues.setValue(null);
        assertEquals("", this.namesAndValues.getValue());
    }

}
