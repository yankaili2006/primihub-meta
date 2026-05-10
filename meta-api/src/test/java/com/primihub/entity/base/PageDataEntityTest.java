package com.primihub.entity.base;

import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.*;

public class PageDataEntityTest {

    @Test
    public void testConstructorAndGetters() {
        List<String> data = Arrays.asList("a", "b", "c");
        PageDataEntity page = new PageDataEntity(100, 10, 1, data);
        assertEquals(100, page.getTotal());
        assertEquals(10, page.getPageSize());
        assertEquals(1, page.getIndex());
        assertEquals(data, page.getData());
    }

    @Test
    public void testTotalPageExactDivision() {
        PageDataEntity page = new PageDataEntity(100, 10, 1, new ArrayList());
        assertEquals(10, page.getTotalPage());
    }

    @Test
    public void testTotalPageWithRemainder() {
        PageDataEntity page = new PageDataEntity(101, 10, 1, new ArrayList());
        assertEquals(11, page.getTotalPage());
    }

    @Test
    public void testTotalPageWhenZero() {
        PageDataEntity page = new PageDataEntity(0, 10, 1, new ArrayList());
        assertEquals(0, page.getTotalPage());
    }

    @Test
    public void testTotalPageLessThanPageSize() {
        PageDataEntity page = new PageDataEntity(3, 10, 1, new ArrayList());
        assertEquals(1, page.getTotalPage());
    }

    @Test
    public void testSetters() {
        PageDataEntity page = new PageDataEntity(0, 0, 0, null);
        page.setTotal(50);
        page.setPageSize(20);
        page.setIndex(2);
        List<String> data = Arrays.asList("x");
        page.setData(data);

        assertEquals(50, page.getTotal());
        assertEquals(20, page.getPageSize());
        assertEquals(2, page.getIndex());
        assertEquals(data, page.getData());
        assertEquals(3, page.getTotalPage());
    }
}
