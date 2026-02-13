package framework.admin.api.config.feign;

import framework.admin.api.config.domain.DTO.*;
import framework.admin.api.config.domain.VO.DicDataVO;
import framework.admin.api.config.domain.VO.DicTypeVO;
import framework.domain.R;
import framework.domain.domain.VO.BasePageVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "admin")
public interface DicFeignClient {
    /**
     * 增加字典类型
     * @param dicTypeWriteReqDTO 增加信息
     * @return 新增字典主键
     */
    @PostMapping("/dictionary_type/add")
    R<Long> addType(@RequestBody @Validated DicTypeWriteReqDTO dicTypeWriteReqDTO);

    /**
     * 查看字典类型
     * @param dicTypeListReqDTO 查看请求
     * @return 分页结果
     */
    @GetMapping("/dictionary_type/list")
    R<BasePageVO<DicTypeVO>> listType(@RequestBody(required = false) DicTypeListReqDTO dicTypeListReqDTO);

    /**
     * 编辑字典类型
     * @param dicTypeWriteReqDTO 修改内容
     * @return 修改的字典主键
     */
    @PostMapping("/dictionary_type/edit")
    R<Long> editType(@RequestBody @Validated DicTypeWriteReqDTO dicTypeWriteReqDTO);

    /**
     * 新增字典数据
     * @param dicDataAddReqDTO 新增数据
     * @return 新增id
     */
    @PostMapping("/dictionary_data/add")
    R<Long> addDicData(@RequestBody @Validated DicDataAddReqDTO dicDataAddReqDTO);

    /**
     * 查看字典数据
     * @param dicDataListReqDTO 查看请求
     * @return 分页结果
     */
    @GetMapping("/dictionary_data/list")
    R<BasePageVO<DicDataVO>> listDicData(@RequestBody(required = false) @Validated DicDataListReqDTO dicDataListReqDTO);

    /**
     * 编辑字典数据
     * @param dicDataEditReqDTO 修改内容
     * @return 修改的主键id
     */
    @PostMapping("/dictionary_data/edit")
    R<Long> editDicData(@RequestBody @Validated DicDataEditReqDTO dicDataEditReqDTO);

}
