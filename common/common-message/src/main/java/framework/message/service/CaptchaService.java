package framework.message.service;

import framework.core.utils.VerifyUtil;
import framework.domain.ResultCode;
import framework.domain.ServiceException;
import framework.domain.constants.MessageConstants;
import framework.redis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

@Component
@RefreshScope
@Slf4j
public class CaptchaService {
    @Autowired
    private RedisService redisService;

    /**
     * 单个手机号，每日发送短信次数的限制
     */
    @Value("${sms.send-limit:}")
    private Integer sendLimit;

    /**
     * 验证码的有效期，单位是分钟
     */
    @Value("${sms.code-expiration:}")
    private Long phoneCodeExpiration;

    /**
     * 用来判断是否发送随机验证码
     */
    @Value("${sms.send-message:false}")
    private boolean sendMessage;

    /**
     * 阿里云短信服务
     */
    @Autowired
    private AliSmsService aliSmsService;

    /**
     * 发送手机验证码
     * @param phone 手机号
     * @return 生成的验证码
     */
    public String sendCode(String phone) {
        //1. 单日次数限制
        String dayTimesKey = MessageConstants.SMS_CODE_TIMES_KEY + phone;
        Integer dayTimes = redisService.getCacheObject(dayTimesKey, Integer.class);
        dayTimes = dayTimes == null ? 0 : dayTimes;
        if (dayTimes >= sendLimit) {
            throw new ServiceException(ResultCode.SEND_MSG_OVERLIMIT);
        }
        //2. 频率限制（1分钟）
        String codeKey = MessageConstants.SMS_CODE_KEY + phone;
        String value = redisService.getCacheObject(codeKey, String.class);
        long expireTime = redisService.getExpire(codeKey);
        if (StringUtils.isNotEmpty(value) && expireTime + 60 > phoneCodeExpiration * 60) {
            long time = expireTime + 60 - phoneCodeExpiration * 60;
            throw new ServiceException(ResultCode.INVALID_PARA.getCode(), "操作频繁，请在" + time + "秒后重新尝试");
        }
        //3. 是否在线上发送短信
        String code = sendMessage ? VerifyUtil.generateVerifyCode(MessageConstants.DEFAULT_SMS_LENGTH) : MessageConstants.DEFAULT_SMS_CODE;
        if (sendMessage) {
            boolean res = aliSmsService.sendMobileCode(phone, code);
            if (!res) {
                throw new ServiceException(ResultCode.SEND_MSG_FAILED);
            }
        }
        //4. 设置缓存
        redisService.setCacheObject(codeKey, code, phoneCodeExpiration, TimeUnit.MINUTES);
        long seconds = ChronoUnit.SECONDS.between(LocalDateTime.now(),
                LocalDateTime.now().plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0));
        redisService.setCacheObject(dayTimesKey, dayTimes + 1, seconds, TimeUnit.SECONDS);
        return code;
    }

    /**
     * 从缓存中获取手机号的验证码
     * @param phone 手机号
     * @return 验证码
     */
    public String getCode(String phone) {
        String cacheKey = MessageConstants.SMS_CODE_KEY + phone;
        return redisService.getCacheObject(cacheKey, String.class);
    }

    /**
     * 从缓存中删除手机号的验证码
     * @param phone 手机号
     * @return 验证码
     */
    public boolean deleteCode(String phone) {
        String cacheKey = MessageConstants.SMS_CODE_KEY + phone;
        return redisService.deleteObject(cacheKey);
    }

    /**
     * 校验手机号与验证码是否匹配
     * @param phone 手机号
     * @param code 验证码
     * @return 布尔类型
     */
    public boolean checkCode(String phone, String code) {
        if (getCode(phone) == null || StringUtils.isEmpty(getCode(phone))) {
            throw new ServiceException(ResultCode.INVALID_CODE);
        }
        return getCode(phone).equals(code);
    }
}
