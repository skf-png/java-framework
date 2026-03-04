package framework.portal.service;

import framework.portal.domain.DTO.LoginDTO;
import framework.portal.domain.DTO.WechatLoginDTO;
import framework.security.domain.DTO.TokenDTO;

public interface UserService {

    TokenDTO login(LoginDTO wechatLoginDTO);

    String sendCode(String phone);
}
