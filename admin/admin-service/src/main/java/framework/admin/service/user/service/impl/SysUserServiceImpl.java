package framework.admin.service.user.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.nacos.shaded.com.google.common.base.Verify;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import framework.admin.service.user.domain.DTO.PasswordLoginDTO;
import framework.admin.service.user.domain.entity.SysUser;
import framework.admin.service.user.mapper.SysUserMapper;
import framework.admin.service.user.service.SysUserService;
import framework.core.utils.AESUtil;
import framework.core.utils.VerifyUtil;
import framework.domain.ResultCode;
import framework.domain.ServiceException;
import framework.security.domain.DTO.LoginUserDTO;
import framework.security.domain.DTO.TokenDTO;
import framework.security.service.TokenService;
import framework.security.utils.JwtUtil;
import framework.security.utils.SecurityUtil;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private TokenService tokenService;

    @Override
    public TokenDTO login(PasswordLoginDTO loginDTO) {
        //1. 判空
        if (loginDTO == null || StringUtils.isEmpty(loginDTO.getPassword())
                || StringUtils.isEmpty(loginDTO.getPhone())) {
            throw new ServiceException(ResultCode.INVALID_PARA);
        }
        //2. 检测手机号是否正确
        if (!VerifyUtil.checkPhone(loginDTO.getPhone())) {
            throw new ServiceException(ResultCode.INVALID_PARA.getCode(), "手机号格式不正确");
        }
        //3. 判断手机号是否存在
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getPhoneNumber, AESUtil.encryptHex(loginDTO.getPhone()))
                .eq(SysUser::getStatus, "enable");
        SysUser sysUser = sysUserMapper.selectOne(queryWrapper);
        if (sysUser == null) {
            throw new ServiceException(ResultCode.INVALID_PARA.getCode(), "手机号不存在");
        }
        //4. 判断密码是否正确
        String password = AESUtil.decryptHex(loginDTO.getPassword());
        if (StringUtils.isEmpty(password)) {
            throw new ServiceException(ResultCode.INVALID_PARA.getCode(), "密码解析错误");
        }
        String passwordEncrypt = DigestUtil.sha256Hex(password);
        if (!passwordEncrypt.equals(sysUser.getPassword())) {
            throw new ServiceException(ResultCode.INVALID_PARA.getCode(), "密码错误");
        }
        //5. 创建token
        LoginUserDTO loginUserDTO = new LoginUserDTO();
        loginUserDTO.setUserId(sysUser.getId());
        loginUserDTO.setUserName(sysUser.getNickName());
        loginUserDTO.setUserFrom("sys");
        return tokenService.createToken(loginUserDTO);
    }
}
