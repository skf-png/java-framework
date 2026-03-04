package framework.portal.controller;

import framework.domain.R;
import framework.domain.domain.VO.TokenVO;
import framework.portal.domain.DTO.WechatLoginDTO;
import framework.portal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 微信登录
     * @param wechatLoginDTO 微信登录DTO
     * @return token令牌
     */
    @PostMapping("/login/wechat")
    public R<TokenVO> login(@RequestBody @Validated WechatLoginDTO wechatLoginDTO) {
        return R.success(userService.login(wechatLoginDTO).convertToVo());
    }

    /**
     * 发送短信验证码
     * @param phone 手机号
     * @return 验证码
     */
    @GetMapping("/send_code")
    public R<String> sendCode(String phone) {
        return R.success(userService.sendCode(phone));
    }
}
