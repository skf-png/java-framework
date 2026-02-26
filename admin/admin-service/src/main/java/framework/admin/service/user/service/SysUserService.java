package framework.admin.service.user.service;

import framework.admin.service.user.domain.DTO.PasswordLoginDTO;
import framework.security.domain.DTO.TokenDTO;

public interface SysUserService {
    TokenDTO login(PasswordLoginDTO loginDTO);
}
