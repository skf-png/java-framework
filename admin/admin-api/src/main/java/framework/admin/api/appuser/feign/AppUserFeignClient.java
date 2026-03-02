package framework.admin.api.appuser.feign;

import framework.admin.api.appuser.domain.VO.AppUserVo;
import framework.domain.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(path = "/app_user", value = "admin", contextId = "appUserFeignClient")
public interface AppUserFeignClient {
    /**
     * 根据微信注册用户
     * @param openId 用户微信ID
     * @return C端用户VO
     */
    @GetMapping("/register/openid")
    R<AppUserVo> registerByOpenId(@RequestParam String openId);

    /**
     * 根据openId查询用户信息
     * @param openId 用户微信ID
     * @return C端用户VO
     */
    @GetMapping("/open_id_find")
    R<AppUserVo> findByOpenId(@RequestParam String openId);
}
