package com.primihub.entity.resource.param;

import org.junit.Test;
import static org.junit.Assert.*;

public class PageParamTest {

    @Test
    public void testDefaultValues() {
        PageParam param = new PageParam();
        assertEquals(Integer.valueOf(1), param.getPageNo());
        assertEquals(Integer.valueOf(5), param.getPageSize());
        assertEquals(Integer.valueOf(0), param.getOffset());
    }

    @Test
    public void testOffsetComputedCorrectly() {
        PageParam param = new PageParam();
        param.setPageNo(1);
        param.setPageSize(10);
        assertEquals(Integer.valueOf(0), param.getOffset());

        param.setPageNo(2);
        assertEquals(Integer.valueOf(10), param.getOffset());

        param.setPageNo(3);
        assertEquals(Integer.valueOf(20), param.getOffset());
    }

    @Test
    public void testSetters() {
        PageParam param = new PageParam();
        param.setPageNo(3);
        param.setPageSize(15);
        assertEquals(Integer.valueOf(3), param.getPageNo());
        assertEquals(Integer.valueOf(15), param.getPageSize());
        assertEquals(Integer.valueOf(30), param.getOffset());
    }
}
