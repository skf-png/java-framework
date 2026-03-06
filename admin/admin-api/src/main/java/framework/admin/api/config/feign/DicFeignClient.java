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
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(value = "admin", contextId = "dicFeignClient")
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
    @PostMapping("/dictionary_type/list")
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
    @PostMapping("/dictionary_data/list")
    R<BasePageVO<DicDataVO>> listDicData(@RequestBody(required = false) @Validated DicDataListReqDTO dicDataListReqDTO);

    /**
     * 编辑字典数据
     * @param dicDataEditReqDTO 修改内容
     * @return 修改的主键id
     */
    @PostMapping("/dictionary_data/edit")
    R<Long> editDicData(@RequestBody @Validated DicDataEditReqDTO dicDataEditReqDTO);

    /**
     * 根据字典类型获取字典数据
     * @param typeKey 字典类型
     * @return 字典数据
     */
    @GetMapping("/dictionary_data/type")
    List<DicDataDTO> getDicDataByType(@RequestParam String typeKey);
    /**
     * 根据多个字典类型获取字典数据
     * @param typeKeys 字典类型
     * @return 字典数据哈希 类型名->字典数据列表
     */
    @PostMapping("/dictionary_data/types")
    Map<String, List<DicDataDTO>> getDicDataByTypes(@RequestBody List<String> typeKeys);

    /**
     * 根据字典数据键获取数据
     * @param dataKey 数据主键
     * @return 字典数据
     */
    @GetMapping("/dictionary_data/key")
    DicDataDTO getDicDataByKey(@RequestParam String dataKey);

    /**
     * 根据多个字典数据键获取数据
     * @param datakeys 数据主键
     * @return 字典数据
     */
    @PostMapping("/dictionary_data/keys")
    List<DicDataDTO> getDicDataByKeys(@RequestBody List<String> datakeys);

}
