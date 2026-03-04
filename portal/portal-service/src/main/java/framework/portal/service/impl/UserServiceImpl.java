package framework.portal.service.impl;

import framework.admin.api.appuser.domain.VO.AppUserVo;
import framework.admin.api.appuser.feign.AppUserFeignClient;
import framework.core.utils.VerifyUtil;
import framework.domain.R;
import framework.domain.ResultCode;
import framework.domain.ServiceException;
import framework.message.service.CaptchaService;
import framework.portal.domain.DTO.LoginDTO;
import framework.portal.domain.DTO.WechatLoginDTO;
import framework.portal.service.UserService;
import framework.security.domain.DTO.LoginUserDTO;
import framework.security.domain.DTO.TokenDTO;
import framework.security.service.TokenService;
import framework.security.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
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
        }
        if (res == null) {
            return null;
        }
        return res.getData();
    }
}
