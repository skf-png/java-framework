package framework.admin.service.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import framework.admin.api.appuser.domain.DTO.AppUserDTO;
import framework.admin.service.user.domain.entity.AppUser;
import framework.admin.service.user.mapper.AppUserMapper;
import framework.admin.service.user.service.AppUserService;
import framework.core.utils.AESUtil;
import framework.domain.ResultCode;
import framework.domain.ServiceException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

@Service
@RefreshScope
public class AppUserServiceImpl implements AppUserService {
    @Autowired
    private AppUserMapper appUserMapper;

    @Value("${appuser.info.defaultAvatar}")
    private String defaultAvatar;

    @Override
    public AppUserDTO registerByOpenId(String openId) {
        //1. 参数校验
        if (StringUtils.isEmpty(openId)) {
            throw new ServiceException(ResultCode.INVALID_PARA.getCode(), "openId不能为空");
        }
        //2. 查重
        if (appUserMapper.selectOne(new LambdaQueryWrapper<AppUser>()
                .eq(AppUser::getOpenId, openId)) != null) {
            throw new ServiceException(ResultCode.INVALID_PARA.getCode(), "该用户已注册");
        }
        //3. 新增
        AppUser appUser = new AppUser();
        appUser.setOpenId(openId);
        appUser.setNickName("user_" +(int)(Math.random() * 9000) + 1000);
        appUser.setAvatar(defaultAvatar);
        appUserMapper.insert(appUser);
        //4. 返回结果
        AppUserDTO appUserDTO = new AppUserDTO();
        BeanUtils.copyProperties(appUser, appUserDTO);
        appUserDTO.setUserId(appUser.getId());
        return appUserDTO;
    }

    @Override
    public AppUserDTO findByOpenId(String openId) {
        //1. 参数校验
        if (StringUtils.isEmpty(openId)) {
            return null;
        }
        //2. 查询openId
        AppUser appUser = appUserMapper.selectOne(new LambdaQueryWrapper<AppUser>().eq(AppUser::getOpenId, openId));
        if (appUser == null) {
            return null;
        }
        //3. 返回结果
        AppUserDTO appUserDTO = new AppUserDTO();
        BeanUtils.copyProperties(appUser, appUserDTO);
        appUserDTO.setUserId(appUser.getId());
        //4. 手机号解密
        appUserDTO.setPhoneNumber(AESUtil.decryptHex(appUser.getPhoneNumber()));
        return appUserDTO;
    }

    @Override
    public AppUserDTO findByPhone(String phoneNumber) {
        //1. 校验
        if (StringUtils.isEmpty(phoneNumber)) {
            return null;
        }
        //2. 查询(手机号加密)
        AppUser user = appUserMapper.selectOne(new  LambdaQueryWrapper<AppUser>()
                .eq(AppUser::getPhoneNumber, AESUtil.encryptHex(phoneNumber)));
        if (user == null) {
            return null;
        }
        //3. 参数转换(手机号解密)
        AppUserDTO appUserDTO = new AppUserDTO();
        BeanUtils.copyProperties(user, appUserDTO);
        appUserDTO.setUserId(user.getId());
        appUserDTO.setPhoneNumber(AESUtil.decryptHex(user.getPhoneNumber()));
        return appUserDTO;
    }

    @Override
    public AppUserDTO registerByPhone(String phoneNumber) {
        //1. 参数校验
        if (StringUtils.isEmpty(phoneNumber)) {
            throw new ServiceException("手机号不能为空！");
        }
        //2. 新增
        AppUser appUser = new AppUser();
        appUser.setPhoneNumber(AESUtil.encryptHex(phoneNumber));
        appUser.setNickName("user_" +(int)(Math.random() * 9000) + 1000);
        appUser.setAvatar(defaultAvatar);
        appUserMapper.insert(appUser);
        //3. 返回结果
        AppUserDTO appUserDTO = new AppUserDTO();
        BeanUtils.copyProperties(appUser, appUserDTO);
        appUserDTO.setUserId(appUser.getId());
        appUserDTO.setPhoneNumber(phoneNumber);
        return appUserDTO;
    }
}
