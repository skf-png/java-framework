package framework.message.service;

import com.aliyun.dypnsapi20170525.Client;
import com.aliyun.dypnsapi20170525.models.SendSmsVerifyCodeRequest;
import com.aliyun.dypnsapi20170525.models.SendSmsVerifyCodeResponse;
import com.aliyun.dypnsapi20170525.models.SendSmsVerifyCodeResponseBody;
import com.google.gson.Gson;
import framework.core.utils.JsonUtils;
import framework.domain.constants.MessageConstants;
import framework.message.config.AliSmsConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
@RefreshScope
public class AliSmsService {
    @Autowired
    private Client client;

    /**
     * 短信模版代码
     */
    @Value("${sms.aliyun.templateCode:}")
    private String templateCode;

    /**
     * 签名
     */
    @Value("${sms.sing-name:}")
    private String singName;

    /**
     * 是否发送线上短信
     */
    @Value("${sms.send-message:false}")
    private boolean sendMessage;

    /**
     * 验证码的有效期，单位是分钟
     */
    @Value("${sms.code-expiration:}")
    private Long phoneCodeExpiration;

    /**
     * 发送验证码模板
     * @param phoneNumber 手机号
     * @param templateCode 使用的模板
     * @param params 参数（主要包括验证码）
     * @return
     */
    public boolean sendTempMessage(String phoneNumber, String templateCode, Map<String, String> params) {
        //由nacos配置是否发送短信
        if (!sendMessage) {
            log.error("短信发送已关闭,phone{}", phoneNumber);
            return false;
        }
        //1. 构建发送的验证码
        SendSmsVerifyCodeRequest request = new SendSmsVerifyCodeRequest()
                .setSignName(singName)
                .setTemplateCode(templateCode)
                .setPhoneNumber(phoneNumber)
                .setTemplateParam(JsonUtils.ObjectToString(params));
        //2. 发送验证码
        try {
            SendSmsVerifyCodeResponse sendSmsVerifyCodeResponse = client.sendSmsVerifyCode(request);
            SendSmsVerifyCodeResponseBody body = sendSmsVerifyCodeResponse.getBody();
            if (body.getCode().equals(MessageConstants.SMS_MSG_OK)) {
                return true;
            }
            log.error("短信{} 发送失败, 失败原因{}...", new Gson().toJson(request), body.getMessage());
            return false;
        } catch (Exception e) {
            log.error("短信{} 发送失败, 失败原因{}...", new Gson().toJson(request), e.getMessage());
            return false;
        }
    }

    /**
     * 发送短信验证码
     * @param phone 手机号
     * @param code 验证码
     * @return 是否发送成功
     */
    public boolean sendMobileCode(String phone, String code) {
        Map<String, String> params = new HashMap<>();
        params.put("code", code);
        params.put("min", phoneCodeExpiration.toString());
        return sendTempMessage(phone, templateCode, params);
    }

}
