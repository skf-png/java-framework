package framework.admin.api.config.domain.VO;

import lombok.Data;

@Data
public class DicDataVO {
    private Long id;
    private String typeKey;
    private String dataKey;
    private String value;
    private String remark;
    private Integer sort;
    private Integer status;
}
