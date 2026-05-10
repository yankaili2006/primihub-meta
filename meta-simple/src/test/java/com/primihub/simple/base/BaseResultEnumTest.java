package com.primihub.simple.base;

import org.junit.Test;
import static org.junit.Assert.*;

public class BaseResultEnumTest {

    @Test
    public void testEnumValues() {
        assertEquals(Integer.valueOf(0), BaseResultEnum.SUCCESS.getReturnCode());
        assertEquals("请求成功", BaseResultEnum.SUCCESS.getMessage());
        assertEquals(Integer.valueOf(-1), BaseResultEnum.FAILURE.getReturnCode());
        assertEquals("请求异常", BaseResultEnum.FAILURE.getMessage());
        assertEquals(Integer.valueOf(100), BaseResultEnum.LACK_OF_PARAM.getReturnCode());
        assertEquals("缺少参数", BaseResultEnum.LACK_OF_PARAM.getMessage());
    }

    @Test
    public void testAllEnumsImplementInterface() {
        for (BaseResultEnum e : BaseResultEnum.values()) {
            assertTrue(e instanceof ResultEnumType);
        }
    }
}
