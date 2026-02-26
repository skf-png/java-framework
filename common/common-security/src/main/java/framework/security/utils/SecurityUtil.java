package framework.security.utils;

import framework.core.utils.ServletUtil;
import framework.domain.constants.SecurityConstants;
import framework.domain.constants.TokenConstants;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;

/**
 * 安全工具类
 */
public class SecurityUtil {
    /**
     * 无参获取token
     * @return
     */
    public static String getToken() {
        return getToken(ServletUtil.getRequest());
    }
    /**
     * 根据请求获取token
     * @param request 请求信息
     * @return token
     */
    public static String getToken(HttpServletRequest request) {
        String header = request.getHeader(SecurityConstants.AUTHENTICATION);
        return replaceTokenPrefix(header);
    }

    /**
     * 替换前缀
     * @param token token
     * @return 去掉前缀后的token
     */
    public static String replaceTokenPrefix(String token) {
        if (StringUtils.isNotEmpty(token) && token.startsWith(TokenConstants.PREFIX)) {
            return token.substring(TokenConstants.PREFIX.length());
        }
        return token;
    }
}
