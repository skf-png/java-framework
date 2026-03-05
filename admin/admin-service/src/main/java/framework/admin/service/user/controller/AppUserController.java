package framework.admin.service.user.controller;

import framework.admin.api.appuser.domain.DTO.AppUserDTO;
import framework.admin.api.appuser.domain.VO.AppUserVo;
import framework.admin.api.appuser.feign.AppUserFeignClient;
import framework.admin.service.user.service.AppUserService;
import framework.domain.R;
import framework.domain.ServiceException;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app_user")
public class AppUserController implements AppUserFeignClient {
    @Autowired
    private AppUserService appUserService;

    @Override
    public R<AppUserVo> registerByOpenId(String openId) {
        AppUserDTO res = appUserService.registerByOpenId(openId);
        if (res == null) {
            throw new ServiceException("注册失败");
        }
        return R.success(res.convertToVO());
    }

    @Override
    public R<AppUserVo> findByOpenId(String openId) {
        AppUserDTO res = appUserService.findByOpenId(openId);
        if (res == null) {
            return R.success();
        }
        return R.success(res.convertToVO());
    }

    @Override
    public R<AppUserVo> findByPhone(String phoneNumber) {
        AppUserDTO res = appUserService.findByPhone(phoneNumber);
        if (res == null) {
            return R.success();
        }
        return R.success(res.convertToVO());
    }

    @Override
    public R<AppUserVo> registerByPhone(String phoneNumber) {
        AppUserDTO res = appUserService.registerByPhone(phoneNumber);
        if (res == null) {
            throw new ServiceException("注册失败");
        }
        return R.success(res.convertToVO());
    }
}
