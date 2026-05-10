package com.primihub.entity;

import org.junit.Test;
import static org.junit.Assert.*;

public class DataSetTest {

    @Test
    public void testDefaultConstructor() {
        DataSet ds = new DataSet();
        assertNull(ds.getId());
        assertEquals("Available", ds.getAvailable());
        assertEquals(Integer.valueOf(0), ds.getHolder());
    }

    @Test
    public void testParameterizedConstructor() {
        DataSet ds = new DataSet("id1", "access1", "driver1", "addr1", "public");
        assertEquals("id1", ds.getId());
        assertEquals("access1", ds.getAccessInfo());
        assertEquals("driver1", ds.getDriver());
        assertEquals("addr1", ds.getAddress());
        assertEquals("public", ds.getVisibility());
    }

    @Test
    public void testFullConstructor() {
        DataSet ds = new DataSet("id1", "access1", "driver1", "addr1", "public", 1);
        assertEquals(Integer.valueOf(1), ds.getHolder());
    }

    @Test
    public void testGetAccessInfoReturnsEmptyWhenNull() {
        DataSet ds = new DataSet();
        ds.setAccessInfo(null);
        assertEquals("", ds.getAccessInfo());
    }

    @Test
    public void testGetAccessInfoReturnsValueWhenSet() {
        DataSet ds = new DataSet();
        ds.setAccessInfo("info");
        assertEquals("info", ds.getAccessInfo());
    }

    @Test
    public void testSettersAndGetters() {
        DataSet ds = new DataSet();
        ds.setId("test-id");
        ds.setDriver("mysql");
        ds.setAddress("localhost:3306");
        ds.setVisibility("private");
        ds.setAvailable("Unavailable");
        ds.setHolder(2);
        ds.setFields("name,string;age,int");
        ds.setAccessInfo("conn");

        assertEquals("test-id", ds.getId());
        assertEquals("mysql", ds.getDriver());
        assertEquals("localhost:3306", ds.getAddress());
        assertEquals("private", ds.getVisibility());
        assertEquals("Unavailable", ds.getAvailable());
        assertEquals(Integer.valueOf(2), ds.getHolder());
        assertEquals("name,string;age,int", ds.getFields());
        assertEquals("conn", ds.getAccessInfo());
    }

    @Test
    public void testToString() {
        DataSet ds = new DataSet("id1", "acc", "drv", "addr", "pub");
        String str = ds.toString();
        assertTrue(str.contains("id1"));
        assertTrue(str.contains("acc"));
    }
}
