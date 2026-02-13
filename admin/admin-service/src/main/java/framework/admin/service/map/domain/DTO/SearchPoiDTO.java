package framework.admin.service.map.domain.DTO;

import lombok.Data;

@Data
public class SearchPoiDTO {

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
