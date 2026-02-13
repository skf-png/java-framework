package framework.admin.api.map.domain.VO;

import lombok.Data;

/**
 * 查询结果VO
 */
@Data
public class SearchPoiVo {

    /**
     * 地点名称
     */
    private String title;

    /**
     * 地点地址
     */
    private String address;

    /**
     * 经度
     */
    private Double longitude;

    /**
     * 纬度
     */
    private Double latitude;
}
