package framework.admin.service.user.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import framework.admin.service.user.domain.DTO.SysUserDTO;
import framework.core.entity.BaseDO;
import framework.core.utils.AESUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

/**
 * 系统用户对象 sys_user
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class SysUser extends BaseDO {

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 手机号
     */
    private String phoneNumber;

    /**
     * 密码
     */
    private String password;

    /**
     * 身份
     */
    private String identity;

    /**
     * 状态
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

    public SysUserDTO convertDTO() {
        SysUserDTO sysUserDTO = new SysUserDTO();
        BeanUtils.copyProperties(this, sysUserDTO);
        sysUserDTO.setPhoneNumber(AESUtil.decryptHex(this.phoneNumber));
        return sysUserDTO;
    }
}