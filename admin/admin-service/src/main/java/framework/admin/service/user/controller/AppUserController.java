package framework.admin.service.user.controller;

import framework.admin.api.appuser.domain.DTO.AppUserDTO;
import framework.admin.api.appuser.domain.DTO.AppUserListReqDTO;
import framework.admin.api.appuser.domain.DTO.UserEditReqDTO;
import framework.admin.api.appuser.domain.VO.AppUserVo;
import framework.admin.api.appuser.feign.AppUserFeignClient;
import framework.admin.service.user.service.AppUserService;
import framework.core.DTO.BasePageDTO;
import framework.domain.R;
import framework.domain.ServiceException;
import framework.domain.domain.VO.BasePageVO;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    @Override
    public R<Void> edit(UserEditReqDTO userEditReqDTO) {
        appUserService.edit(userEditReqDTO);
        return R.success();
    }

    @Override
    public R<AppUserVo> findById(Long userId) {
        AppUserDTO appUserDTO = appUserService.findById(userId);
        if (appUserDTO == null) {
            return R.success();
        }
        return R.success(appUserDTO.convertToVO());
    }

    @Override
    public R<List<AppUserVo>> list(List<Long> userIds) {
        List<AppUserDTO> appUserDTOList = appUserService.getUserList(userIds);
        return R.success(
                appUserDTOList.stream()
                        .filter(Objects::nonNull)
                        .map(AppUserDTO::convertToVO)
                        .collect(Collectors.toList())
        );
    }

    /**
     * 查询C端用户
     * @param appUserListReqDTO 查询C端用户DTO
     * @return C端用户分页结果
     */
    @PostMapping("/list/search")
    public R<BasePageVO<AppUserVo>> list(@RequestBody AppUserListReqDTO appUserListReqDTO) {
        BasePageDTO<AppUserDTO> appUserDTOList = appUserService.getUserList(appUserListReqDTO);
        BasePageVO<AppUserVo> result = new BasePageVO<>();
        BeanUtils.copyProperties(appUserDTOList, result);
        return R.success(result);
    }
}
