package framework.admin.service.config.service;

import framework.admin.api.config.domain.DTO.*;
import framework.admin.api.config.domain.VO.DicDataVO;
import framework.admin.api.config.domain.VO.DicTypeVO;
import framework.domain.domain.VO.BasePageVO;

public interface DicService {

    Long addDicType(DicTypeWriteReqDTO dicTypeWriteReqDTO);

    BasePageVO<DicTypeVO> listType(DicTypeListReqDTO dicTypeListReqDTO);

    Long editType(DicTypeWriteReqDTO dicTypeWriteReqDTO);

    Long addDicData(DicDataAddReqDTO dicDataAddReqDTO);

    BasePageVO<DicDataVO> listDicData(DicDataListReqDTO dicDataListReqDTO);

    Long editDicData(DicDataEditReqDTO dicDataEditReqDTO);
}
