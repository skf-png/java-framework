package framework.portal.service;

import framework.admin.api.appuser.domain.DTO.UserEditReqDTO;
import framework.portal.domain.DTO.LoginDTO;
import framework.portal.domain.DTO.UserDTO;
import framework.portal.domain.DTO.WechatLoginDTO;
import framework.security.domain.DTO.TokenDTO;

public interface UserService {

    TokenDTO login(LoginDTO wechatLoginDTO);

    String sendCode(String phone);

    String edit(UserEditReqDTO userEditReqDTO);

    UserDTO getLoginUser();

    void logout();
}
