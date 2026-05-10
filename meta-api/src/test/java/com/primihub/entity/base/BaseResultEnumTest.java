package com.primihub.entity.base;

import org.junit.Test;
import static org.junit.Assert.*;

public class BaseResultEnumTest {

    @Test
    public void testSuccessValues() {
        assertEquals(Integer.valueOf(0), BaseResultEnum.SUCCESS.getReturnCode());
        assertEquals("请求成功", BaseResultEnum.SUCCESS.getMessage());
    }

    @Test
    public void testFailureValues() {
        assertEquals(Integer.valueOf(-1), BaseResultEnum.FAILURE.getReturnCode());
        assertEquals("请求异常", BaseResultEnum.FAILURE.getMessage());
    }

    @Test
    public void testLackOfParamValues() {
        assertEquals(Integer.valueOf(100), BaseResultEnum.LACK_OF_PARAM.getReturnCode());
        assertEquals("缺少参数", BaseResultEnum.LACK_OF_PARAM.getMessage());
    }

    @Test
    public void testAllEnumsHaveValues() {
        for (BaseResultEnum e : BaseResultEnum.values()) {
            assertNotNull(e.getReturnCode());
            assertNotNull(e.getMessage());
        }
    }
}
