package framework.admin.service.config.controller;

import framework.admin.api.config.domain.DTO.ArgumentAddReqDTO;
import framework.admin.api.config.domain.DTO.ArgumentDTO;
import framework.admin.api.config.domain.DTO.ArgumentEditReqDTO;
import framework.admin.api.config.domain.DTO.ArgumentListReqDTO;
import framework.admin.api.config.domain.VO.ArgumentVO;
import framework.admin.api.config.feign.ArgumentFeignClient;
import framework.admin.service.config.service.ArgumentService;
import framework.core.DTO.BasePageDTO;
import framework.domain.R;
import framework.domain.domain.VO.BasePageVO;
import jakarta.servlet.ServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/argument")
public class ArgumentController implements ArgumentFeignClient {
    @Autowired
    private ArgumentService argumentService;

    /**
     * 新增参数
     * @param argumentAddReqDTO 新增参数信息
     * @return 新增id
     */
    @PostMapping("/add")
    public R<Long> add(@RequestBody @Validated ArgumentAddReqDTO argumentAddReqDTO) {
        Long res = argumentService.add(argumentAddReqDTO);
        return R.success(res);
    }

    /**
     * 获取参数列表
     * @param argumentListReqDTO 请求信息
     * @return 分页参数信息
     */
    @PostMapping("/list")
    public R<BasePageVO<ArgumentVO>> list (@Validated @RequestBody ArgumentListReqDTO argumentListReqDTO){
        BasePageVO<ArgumentVO> res = argumentService.list(argumentListReqDTO);
        return R.success(res);
    }

    /**
     * 修改参数信息
     * @param argumentEditReqDTO 修改的参数信息
     * @return 修改参数id
     */
    @PostMapping("/edit")
    public R<Long> edit(@RequestBody @Validated ArgumentEditReqDTO argumentEditReqDTO) {
        Long res = argumentService.edit(argumentEditReqDTO);
        return R.success(res);
    }

    @Override
    public ArgumentDTO getByConfigKey(String configKey) {
        ArgumentDTO res = argumentService.getConfigKey(configKey);
        return res;
    }

    @Override
    public List<ArgumentDTO> getByConfigKeys(List<String> configKeys) {
        List<ArgumentDTO> res = argumentService.getConfigKeys(configKeys);
        return res;
    }
}
