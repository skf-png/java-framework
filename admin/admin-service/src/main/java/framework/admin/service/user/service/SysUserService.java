package framework.admin.service.user.service;

import framework.admin.service.user.domain.DTO.PasswordLoginDTO;
import framework.admin.service.user.domain.DTO.SysUserDTO;
import framework.admin.service.user.domain.DTO.SysUserListReqDTO;
import framework.admin.service.user.domain.DTO.SysUserLoginDTO;
import framework.security.domain.DTO.TokenDTO;

import java.util.List;

public interface SysUserService {
    TokenDTO login(PasswordLoginDTO loginDTO);

    Long addOrUpdate(SysUserDTO sysUserDTO);

    List<SysUserDTO> list(SysUserListReqDTO sysUserListReqDTO);

    SysUserLoginDTO getLoginInfo();
}
