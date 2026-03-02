package framework.admin.service.user.service;

import framework.admin.api.appuser.domain.DTO.AppUserDTO;

public interface AppUserService {
    AppUserDTO registerByOpenId(String openId);

    AppUserDTO findByOpenId(String openId);
}
