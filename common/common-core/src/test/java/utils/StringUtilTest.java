package utils;

import framework.core.utils.StringUtil;
import org.junit.jupiter.api.Test;

class StringUtilTest {

    @Test
    void isMatch() {
        String str1 = "/a/b/as/c";
        String pattern = "/a/**/c";
        System.out.println(StringUtil.isMatch(pattern, str1));
    }
}