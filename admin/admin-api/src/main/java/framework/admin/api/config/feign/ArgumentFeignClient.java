package framework.admin.api.config.feign;

import framework.admin.api.config.domain.DTO.ArgumentDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "admin", path = "/argument", contextId = "argumentFeignClient")
public interface ArgumentFeignClient {
    /**
     * 根据主键查询参数
     * @param configKey 主键
     * @return 参数信息
     */
    @PostMapping("/key")
    ArgumentDTO getByConfigKey(@RequestParam String configKey);

    /**
     * 根据多个主键查询参数
     * @param configKeys 主键
     * @return 参数信息
     */
    @PostMapping("/keys")
    List<ArgumentDTO> getByConfigKeys(@RequestParam List<String> configKeys);


}
