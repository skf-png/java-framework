package framework.admin.service.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import framework.admin.api.appuser.domain.DTO.AppUserDTO;
import framework.admin.api.appuser.domain.DTO.AppUserListReqDTO;
import framework.admin.api.appuser.domain.DTO.UserEditReqDTO;
import framework.admin.service.user.config.RabbitConfig;
import framework.admin.service.user.domain.entity.AppUser;
import framework.admin.service.user.mapper.AppUserMapper;
import framework.admin.service.user.service.AppUserService;
import framework.core.DTO.BasePageDTO;
import framework.core.utils.AESUtil;
import framework.domain.ResultCode;
import framework.domain.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RefreshScope
public class AppUserServiceImpl implements AppUserService {
    @Autowired
    private AppUserMapper appUserMapper;

    @Value("${appuser.info.defaultAvatar}")
    private String defaultAvatar;

    @Autowired
    private RabbitTemplate rabbitTemplate;

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

    @Override
    public void edit(UserEditReqDTO userEditReqDTO) {
        //1. 查询id是否存在
        AppUser appUser = appUserMapper.selectOne(new LambdaQueryWrapper<AppUser>().eq(AppUser::getId, userEditReqDTO.getUserId()));
        if (appUser == null) {
            throw new ServiceException(ResultCode.INVALID_PARA.getCode(), "该用户不存在!");
        }
        //2. 构建更新类
        appUser.setNickName(userEditReqDTO.getNickName());
        appUser.setAvatar(defaultAvatar);
        appUserMapper.updateById(appUser);
        //3. 发送广播信息
        AppUserDTO appUserDTO = new AppUserDTO();
        BeanUtils.copyProperties(appUser, appUserDTO);
        try {
            rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_NAME, "",  appUserDTO);
        } catch (Exception e) {
            log.error("编辑用户发送消息失败", e.getMessage());
        }

    }

    @Override
    public AppUserDTO findById(Long userId) {
        //1. 判空
        if (userId == null) {
            return null;
        }
        //2. 查询
        AppUser appUser = appUserMapper.selectById(userId);
        if (appUser == null) {
            return null;
        }
        //3. 转换
        AppUserDTO appUserDTO = new AppUserDTO();
        BeanUtils.copyProperties(appUser, appUserDTO);
        appUserDTO.setUserId(appUser.getId());
        appUserDTO.setPhoneNumber(AESUtil.decryptHex(appUser.getPhoneNumber()));
        return appUserDTO;
    }

    @Override
    public List<AppUserDTO> getUserList(List<Long> userIds) {
        //1. 参数校验
        if (CollectionUtils.isEmpty(userIds)) {
            return Arrays.asList();
        }
        //2. 查询
        List<AppUser> appUsers = appUserMapper.selectBatchIds(userIds);
        //3. 返回结果
        return appUsers.stream()
                .map(appUser -> {
                    AppUserDTO appUserDTO = new AppUserDTO();
                    BeanUtils.copyProperties(appUser, appUserDTO);
                    appUserDTO.setUserId(appUser.getId());
                    appUserDTO.setPhoneNumber(AESUtil.decryptHex(appUser.getPhoneNumber()));
                    return appUserDTO;
                })
                .collect(Collectors.toList());
    }

    @Override
    public BasePageDTO<AppUserDTO> getUserList(AppUserListReqDTO appUserListReqDTO) {
        LambdaQueryWrapper<AppUser> queryWrapper = new LambdaQueryWrapper<>();
        //1. 如果不为空再继续构建
        if (appUserListReqDTO != null) {
            if (appUserListReqDTO.getUserId() != null) {
                queryWrapper.eq(AppUser::getId, appUserListReqDTO.getUserId());
            }
            if (StringUtils.isNotEmpty(appUserListReqDTO.getPhoneNumber())) {
                queryWrapper.eq(AppUser::getPhoneNumber, AESUtil.encryptHex(appUserListReqDTO.getPhoneNumber()));
            }
            if (StringUtils.isNotEmpty(appUserListReqDTO.getNickName())) {
                queryWrapper.like(AppUser::getNickName, appUserListReqDTO.getNickName());
            }
            if (appUserListReqDTO.getOpenId() != null) {
                queryWrapper.eq(AppUser::getOpenId, appUserListReqDTO.getOpenId());
            }
        }
        //2. 查询
        Page<AppUser> appUserPage = appUserMapper.selectPage(new Page<AppUser>(appUserListReqDTO.getPageNo().intValue(),
                appUserListReqDTO.getPageSize().intValue()), queryWrapper);
        //3. 返回结果
        BasePageDTO<AppUserDTO>  basePageDTO = new BasePageDTO<>();
        basePageDTO.setTotals((int)appUserPage.getTotal());
        basePageDTO.setTotalPages((int) appUserPage.getPages());
        basePageDTO.setList(appUserPage.getRecords().stream()
                .map(appUser ->{
                    AppUserDTO appUserDTO = new AppUserDTO();
                    BeanUtils.copyProperties(appUser, appUserDTO);
                    appUserDTO.setUserId(appUser.getId());
                    appUserDTO.setPhoneNumber(AESUtil.decryptHex(appUser.getPhoneNumber()));
                    return appUserDTO;
                })
                .collect(Collectors.toList()));
        return basePageDTO;
    }
}
