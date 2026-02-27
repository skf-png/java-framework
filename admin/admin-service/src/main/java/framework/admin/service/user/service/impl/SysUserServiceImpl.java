package framework.admin.service.user.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.nacos.shaded.com.google.common.base.Verify;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import framework.admin.service.config.service.DicService;
import framework.admin.service.user.domain.DTO.PasswordLoginDTO;
import framework.admin.service.user.domain.DTO.SysUserDTO;
import framework.admin.service.user.domain.DTO.SysUserListReqDTO;
import framework.admin.service.user.domain.DTO.SysUserLoginDTO;
import framework.admin.service.user.domain.entity.SysUser;
import framework.admin.service.user.mapper.SysUserMapper;
import framework.admin.service.user.service.SysUserService;
import framework.core.utils.AESUtil;
import framework.core.utils.BeanCopyUtil;
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

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private DicService dicService;

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

    @Override
    public Long addOrUpdate(SysUserDTO sysUserDTO) {
        SysUser sysUser = new SysUser();
        //1. id为空表示为新增
        if (sysUserDTO.getUserId() == null) {
            //2. 判断手机号格式
            if (!VerifyUtil.checkPhone(sysUserDTO.getPhoneNumber())) {
                throw new ServiceException(ResultCode.INVALID_PARA.getCode(), "手机号格式错误");
            }
            //3. 判断密码格式是否正确
            if (StringUtils.isEmpty(sysUserDTO.getPassword()) || !sysUserDTO.checkPassword()) {
                throw new ServiceException(ResultCode.INVALID_PARA.getCode(), "密码格式错误");
            }
            //4. 判断手机号是否存在
            SysUser sysUser1 = sysUserMapper.selectOne(
                    new LambdaQueryWrapper<SysUser>().eq(SysUser::getPhoneNumber, AESUtil.encryptHex(sysUserDTO.getPhoneNumber()))
            );
            if (sysUser1 != null) {
                throw new ServiceException(ResultCode.INVALID_PARA.getCode(), "手机号已经被占用");
            }
            //5. 判断身份是否存在
            if (StringUtils.isEmpty(sysUserDTO.getIdentity())
                    || dicService.getDicDataByKey(sysUserDTO.getIdentity()) == null) {
                throw new ServiceException(ResultCode.INVALID_PARA.getCode(), "身份错误");
            }
            //6. 构建sysUser对象，账号密码加密
            sysUser.setPhoneNumber(AESUtil.encryptHex(sysUserDTO.getPhoneNumber()));
            sysUser.setPassword(DigestUtil.sha256Hex(sysUserDTO.getPassword()));
            sysUser.setIdentity(sysUserDTO.getIdentity());
        }
        //7. 此时为编辑状态，只能修改备注和昵称和状态
        if (StringUtils.isEmpty(sysUserDTO.getStatus()) || dicService.getDicDataByKey(sysUserDTO.getStatus()) == null) {
            throw new ServiceException(ResultCode.INVALID_PARA.getCode(), "状态异常");
        }
        sysUser.setId(sysUserDTO.getUserId());
        sysUser.setRemark(sysUserDTO.getRemark());
        sysUser.setStatus(sysUserDTO.getStatus());
        sysUser.setNickName(sysUserDTO.getNickName());
        //8. 执行更新/新增
        sysUserMapper.insertOrUpdate(sysUser);
        return 0L;
    }

    @Override
    public List<SysUserDTO> list(SysUserListReqDTO sysUserListReqDTO) {
        List<SysUser> sysUser = null;
        //1. 如果req为空则认为查询全部用户
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        if (sysUserListReqDTO != null) {
            //2. 如果不为空，根据req的条件构建查询
            if (StringUtils.isNotEmpty(sysUserListReqDTO.getPhoneNumber())) {
                queryWrapper.eq(SysUser::getPhoneNumber, AESUtil.encryptHex(sysUserListReqDTO.getPhoneNumber()));
            }
            if (StringUtils.isNotEmpty(sysUserListReqDTO.getStatus())) {
                queryWrapper.eq(SysUser::getStatus, sysUserListReqDTO.getStatus());
            }
            if (sysUserListReqDTO.getUserId() != null) {
                queryWrapper.eq(SysUser::getId, sysUserListReqDTO.getUserId());
            }
        }

        sysUser = sysUserMapper.selectList(queryWrapper);

        return sysUser.stream()
                .map(SysUser::convertDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SysUserLoginDTO getLoginInfo() {
        //1. 获取user登录信息
        LoginUserDTO loginUser = tokenService.getLoginUser();
        if (loginUser == null || loginUser.getUserId() == null) {
            throw new ServiceException(ResultCode.INVALID_PARA.getCode(), "用户登录状态已过期");
        }
        //2. 根据登录信息查询数据库
        SysUser sysUser = sysUserMapper.selectById(loginUser.getUserId());
        //3. 返回结果
        SysUserLoginDTO sysUserLoginDTO = new SysUserLoginDTO();
        BeanCopyUtil.copyProperties(loginUser, sysUserLoginDTO);
        BeanCopyUtil.copyProperties(sysUser, sysUserLoginDTO);

        return sysUserLoginDTO;
    }
}
