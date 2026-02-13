package framework.admin.service.config.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import framework.core.entity.BaseDO;
import lombok.Data;

@TableName("sys_dictionary_type")
@Data
public class SysDicType extends BaseDO {
    /**
     * 字典类型编码
     */
    private String typeKey;
    /**
     * 字典类型名称
     */
    private String value;
    /**
     * 备注
     */
    private String remark;
    /**
     * 字典类型状态 1正常 0停⽤
     */
    private Integer status;
}
