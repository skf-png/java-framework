package framework.admin.service.map.service;

import framework.admin.api.map.domain.DTO.LocationReqDTO;
import framework.admin.api.map.domain.DTO.PlaceSearchReqDTO;
import framework.admin.service.map.domain.DTO.RegionCityDTO;
import framework.admin.service.map.domain.DTO.SearchPoiDTO;
import framework.admin.service.map.domain.DTO.SysRegionDTO;
import framework.core.DTO.BasePageDTO;

import java.util.List;
import java.util.Map;

public interface MapService {
    List<SysRegionDTO> getCityList();

    Map<String, List<SysRegionDTO>> getCityPyList();

    List<SysRegionDTO> getRegionChildren(Long parentId);

    List<SysRegionDTO> getHotCityList();

    /**
     * 根据地点搜索
     * @param placeSearchReqDTO 搜索条件
     * @return 搜索结果
     */
    BasePageDTO<SearchPoiDTO> searchSuggestOnMap(PlaceSearchReqDTO placeSearchReqDTO);

    /**
     * 根据经纬度来定位城市
     * @param locationReqDTO 经纬度信息
     * @return 城市信息
     */
    RegionCityDTO getCityByLocation(LocationReqDTO locationReqDTO);
}
