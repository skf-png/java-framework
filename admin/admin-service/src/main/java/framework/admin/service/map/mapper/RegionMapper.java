package framework.admin.service.map.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import framework.admin.service.map.domain.entity.SysRegion;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RegionMapper extends BaseMapper<SysRegion> {
}
