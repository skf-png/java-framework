package framework.portal.controller;

import framework.admin.api.appuser.domain.DTO.UserEditReqDTO;
import framework.domain.R;
import framework.domain.domain.VO.TokenVO;
import framework.portal.domain.DTO.CodeLoginDTO;
import framework.portal.domain.DTO.WechatLoginDTO;
import framework.portal.domain.VO.UserVo;
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

    /**
     * 验证码登录
     * @param codeLoginDTO 验证码登录信息
     * @return token信息VO
     */
    @PostMapping("/login/code")
    public R<TokenVO> login(@RequestBody @Validated CodeLoginDTO codeLoginDTO) {
        return R.success(userService.login(codeLoginDTO).convertToVo());
    }

    /**
     * 修改用户信息
     * @param userEditReqDTO C端用户编辑DTO
     * @return token
     */
    @PostMapping("/edit")
    public R<String> edit(@RequestBody @Validated UserEditReqDTO userEditReqDTO) {
        String token = userService.edit(userEditReqDTO);
        return R.success(token);
    }

    /**
     * 获取用户登录信息
     * @return 用户信息VO
     */
    @GetMapping("/login_info/get")
    public R<UserVo> getLoginUser() {
        return R.success(userService.getLoginUser().convertToVO());
    }

    /**
     * 退出登录
     * @return void
     */
    @DeleteMapping("/logout")
    R<Void> logout() {
        userService.logout();
        return R.success();
    }
}
