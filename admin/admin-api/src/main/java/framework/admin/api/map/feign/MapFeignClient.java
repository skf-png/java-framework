package framework.admin.api.map.feign;

import framework.admin.api.map.domain.DTO.LocationReqDTO;
import framework.admin.api.map.domain.DTO.PlaceSearchReqDTO;
import framework.admin.api.map.domain.VO.RegionCityVo;
import framework.admin.api.map.domain.VO.RegionVO;
import framework.admin.api.map.domain.VO.SearchPoiVo;
import framework.domain.R;
import framework.domain.domain.VO.BasePageVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(value = "admin", contextId = "mapFeignClient")
public interface MapFeignClient {
    /**
     * 获取所有城市信息
     * @return 所有城市信息
     */
    @GetMapping("/map/city_list")
    public R<List<RegionVO>> getCityList();
    /**
     * 城市拼音归类查询
     * @return 城市字母与城市列表的哈希
     */
    @GetMapping("/map/city_pinyin_list")
    public R<Map<String, List<RegionVO>>> getCityPyList();

    /**
     * 获取下级⾏政区划信息
     */
    @GetMapping("/map/region_children_list")
    public R<List<RegionVO>> getRegionChildrenList(@RequestParam Long parentId);
    /**
     * 获取热门城市
     */
    @GetMapping("/map/city_hot_list")
    public R<List<RegionVO>> getCityHotList();

    /**
     * 根据地点搜索
     * @param placeSearchReqDTO 搜索条件
     * @return 搜索结果
     */
    @PostMapping("/map/search")
    R<BasePageVO<SearchPoiVo>> searchSuggestOnMap(@RequestBody @Validated PlaceSearchReqDTO placeSearchReqDTO);

    /**
     * 根据经纬度来定位城市
     * @param locationReqDTO 经纬度信息
     * @return 城市信息
     */
    @PostMapping("/map/locate_city_by_location")
    R<RegionCityVo> locateCityByLocation(@RequestBody @Validated LocationReqDTO locationReqDTO);
}
