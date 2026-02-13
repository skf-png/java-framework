package framework.admin.service.config.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import framework.core.entity.BaseDO;
import lombok.Data;

@Data
@TableName("sys_dictionary_data")
public class SysDicData extends BaseDO {
    /**
     * 字典类型主键
     */
    private String typeKey;
    /**
     * 字典类型主键
     */
    private String dataKey;
    /**
     * 字典数据名称
     */
    private String value;
    /**
     * 备注
     */
    private String remark;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 字典数据状态 1正常 0停⽤
     */
    private Integer status;
}
