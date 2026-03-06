package framework.portal.service.impl;

import framework.admin.api.appuser.domain.DTO.UserEditReqDTO;
import framework.admin.api.appuser.domain.VO.AppUserVo;
import framework.admin.api.appuser.feign.AppUserFeignClient;
import framework.core.utils.BeanCopyUtil;
import framework.core.utils.VerifyUtil;
import framework.domain.R;
import framework.domain.ResultCode;
import framework.domain.ServiceException;
import framework.message.service.CaptchaService;
import framework.portal.domain.DTO.CodeLoginDTO;
import framework.portal.domain.DTO.LoginDTO;
import framework.portal.domain.DTO.UserDTO;
import framework.portal.domain.DTO.WechatLoginDTO;
import framework.portal.service.UserService;
import framework.security.domain.DTO.LoginUserDTO;
import framework.security.domain.DTO.TokenDTO;
import framework.security.service.TokenService;
import framework.security.utils.JwtUtil;
import framework.security.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private AppUserFeignClient appUserFeignClient;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private CaptchaService captchaService;

    @Override
    public TokenDTO login(LoginDTO loginDTO) {
        LoginUserDTO loginUserDTO = new LoginUserDTO();
        //1. 参数校验
        if (loginDTO == null) {
            throw new ServiceException(ResultCode.INVALID_PARA.getCode(), "登录信息不能为空");
        }
        //2. 判断类型
        if (loginDTO instanceof WechatLoginDTO wechatLoginDTO) {
            loginBywechat(wechatLoginDTO, loginUserDTO);
        } else if (loginDTO instanceof CodeLoginDTO codeLoginDTO) {
            loginByCode(codeLoginDTO, loginUserDTO);
        }
        //3. 设置缓存
        loginUserDTO.setUserFrom("app");
        return tokenService.createToken(loginUserDTO);
    }



    @Override
    public String sendCode(String phone) {
        if (!VerifyUtil.checkPhone(phone)) {
            throw new ServiceException(ResultCode.INVALID_PARA.getCode(),"手机号格式错误");
        }
        return captchaService.sendCode(phone);
    }

    @Override
    public String edit(UserEditReqDTO userEditReqDTO) {
        //1. 用户只能修改自己的
        LoginUserDTO loginUser = tokenService.getLoginUser();
        if (loginUser.getUserId().longValue() != userEditReqDTO.getUserId().longValue()) {
            throw new ServiceException(ResultCode.INVALID_PARA.getCode(), "userId错误");
        }
        R<Void> edit = appUserFeignClient.edit(userEditReqDTO);
        if (edit == null || edit.getCode() != ResultCode.SUCCESS.getCode()) {
            throw new ServiceException("修改失败");
        }
        //2. 更新redis数据库
        loginUser.setUserName(userEditReqDTO.getNickName());
        TokenDTO token = tokenService.createToken(loginUser);
        return token.getAccessToken();
    }

    @Override
    public UserDTO getLoginUser() {
        //1. 获取登录信息
        LoginUserDTO loginUser = tokenService.getLoginUser();
        if (loginUser == null) {
            throw new ServiceException(ResultCode.INVALID_PARA.getCode(), "用户令牌错误");
        }
        //2. 根据id获取数据库中的信息
        R<AppUserVo> byId = appUserFeignClient.findById(loginUser.getUserId());
        if (byId == null || byId.getCode() != ResultCode.SUCCESS.getCode() || byId.getData() == null) {
            throw new ServiceException(ResultCode.INVALID_PARA.getCode(), "用户查询错误");
        }
        //3. 返回结果
        UserDTO userDTO = new UserDTO();
        BeanCopyUtil.copyProperties(byId.getData(), userDTO);
        BeanCopyUtil.copyProperties(loginUser, userDTO);
        return userDTO;
    }

    @Override
    public void logout() {
        String token = SecurityUtil.getToken();
        if (StringUtils.isEmpty(token)) {
            return;
        }
        String userName = JwtUtil.getUserName(token);
        String userId = JwtUtil.getUserId(token);
        log.info("{}退出了系统, 用户ID{}", userName, userId);
        // 2 删除用户缓存记录
        tokenService.delLoginUser(token);
    }

    /**
     * 微信登录
     * @param wechatLoginDTO 登录信息
     * @param loginUserDTO 用户信息
     */
    private void loginBywechat(WechatLoginDTO wechatLoginDTO, LoginUserDTO loginUserDTO) {
        AppUserVo appUserVo = new AppUserVo();
        //1. 获取用户
        R<AppUserVo> user = appUserFeignClient.findByOpenId(wechatLoginDTO.getOpenId());
        //2. 判断用户是否存在，不存在进入注册逻辑。
        if (user == null || user.getCode() != ResultCode.SUCCESS.getCode() || user.getData() == null) {
            appUserVo = register(wechatLoginDTO);
        } else {
            appUserVo = user.getData();
        }
        //3. 设置用户登录信息
        loginUserDTO.setUserId(appUserVo.getUserId());
        loginUserDTO.setUserName(appUserVo.getNickName());
    }

    /**
     * 手机号登录
     * @param codeLoginDTO
     * @param loginUserDTO
     */
    private void loginByCode(CodeLoginDTO codeLoginDTO, LoginUserDTO loginUserDTO) {
        //1. 参数校验
        if (!VerifyUtil.checkPhone(codeLoginDTO.getPhone())) {
            throw new ServiceException(ResultCode.INVALID_PARA.getCode(), "手机号格式错误");
        }
        //2. 执行查询
        AppUserVo appUserVo = null;
        R<AppUserVo> res = appUserFeignClient.findByPhone(codeLoginDTO.getPhone());
        if (res == null || res.getCode() != ResultCode.SUCCESS.getCode() || res.getData() == null) {
            appUserVo = register(codeLoginDTO);
        } else {
            appUserVo = res.getData();
        }
        //3. 验证码校验
        String code = captchaService.getCode(codeLoginDTO.getPhone());
        if (code == null) {
            throw new ServiceException(ResultCode.INVALID_CODE);
        }
        if (!code.equals(codeLoginDTO.getCode())) {
            throw new ServiceException(ResultCode.ERROR_CODE);
        }
        //4. 登录信息设置
        captchaService.deleteCode(codeLoginDTO.getPhone());
        loginUserDTO.setUserId(appUserVo.getUserId());
        loginUserDTO.setUserName(appUserVo.getNickName());
    }

    /**
     * 根据入参注册
     * @param loginDTO
     * @return
     */
    private AppUserVo register(LoginDTO loginDTO) {
        R<AppUserVo> res = new R<>();
        if (loginDTO instanceof WechatLoginDTO wechatLoginDTO) {
            res = appUserFeignClient.registerByOpenId(wechatLoginDTO.getOpenId());
            if (res == null || res.getCode() != ResultCode.SUCCESS.getCode() || res.getData() == null) {
                log.error("用户注册失败");
            }
        } else if (loginDTO instanceof CodeLoginDTO codeLoginDto) {
            res = appUserFeignClient.registerByPhone(codeLoginDto.getPhone());
            if (res == null || res.getCode() != ResultCode.SUCCESS.getCode() || res.getData() == null) {
                log.error("用户注册失败");
            }
        }
        if (res == null) {
            return null;
        }
        return res.getData();
    }
}
