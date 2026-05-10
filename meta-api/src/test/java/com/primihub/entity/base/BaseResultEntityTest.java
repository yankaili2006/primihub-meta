package com.primihub.entity.base;

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
        assertEquals(BaseResultEnum.SUCCESS.getReturnCode(), result.getCode());
        assertEquals(BaseResultEnum.SUCCESS.getMessage(), result.getMsg());
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
        assertEquals(BaseResultEnum.LACK_OF_PARAM.getReturnCode(), result.getCode());
        assertEquals(BaseResultEnum.LACK_OF_PARAM.getMessage(), result.getMsg());
    }

    @Test
    public void testFailureWithExtraInfo() {
        BaseResultEntity result = BaseResultEntity.failure(BaseResultEnum.LACK_OF_PARAM, "resourceId");
        assertEquals(BaseResultEnum.LACK_OF_PARAM.getReturnCode(), result.getCode());
        assertTrue(result.getMsg().contains("resourceId"));
    }

    @Test
    public void testFailureWithExtraAndConfig() {
        BaseResultEntity result = BaseResultEntity.failure(BaseResultEnum.LACK_OF_PARAM, "paramName", "configDetail");
        assertEquals(BaseResultEnum.LACK_OF_PARAM.getReturnCode(), result.getCode());
        assertTrue(result.getMsg().contains("paramName"));
        assertEquals("configDetail", result.getExtra());
    }

    @Test
    public void testConstructorWithResultEnumType() {
        BaseResultEntity entity = new BaseResultEntity(BaseResultEnum.FAILURE);
        assertEquals(BaseResultEnum.FAILURE.getReturnCode(), entity.getCode());
        assertEquals(BaseResultEnum.FAILURE.getMessage(), entity.getMsg());
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

    @Test
    public void testConstructorWithCodeMsg() {
        BaseResultEntity entity = new BaseResultEntity(1, "error");
        assertEquals(Integer.valueOf(1), entity.getCode());
        assertEquals("error", entity.getMsg());
    }
}
