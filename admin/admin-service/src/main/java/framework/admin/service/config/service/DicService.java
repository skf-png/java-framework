package framework.admin.service.config.service;

import framework.admin.api.config.domain.DTO.*;
import framework.admin.api.config.domain.VO.DicDataVO;
import framework.admin.api.config.domain.VO.DicTypeVO;
import framework.domain.domain.VO.BasePageVO;

import java.util.List;
import java.util.Map;

public interface DicService {

    Long addDicType(DicTypeWriteReqDTO dicTypeWriteReqDTO);

    BasePageVO<DicTypeVO> listType(DicTypeListReqDTO dicTypeListReqDTO);

    Long editType(DicTypeWriteReqDTO dicTypeWriteReqDTO);

    Long addDicData(DicDataAddReqDTO dicDataAddReqDTO);

    BasePageVO<DicDataVO> listDicData(DicDataListReqDTO dicDataListReqDTO);

    Long editDicData(DicDataEditReqDTO dicDataEditReqDTO);

    List<DicDataDTO> getDicDataByType(String typeKey);

    Map<String, List<DicDataDTO>> getDicDataByTypes(List<String> typeKeys);

    DicDataDTO getDicDataByKey(String datakey);

    List<DicDataDTO> getDicDataByKeys(List<String> datakeys);
}
