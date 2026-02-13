package framework.admin.api.config.domain.DTO;

import framework.domain.domain.DTO.BasePageReqDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DicDataListReqDTO extends BasePageReqDTO {
    @NotBlank(message = "字典类型键名不能为空")
    private String typeKey;
    private String value;
}
