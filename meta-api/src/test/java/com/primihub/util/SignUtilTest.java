package com.primihub.util;

import org.junit.Test;
import static org.junit.Assert.*;

public class SignUtilTest {

    @Test
    public void testGetMD5ValueUpperCaseByDefaultEncode() {
        String result = SignUtil.getMD5ValueUpperCaseByDefaultEncode("hello");
        assertEquals("5D41402ABC4B2A76B9719D911017C592", result);
    }

    @Test
    public void testGetMD5ValueLowerCaseByDefaultEncode() {
        String result = SignUtil.getMD5ValueLowerCaseByDefaultEncode("hello");
        assertEquals("5d41402abc4b2a76b9719d911017c592", result);
    }

    @Test
    public void testMD5EmptyString() {
        String upper = SignUtil.getMD5ValueUpperCaseByDefaultEncode("");
        String lower = SignUtil.getMD5ValueLowerCaseByDefaultEncode("");
        assertEquals("D41D8CD98F00B204E9800998ECF8427E", upper);
        assertEquals("d41d8cd98f00b204e9800998ecf8427e", lower);
    }

    @Test
    public void testGetSha1ValueLowerCaseByDefaultEncode() {
        String result = SignUtil.getSha1ValueLowerCaseByDefaultEncode("hello");
        assertEquals("aaf4c61ddcc5e8a2dabede0f3b482cd9aea9434d", result);
    }

    @Test
    public void testGetSha1ValueUpperCaseByDefaultEncode() {
        String result = SignUtil.getSha1ValueUpperCaseByDefaultEncode("hello");
        assertEquals("AAF4C61DDCC5E8A2DABEDE0F3B482CD9AEA9434D", result);
    }

    @Test
    public void testGetSha256ValueLowerCaseByDefaultEncode() {
        String result = SignUtil.getSha256ValueLowerCaseByDefaultEncode("hello");
        assertEquals("2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824", result);
    }

    @Test
    public void testGetSha256ValueUpperCaseByDefaultEncode() {
        String result = SignUtil.getSha256ValueUpperCaseByDefaultEncode("hello");
        assertEquals("2CF24DBA5FB0A30E26E83B2AC5B9E29E1B161E5C1FA7425E73043362938B9824", result);
    }

    @Test
    public void testGetSha512ValueLowerCaseByDefaultEncode() {
        String result = SignUtil.getSha512ValueLowerCaseByDefaultEncode("hello");
        assertNotNull(result);
        assertEquals(128, result.length());
    }

    @Test
    public void testGetSha512ValueUpperCaseByDefaultEncode() {
        String result = SignUtil.getSha512ValueUpperCaseByDefaultEncode("hello");
        assertNotNull(result);
        assertEquals(128, result.length());
    }

    @Test
    public void testChineseCharacters() {
        String md5 = SignUtil.getMD5ValueUpperCaseByDefaultEncode("你好");
        assertNotNull(md5);
        assertEquals(32, md5.length());
    }

    @Test
    public void testSpecialCharacters() {
        String md5 = SignUtil.getMD5ValueUpperCaseByDefaultEncode("!@#$%^&*()");
        assertNotNull(md5);
        assertEquals(32, md5.length());
    }
}
