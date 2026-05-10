package com.primihub.simple.base;

import org.junit.Test;
import static org.junit.Assert.*;

public class BaseResultEntityTest {

    @Test
    public void testDefaultConstructor() {
        BaseResultEntity entity = new BaseResultEntity();
        assertNull(entity.getCode());
        assertNull(entity.getMsg());
        assertNull(entity.getResult());
    }

    @Test
    public void testSuccessStatic() {
        BaseResultEntity result = BaseResultEntity.success();
        assertEquals(Integer.valueOf(0), result.getCode());
        assertEquals("请求成功", result.getMsg());
    }

    @Test
    public void testSuccessWithResult() {
        BaseResultEntity result = BaseResultEntity.success("hello");
        assertEquals(Integer.valueOf(0), result.getCode());
        assertEquals("hello", result.getResult());
    }

    @Test
    public void testSuccessWithResultAndExtra() {
        BaseResultEntity result = BaseResultEntity.success("data", "extraInfo");
        assertEquals("data", result.getResult());
        assertEquals("extraInfo", result.getExtra());
    }

    @Test
    public void testFailureWithEnum() {
        BaseResultEntity result = BaseResultEntity.failure(BaseResultEnum.LACK_OF_PARAM);
        assertEquals(Integer.valueOf(100), result.getCode());
        assertEquals("缺少参数", result.getMsg());
    }

    @Test
    public void testFailureWithExtraInfo() {
        BaseResultEntity result = BaseResultEntity.failure(BaseResultEnum.LACK_OF_PARAM, "paramName");
        assertEquals(Integer.valueOf(100), result.getCode());
        assertTrue(result.getMsg().contains("paramName"));
    }

    @Test
    public void testFailureWithExtraAndConfig() {
        BaseResultEntity result = BaseResultEntity.failure(BaseResultEnum.FAILURE, "errorMsg", "configDetail");
        assertEquals(Integer.valueOf(-1), result.getCode());
        assertTrue(result.getMsg().contains("errorMsg"));
        assertEquals("configDetail", result.getExtra());
    }

    @Test
    public void testConstructorWithResultOnly() {
        BaseResultEntity entity = new BaseResultEntity(42);
        assertEquals(Integer.valueOf(0), entity.getCode());
        assertEquals(42, entity.getResult());
    }

    @Test
    public void testConstructorWithCodeMsgResult() {
        BaseResultEntity entity = new BaseResultEntity(1, "error", "data");
        assertEquals(Integer.valueOf(1), entity.getCode());
        assertEquals("error", entity.getMsg());
        assertEquals("data", entity.getResult());
    }
}
