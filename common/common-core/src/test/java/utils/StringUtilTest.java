package utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringUtilTest {

    @Test
    void isMatch() {
        String str1 = "/a/b/as/c";
        String pattern = "/a/**/c";
        System.out.println(StringUtil.isMatch(pattern, str1));
    }
}