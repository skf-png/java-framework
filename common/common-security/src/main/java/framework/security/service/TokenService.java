package framework.security.service;

import framework.core.utils.ServletUtil;
import framework.domain.constants.CacheConstants;
import framework.domain.constants.SecurityConstants;
import framework.domain.constants.TokenConstants;
import framework.redis.service.RedisService;
import framework.security.domain.DTO.LoginUserDTO;
import framework.security.domain.DTO.TokenDTO;
import framework.security.utils.JwtUtil;
import framework.security.utils.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class TokenService {
    /**
     * 毫秒
     */
    private final static long MILLIS_SECOND = 1000;

    /**
     * 分钟
     */
    private final static long MILLIS_MINUTE = 60 * MILLIS_SECOND;

    /**
     * 过期时间
     */
    private final static Long EXPIRE_TIME = CacheConstants.EXPIRATION;

    /**
     * token的KEY前缀
     */
    private final static String ACCESS_TOKEN = TokenConstants.LOGIN_TOKEN_KEY;

    /**
     * 缓存刷新时间（单位：分钟）
     */
    private final static Long MILLIS_MINUTE_TEN = CacheConstants.REFRESH_TIME * MILLIS_MINUTE;

    @Autowired
    private RedisService redisService;

    /**
     * 验证有效期是否小于缓存刷新时间，小于则刷新
     * @param loginUserDTO
     */
    public void verifyToken(LoginUserDTO loginUserDTO) {
        long currentTime = System.currentTimeMillis();
        long expireTime = loginUserDTO.getExpireTime();
        if (expireTime -  currentTime <= MILLIS_MINUTE_TEN) {
            refreshToken(loginUserDTO);
        }
    }
    /**
     * 设置信息，允许用户登录
     * @param loginUserDTO
     */
    public void setLoginUser(LoginUserDTO loginUserDTO) {
        if (loginUserDTO != null && StringUtils.isNotEmpty(loginUserDTO.getUserKey())) {
            refreshToken(loginUserDTO);
        }
    }
    /**
     * 缓存用户登录有效期
     * @param loginUserDTO
     */
    public void refreshToken(LoginUserDTO loginUserDTO) {
        delLoginUser();
        loginUserDTO.setLoginTime(System.currentTimeMillis());
        loginUserDTO.setExpireTime(loginUserDTO.getLoginTime() + EXPIRE_TIME * MILLIS_MINUTE);
        String userKey = getUserKey(loginUserDTO.getUserKey());
        redisService.setCacheObject(userKey, loginUserDTO, EXPIRE_TIME, TimeUnit.MINUTES);
    }

    /**
     * 从token中获取user信息
     * @param token
     * @return
     */
    public LoginUserDTO getLoginUser(String token) {
        LoginUserDTO loginUserDTO = null;
        try {
            if (StringUtils.isNotEmpty(token)) {
                String userKey = JwtUtil.getUserKey(token);
                loginUserDTO = redisService.getCacheObject(getUserKey(userKey), LoginUserDTO.class);
                return  loginUserDTO;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * 从请求信息获取user信息
     * @param request
     * @return
     */
    public LoginUserDTO getLoginUser(HttpServletRequest request) {
        String token = SecurityUtil.getToken(request);
        return getLoginUser(token);
    }

    /**
     * 无参获取user信息
     * @return
     */
    public LoginUserDTO getLoginUser() {
        return getLoginUser(SecurityUtil.getToken());
    }

    /**
     * 根据user信息创建token
     * @param loginUserDTO
     * @return
     */
    public TokenDTO createToken(LoginUserDTO loginUserDTO) {
        //随机设置userKey
        String userKey = UUID.randomUUID().toString();
        loginUserDTO.setUserKey(userKey);
        refreshToken(loginUserDTO);
        //创建user的信息map
        Map<String, Object> claims = new HashMap<>();
        claims.put(SecurityConstants.USER_KEY, userKey);
        claims.put(SecurityConstants.USER_ID, loginUserDTO.getUserId());
        claims.put(SecurityConstants.USERNAME, loginUserDTO.getUserName());
        claims.put(SecurityConstants.USER_FROM, loginUserDTO.getUserFrom());
        //生成token
        TokenDTO tokenDTO = new TokenDTO();
        tokenDTO.setAccessToken(JwtUtil.createToken(claims));
        tokenDTO.setExpires(EXPIRE_TIME);
        return tokenDTO;
    }

    /**
     * 根据令牌删除用户登录态
     * @param token 令牌
     */
    public void delLoginUser(String token) {
        if (StringUtils.isNotEmpty(token)) {
            String useKey = JwtUtil.getUserKey(token);
            redisService.deleteObject(getUserKey(useKey));
        }
    }

    /**
     * 删除自己用户的登录态
     */
    public void delLoginUser() {
        String token = SecurityUtil.getToken();
        if (StringUtils.isNotEmpty(token)) {
            String useKey = JwtUtil.getUserKey(token);
            redisService.deleteObject(getUserKey(useKey));
        }
    }

    /**
     * 允许超管删除别人的登录状态
     * @param userId 用户ID
     * @param userFrom 用户来源
     */
    public void delLoginUser(Long userId, String userFrom) {
        if (userId == null) return;
        // 遍历redis里面的key删除
        Collection<String> tokenKeys = redisService.keys(ACCESS_TOKEN + "*");
        for (String tokenKey : tokenKeys) {
            LoginUserDTO user = redisService.getCacheObject(tokenKey, LoginUserDTO.class);
            if (user != null && user.getUserId().equals(userId) && user.getUserFrom().equals(userFrom)) {
                redisService.deleteObject(tokenKey);
            }
        }
    }

    /**
     * 返回存储token的主键
     * @param token
     * @return
     */
    public String getUserKey(String token) {
        return ACCESS_TOKEN + token;
    }
}
