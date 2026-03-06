package framework.portal.domain.VO;

import framework.domain.domain.VO.LoginUserVO;
import lombok.Data;

/**
 * C端用户VO
 */
@Data
public class UserVo extends LoginUserVO {

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 昵称
     */
    private String nickName;
}
