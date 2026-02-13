package framework.admin.api.map.domain.VO;

import lombok.Data;

/**
 * 城市信息VO
 */
@Data
public class RegionCityVo {

    /**
     * 城市ID
     */
    private Long id;

    /**
     * 城市名称
     */
    private String name;

    /**
     * 城市全称
     */
    private String fullName;
}
