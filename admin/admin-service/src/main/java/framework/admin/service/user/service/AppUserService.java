package framework.admin.service.user.service;

import framework.admin.api.appuser.domain.DTO.AppUserDTO;
import framework.admin.api.appuser.domain.DTO.AppUserListReqDTO;
import framework.admin.api.appuser.domain.DTO.UserEditReqDTO;
import framework.core.DTO.BasePageDTO;

import java.util.List;

public interface AppUserService {
    AppUserDTO registerByOpenId(String openId);

    AppUserDTO findByOpenId(String openId);

    AppUserDTO findByPhone(String phoneNumber);

    AppUserDTO registerByPhone(String phoneNumber);

    void edit(UserEditReqDTO userEditReqDTO);

    AppUserDTO findById(Long userId);

    List<AppUserDTO> getUserList(List<Long> userIds);

    BasePageDTO<AppUserDTO> getUserList(AppUserListReqDTO appUserListReqDTO);
}
