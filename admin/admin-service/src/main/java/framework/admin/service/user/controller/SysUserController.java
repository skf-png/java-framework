package framework.admin.service.user.controller;

import framework.admin.service.user.domain.DTO.PasswordLoginDTO;
import framework.domain.domain.VO.TokenVO;
import framework.admin.service.user.service.SysUserService;
import framework.domain.R;
import framework.security.domain.DTO.TokenDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sys_user")
public class SysUserController {
    @Autowired
    private SysUserService sysUserService;

    /**
     * ⼿机号、密码登录
     */
    @PostMapping("/login/password")
    public R<TokenVO> login(@Validated @RequestBody PasswordLoginDTO loginDTO)
    {
        // 用户登录，获取登录token
        TokenDTO tokenDTO = sysUserService.login(loginDTO);
        return R.success(tokenDTO.convertToVo());
    }
}
