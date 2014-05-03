package com.codealot.url2text;

import static org.junit.Assert.*;
import static com.codealot.url2text.Constants.*;
import org.junit.Test;

public class NameAndValueTest {
    
    private final NameAndValue nameAndValue = new NameAndValue();

    @Test
    public void testNameAndValue() {
        // make sure no nulls are returned
        
        assertEquals(STR_NOT_SET, this.nameAndValue.getName());
        assertEquals(STR_NOT_SET, this.nameAndValue.getValue());
    }

    @Test
    public void testNameAndValueStringString() {
        // make sure no nulls are returned
        assertEquals(STR_NOT_SET, this.nameAndValue.getName());
        assertEquals(STR_NOT_SET, this.nameAndValue.getValue());
    }

    @Test
    public void testSetName() {
        // make sure no nulls are returned
        this.nameAndValue.setName(null);
        assertEquals("", this.nameAndValue.getName());
    }

    @Test
    public void testSetValue() {
        // make sure no nulls are returned
        this.nameAndValue.setValue(null);
        assertEquals("", this.nameAndValue.getValue());
    }

    @Test
    public void testEquals()
    {
        NameAndValue nameAndValue2 = new NameAndValue();
        
        assertEquals(nameAndValue, nameAndValue2);
        
        nameAndValue2.setName("name");
        
        assertNotEquals(nameAndValue, nameAndValue2);
    }
    
    @Test
    public void testCompareTo()
    {
        NameAndValue nav13 = new NameAndValue("1", "3");
        NameAndValue nav22 = new NameAndValue("2", "2");
        NameAndValue nav23 = new NameAndValue("2", "3");
        
        assertTrue(nav22.compareTo(nav13) > 0);
        assertTrue(nav13.compareTo(nav22) < 0);
        assertTrue(nav13.compareTo(new NameAndValue("1", "3")) == 0);
        assertTrue(nav23.compareTo(nav22) > 0);
    }
}
