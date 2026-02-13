package framework.admin.api.config.domain.VO;

import lombok.Data;

@Data
public class DicTypeVO {
    private Long id;
    private String typeKey;
    private String value;
    private String remark;
    private Integer status;
}
