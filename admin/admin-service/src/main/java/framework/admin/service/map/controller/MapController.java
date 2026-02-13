package framework.admin.service.map.controller;

import framework.admin.api.map.domain.DTO.LocationReqDTO;
import framework.admin.api.map.domain.DTO.PlaceSearchReqDTO;
import framework.admin.api.map.domain.VO.RegionCityVo;
import framework.admin.api.map.domain.VO.RegionVO;
import framework.admin.api.map.domain.VO.SearchPoiVo;
import framework.admin.api.map.feign.MapFeignClient;
import framework.admin.service.map.domain.DTO.RegionCityDTO;
import framework.admin.service.map.domain.DTO.SearchPoiDTO;
import framework.admin.service.map.domain.DTO.SysRegionDTO;
import framework.admin.service.map.service.MapService;
import framework.core.DTO.BasePageDTO;
import framework.core.utils.BeanCopyUtil;
import framework.domain.R;
import framework.domain.domain.VO.BasePageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class MapController implements MapFeignClient {
    @Autowired
    private MapService mapService;

    @Override
    public R<List<RegionVO>> getCityList() {
        List<SysRegionDTO> cityList = mapService.getCityList();
        List<RegionVO> regionVOS = BeanCopyUtil.copyListProperties(cityList, RegionVO::new);

        return R.success(regionVOS);
    }

    @Override
    public R<Map<String, List<RegionVO>>> getCityPyList() {
        Map<String, List<SysRegionDTO>> cityList = mapService.getCityPyList();
        Map<String, List<RegionVO>> result = new LinkedHashMap<>();
        for (Map.Entry<String, List<SysRegionDTO>> region : cityList.entrySet()) {
            result.put(region.getKey(), BeanCopyUtil.copyListProperties(region.getValue(), RegionVO::new));
        }
        return R.success(result);
    }

    @Override
    public R<List<RegionVO>> getRegionChildrenList(Long parentId) {
        List<SysRegionDTO> list = mapService.getRegionChildren(parentId);
        List<RegionVO> result = BeanCopyUtil.copyListProperties(list, RegionVO::new);
        return R.success(result);
    }

    @Override
    public R<List<RegionVO>> getCityHotList() {
        List<SysRegionDTO> list = mapService.getHotCityList();
        List<RegionVO> result = BeanCopyUtil.copyListProperties(list, RegionVO::new);
        return R.success(result);
    }

    /**
     * 根据地点搜索
     * @param placeSearchReqDTO 搜索条件
     * @return 搜索结果
     */
    @Override
    public R<BasePageVO<SearchPoiVo>> searchSuggestOnMap(PlaceSearchReqDTO placeSearchReqDTO) {
        BasePageDTO<SearchPoiDTO> basePageReqDTO =  mapService.searchSuggestOnMap(placeSearchReqDTO);
        BasePageVO<SearchPoiVo> result = new BasePageVO<>();
        BeanUtils.copyProperties(basePageReqDTO, result);
        return R.success(result);
    }

    /**
     * 根据经纬度来定位城市
     * @param locationReqDTO 经纬度信息
     * @return 城市信息
     */
    @Override
    public R<RegionCityVo> locateCityByLocation(LocationReqDTO locationReqDTO) {
        RegionCityDTO regionCityDTO = mapService.getCityByLocation(locationReqDTO);
        RegionCityVo result = new RegionCityVo();
        BeanUtils.copyProperties(regionCityDTO, result);
        return R.success(result);
    }
}
