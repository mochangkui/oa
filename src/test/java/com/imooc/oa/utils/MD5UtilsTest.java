package com.imooc.oa.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MD5UtilsTest {

    @Test
    void md5Digest() {
        String result = MD5Utils.md5Digest("test");
        System.out.println(result);
    }
}