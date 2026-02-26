package framework.domain.domain.VO;

import lombok.Data;

@Data
public class TokenVO {
    /**
     * 访问令牌
     */
    private String accessToken;
    /**
     * 过期时间
     */
    private Long expires;
}
