package framework.admin.service.user.controller;

import framework.admin.service.user.domain.DTO.PasswordLoginDTO;
import framework.admin.service.user.domain.DTO.SysUserDTO;
import framework.admin.service.user.domain.DTO.SysUserListReqDTO;
import framework.admin.service.user.domain.DTO.SysUserLoginDTO;
import framework.admin.service.user.domain.VO.SysUserLoginVO;
import framework.admin.service.user.domain.VO.SysUserVo;
import framework.admin.service.user.domain.entity.SysUser;
import framework.domain.domain.VO.TokenVO;
import framework.admin.service.user.service.SysUserService;
import framework.domain.R;
import framework.security.domain.DTO.TokenDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sys_user")
public class SysUserController {
    @Autowired
    private SysUserService sysUserService;

    /**
     * ⼿机号、密码登录
     */
    @PostMapping("/login/password")
    public R<TokenVO> login(@Validated @RequestBody PasswordLoginDTO loginDTO) {
        // 用户登录，获取登录token
        TokenDTO tokenDTO = sysUserService.login(loginDTO);
        return R.success(tokenDTO.convertToVo());
    }

    /**
     * 新增或者更新sys用户
     * @param sysUserDTO
     * @return
     */
    @PostMapping("/add_edit")
    public R<Long> addOrUpdate(@Validated @RequestBody SysUserDTO sysUserDTO) {
        Long res = sysUserService.addOrUpdate(sysUserDTO);
        return R.success(res);
    }

    /**
     * 查询用户
     * @param sysUserListReqDTO 查询信息
     * @return 查询用户
     */
    @PostMapping("/list")
    public R<List<SysUserVo>> list(@Validated @RequestBody(required = false) SysUserListReqDTO sysUserListReqDTO) {
        List<SysUserDTO> res = sysUserService.list(sysUserListReqDTO);
        return R.success(res.stream()
                .map(SysUserDTO::convertToVO)
                .collect(Collectors.toList()));
    }

    /**
     * 获取用户登录信息
     * @return
     */
    @GetMapping("/login_info/get")
    public R<SysUserLoginVO> getLoginInfo() {
        SysUserLoginDTO res = sysUserService.getLoginInfo();
        return R.success(res.convertToVO());
    }
}
