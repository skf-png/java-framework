package framework.admin.api.config.domain.DTO;

import framework.domain.domain.DTO.BasePageReqDTO;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DicTypeListReqDTO extends BasePageReqDTO {
    /**
     * 字典类型值，右查询
     */
    private String value;
    /**
     * 字典类型键，精准查询
     */
    private String typeKey;
}