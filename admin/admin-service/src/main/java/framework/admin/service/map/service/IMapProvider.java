package framework.admin.service.map.service;

import framework.admin.api.map.domain.DTO.LocationReqDTO;
import framework.admin.api.map.domain.DTO.PlaceSearchReqDTO;
import framework.admin.service.map.domain.DTO.*;
import framework.core.DTO.BasePageDTO;

/**
 * 地图服务提供方
 */
public interface IMapProvider {
    /**
     * 根据关键词搜索地点
     * @param suggestSearchDTO 搜索条件
     * @return 搜索结果
     */
    PoiListDTO searchQQMapPlaceByRegion(SuggestSearchDTO suggestSearchDTO);

    /**
     * 根据经纬度来获取区域信息
     * @param locationDTO 经纬度
     * @return 区域信息
     */
    GeoResultDTO getQQMapDistrictByLonLat(LocationDTO locationDTO);


}
