package framework.utils;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;

import java.util.List;

public class StringUtil {
    /**
     * 判断字符串是否符合规则
     * @param pattern 规则
     * @param str 字符串
     * @return false表示不符合，true表示符合
     */
    public static boolean isMatch(String pattern, String str) {
        if (pattern == null || str == null) {
            return false;
        }
        AntPathMatcher matcher = new AntPathMatcher();
        return matcher.match(pattern, str);
    }

    /**
     * 判断字符串是否符合一系列的规则中的某一个
     * @param patterns 规则列表
     * @param str 字符串
     * @return false表示不符合，true表示符合
     */
    public static boolean matches(List<String> patterns, String str) {
        if (CollectionUtils.isEmpty(patterns) || str == null) {
            return false;
        }
        for (String pattern : patterns) {
            if (isMatch(pattern, str)) {
                return true;
            }
        }
        return false;
    }

}
