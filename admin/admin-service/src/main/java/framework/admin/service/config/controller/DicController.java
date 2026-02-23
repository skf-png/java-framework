package framework.admin.service.config.controller;

import framework.admin.api.config.domain.DTO.*;
import framework.admin.api.config.domain.VO.DicDataVO;
import framework.admin.api.config.domain.VO.DicTypeVO;
import framework.admin.api.config.feign.DicFeignClient;
import framework.admin.service.config.service.DicService;
import framework.domain.R;
import framework.domain.domain.VO.BasePageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class DicController implements DicFeignClient {
    @Autowired
    DicService dicService;

    @Override
    public R<Long> addType(DicTypeWriteReqDTO dicTypeWriteReqDTO) {
        Long res = dicService.addDicType(dicTypeWriteReqDTO);
        return R.success(res);
    }

    @Override
    public R<BasePageVO<DicTypeVO>> listType(DicTypeListReqDTO dicTypeListReqDTO) {
        BasePageVO<DicTypeVO> res = dicService.listType(dicTypeListReqDTO);
        return R.success(res);
    }

    @Override
    public R<Long> editType(DicTypeWriteReqDTO dicTypeWriteReqDTO) {
        Long res = dicService.editType(dicTypeWriteReqDTO);
        return R.success(res);
    }

    @Override
    public R<Long> addDicData(DicDataAddReqDTO dicDataAddReqDTO) {
        Long res = dicService.addDicData(dicDataAddReqDTO);
        return R.success(res);
    }

    @Override
    public R<BasePageVO<DicDataVO>> listDicData(DicDataListReqDTO dicDataListReqDTO) {
        BasePageVO<DicDataVO> res = dicService.listDicData(dicDataListReqDTO);
        return R.success(res);
    }

    @Override
    public R<Long> editDicData(DicDataEditReqDTO dicDataEditReqDTO) {
        Long res = dicService.editDicData(dicDataEditReqDTO);
        return R.success(res);
    }

    @Override
    public List<DicDataDTO> getDicDataByType(String typeKey) {
        List<DicDataDTO> res = dicService.getDicDataByType(typeKey);
        return res;
    }

    @Override
    public Map<String, List<DicDataDTO>> getDicDataByTypes(List<String> typeKeys) {
        Map<String, List<DicDataDTO>> res = dicService.getDicDataByTypes(typeKeys);
        return res;
    }

    @Override
    public DicDataDTO getDicDataByKey(String dataKey) {
        DicDataDTO res = dicService.getDicDataByKey(dataKey);
        return res;
    }

    @Override
    public List<DicDataDTO> getDicDataByKeys(List<String> datakeys) {
        List<DicDataDTO> res = dicService.getDicDataByKeys(datakeys);
        return res;
    }
}
