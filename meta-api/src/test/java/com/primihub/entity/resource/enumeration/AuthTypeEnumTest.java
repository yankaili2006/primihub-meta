package com.primihub.entity.resource.enumeration;

import org.junit.Test;
import static org.junit.Assert.*;

public class AuthTypeEnumTest {

    @Test
    public void testEnumValues() {
        assertEquals(Integer.valueOf(1), AuthTypeEnum.PUBLIC.getAuthType());
        assertEquals("公开", AuthTypeEnum.PUBLIC.getAuthName());

        assertEquals(Integer.valueOf(2), AuthTypeEnum.PRIVATE.getAuthType());
        assertEquals("私有", AuthTypeEnum.PRIVATE.getAuthName());

        assertEquals(Integer.valueOf(3), AuthTypeEnum.VISIBILITY.getAuthType());
        assertEquals("可见性", AuthTypeEnum.VISIBILITY.getAuthName());
    }

    @Test
    public void testAuthTypeMap() {
        assertSame(AuthTypeEnum.PUBLIC, AuthTypeEnum.AUTH_TYPE_MAP.get(1));
        assertSame(AuthTypeEnum.PRIVATE, AuthTypeEnum.AUTH_TYPE_MAP.get(2));
        assertSame(AuthTypeEnum.VISIBILITY, AuthTypeEnum.AUTH_TYPE_MAP.get(3));
        assertNull(AuthTypeEnum.AUTH_TYPE_MAP.get(999));
    }
}
