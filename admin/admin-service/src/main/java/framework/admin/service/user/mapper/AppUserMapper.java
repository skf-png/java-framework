package framework.admin.service.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import framework.admin.service.user.domain.entity.AppUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AppUserMapper extends BaseMapper<AppUser> {
}
