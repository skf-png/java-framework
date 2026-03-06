package framework.admin.api.appuser.feign;

import framework.admin.api.appuser.domain.DTO.UserEditReqDTO;
import framework.admin.api.appuser.domain.VO.AppUserVo;
import framework.domain.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

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

    /**
     * 根据手机号查询用户信息
     * @param phoneNumber 手机号
     * @return C端用户VO
     */
    @GetMapping("/phone_find")
    R<AppUserVo> findByPhone(@RequestParam String phoneNumber);

    /**
     * 根据手机号注册用户
     * @param phoneNumber 手机号
     * @return C端用户VO
     */
    @GetMapping("/register/phone")
    R<AppUserVo> registerByPhone(@RequestParam String phoneNumber);

    /**
     * 编辑C端用户
     * @param userEditReqDTO C端用户DTO
     * @return void
     */
    @PostMapping("/edit")
    R<Void> edit(@RequestBody @Validated UserEditReqDTO userEditReqDTO);

    /**
     * 根据用户ID获取用户信息
     * @param userId 用户ID
     * @return C端用户VO
     */
    @GetMapping("/id_find")
    R<AppUserVo> findById(@RequestParam Long userId);

    /**
     * 根据用户ID列表获取用户列表信息
     * @param userIds 用户ID列表
     * @return C端用户VO列表
     */
    @PostMapping("/list")
    R<List<AppUserVo>> list(@RequestBody List<Long> userIds);
}
