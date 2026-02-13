package framework.admin.api.config.domain.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DicDataAddReqDTO {
    @NotBlank(message = "字典类型键不能为空")
    private String typeKey;
    @NotBlank(message = "字典数据键不能为空")
    private String dataKey;
    @NotBlank(message = "字典数据不能为空")
    private String value;
    private String remark;
    private Integer sort;
}
