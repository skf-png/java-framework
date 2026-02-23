package framework.admin.service.map.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.github.benmanes.caffeine.cache.Cache;
import framework.admin.api.map.constants.MapConstants;
import framework.admin.api.map.domain.DTO.LocationReqDTO;
import framework.admin.api.map.domain.DTO.PlaceSearchReqDTO;
import framework.admin.service.config.service.ArgumentService;
import framework.admin.service.map.domain.DTO.*;
import framework.admin.service.map.domain.entity.SysRegion;
import framework.admin.service.map.mapper.RegionMapper;
import framework.admin.service.map.service.IMapProvider;
import framework.admin.service.map.service.MapService;
import framework.cache.utils.CacheUtils;
import framework.core.DTO.BasePageDTO;
import framework.core.utils.BeanCopyUtil;
import framework.core.utils.PageUtil;
import framework.redis.service.RedisService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class MapServiceImpl implements MapService {
    @Autowired
    private RegionMapper regionMapper;

    @Autowired
    private RedisService redisService;

    @Autowired
    private Cache<String, Object> cache;

    @Autowired
    private IMapProvider mapProvider;

    @Autowired
    private ArgumentService argumentService;
    /**
     * 初始化缓存
     */
    @PostConstruct
    private void init() {
        //级别等于城市按照拼音升序排序
        QueryWrapper<SysRegion> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysRegion::getLevel, MapConstants.CITY_LEVEL)
                .orderByAsc(SysRegion::getPinyin);
        List<SysRegion> list = regionMapper.selectList(queryWrapper);
        //转换成DTO
        List<SysRegionDTO> sysRegionDTOS = BeanCopyUtil.copyListProperties(list, SysRegionDTO::new);
        //存入缓存
        CacheUtils.setL2Cache(redisService,MapConstants.CACHE_MAP_CITY_KEY, sysRegionDTOS,cache);
        //对拼音进行处理
        loadCityPyList(sysRegionDTOS);
//        System.out.println("初始化完成");
//        System.out.println(list.toString());
//        List<SysRegionDTO> res = CacheUtils.getL2Cache(redisService, MapConstants.CACHE_MAP_CITY_KEY
//                , new TypeReference<List<SysRegionDTO>>() {},cache);
//        System.out.println(res.toString());
    }

    /**
     * 按照拼音大写对城市排序，并且存入缓存
     * @param list
     */
    private void loadCityPyList(List<SysRegionDTO> list) {
        Map<String, List<SysRegionDTO>> cityMap = new TreeMap<>();
        for (SysRegionDTO sysRegionDTO : list) {
            //提取大写拼音首字母
            String key = sysRegionDTO.getPinyin().toUpperCase().substring(0, 1);
            //存在add，不存在new + put
            if (cityMap.containsKey(key)) {
                cityMap.get(key).add(sysRegionDTO);
            } else  {
                List<SysRegionDTO> lists = new ArrayList<>();
                lists.add(sysRegionDTO);
                cityMap.put(key, lists);
            }
        }
        //放入缓存中
        CacheUtils.setL2Cache(redisService, MapConstants.CACHE_MAP_CITY_PINYIN_KEY, cityMap,cache);
    }

    @Override
    public List<SysRegionDTO> getCityList() {
        //从缓存获取数据
        List<SysRegionDTO> res = CacheUtils.getL2Cache(redisService, MapConstants.CACHE_MAP_CITY_KEY
                , new TypeReference<List<SysRegionDTO>>() {},cache);
        return res;
    }

    @Override
    public Map<String, List<SysRegionDTO>> getCityPyList() {
        //从缓存获取数据
        Map<String, List<SysRegionDTO>> cityMap = CacheUtils.getL2Cache(redisService, MapConstants.CACHE_MAP_CITY_PINYIN_KEY
                , new TypeReference<Map<String, List<SysRegionDTO>>>() {}, cache);
        return cityMap;
    }

    @Override
    public List<SysRegionDTO> getRegionChildren(Long parentId) {
        //先从缓存里面获取数据
        String key = MapConstants.CACHE_MAP_CITY_CHILDREN_KEY + parentId;
        List<SysRegionDTO> res = CacheUtils.getL2Cache(redisService, key,new TypeReference<List<SysRegionDTO>>() {},cache);
        if (res != null) {
            return res;
        }
        //从数据库中查询数据
        QueryWrapper<SysRegion> queryWrapper = new QueryWrapper<>();
        //parentId和入参相等即可
        queryWrapper.lambda().eq(SysRegion::getParentId, parentId);
        List<SysRegion> sysRegions = regionMapper.selectList(queryWrapper);
        res = BeanCopyUtil.copyListProperties(sysRegions, SysRegionDTO::new);
        //设置缓存
        CacheUtils.setL2Cache(redisService, key, res,cache, 120L,  TimeUnit.MINUTES);
        return res;
    }

    @Override
    public List<SysRegionDTO> getHotCityList() {
        //从缓存获取数据
        List<SysRegionDTO> list = CacheUtils.getL2Cache(redisService, MapConstants.CACHE_MAP_HOT_CITY,
                new TypeReference<List<SysRegionDTO>>() {}, cache);
        if (list != null) {
            return list;
        }
        //字符串分割
        String hotCityIds = argumentService.getConfigKey(MapConstants.CONFIG_KEY).getValue();
        Long[] ids = Arrays.stream(hotCityIds.split(",")).map(Long::valueOf).toArray(Long[]::new);
        //从redis的城市列表中查询
        list = CacheUtils.getL2Cache(redisService, MapConstants.CACHE_MAP_CITY_KEY,
                new TypeReference<List<SysRegionDTO>>() {}, cache);
        List<SysRegionDTO> result = new ArrayList<>();

        for (SysRegionDTO sysRegionDTO : list) {
            //判断是否是否包含
            if (Arrays.stream(ids).anyMatch(id -> sysRegionDTO.getId().equals(id))) {
                result.add(sysRegionDTO);
            }
        }

        //保存缓存
        CacheUtils.setL2Cache(redisService, MapConstants.CACHE_MAP_HOT_CITY, result,cache, 120L,  TimeUnit.MINUTES);
        return result;
    }

    /**
     * 根据地点搜索
     * @param placeSearchReqDTO 搜索条件
     * @return 搜索结果
     */
    @Override
    public BasePageDTO<SearchPoiDTO> searchSuggestOnMap(PlaceSearchReqDTO placeSearchReqDTO) {
        // 1 构建查询腾讯位置服务的入参
        SuggestSearchDTO suggestSearchDTO = new SuggestSearchDTO();
        BeanUtils.copyProperties(placeSearchReqDTO, suggestSearchDTO);
        suggestSearchDTO.setPageIndex(placeSearchReqDTO.getPageNo());
        suggestSearchDTO.setId(String.valueOf(placeSearchReqDTO.getId()));
        // 2 调用地图位置查询接口
        PoiListDTO poiListDTO = mapProvider.searchQQMapPlaceByRegion(suggestSearchDTO);
        // 3 做结果对象转换
        List<PoiDTO> poiDTOList = poiListDTO.getData();
        BasePageDTO<SearchPoiDTO> result = new BasePageDTO<>();
        result.setTotals(poiListDTO.getCount());
        result.setTotalPages(PageUtil.getTotalPages(result.getTotals(), placeSearchReqDTO.getPageSize()));

        List<SearchPoiDTO> pageRes = new ArrayList<>();
        for (PoiDTO poiDTO : poiDTOList) {
            SearchPoiDTO searchPoiDTO = new SearchPoiDTO();
            BeanUtils.copyProperties(poiDTO, searchPoiDTO);
            searchPoiDTO.setLongitude(poiDTO.getLocation().getLng());
            searchPoiDTO.setLatitude(poiDTO.getLocation().getLat());
            pageRes.add(searchPoiDTO);
        }
        result.setList(pageRes);
        return result;
    }

    /**
     * 根据经纬度来定位城市
     * @param locationReqDTO 经纬度信息
     * @return 城市信息
     */
    @Override
    public RegionCityDTO getCityByLocation(LocationReqDTO locationReqDTO) {
        // 1 构建查询腾讯位置服务的入参
        LocationDTO locationDTO = new LocationDTO();
        BeanUtils.copyProperties(locationReqDTO, locationDTO);
        RegionCityDTO result = new RegionCityDTO();
        // 2 调用腾讯位置服务接口
        GeoResultDTO geoResultDTO =  mapProvider.getQQMapDistrictByLonLat(locationDTO);
        if (geoResultDTO != null && geoResultDTO.getResult() != null && geoResultDTO.getResult().getAd_info() != null) {
            String cityName = geoResultDTO.getResult().getAd_info().getCity();
            // 3 查缓存
            List<SysRegionDTO> cache1 = CacheUtils.getL2Cache(redisService, MapConstants.CACHE_MAP_CITY_KEY,
                    new TypeReference<List<SysRegionDTO>>(){}, cache);
            for (SysRegionDTO sysRegionDTO: cache1) {
                if (sysRegionDTO.getFullName().equals(cityName)) {
                    BeanUtils.copyProperties(sysRegionDTO, result);
                    return result;
                }
            }
        }
        return result;
    }
}
