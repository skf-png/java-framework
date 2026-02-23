package framework.admin.service.config.service;

import framework.admin.api.config.domain.DTO.ArgumentAddReqDTO;
import framework.admin.api.config.domain.DTO.ArgumentDTO;
import framework.admin.api.config.domain.DTO.ArgumentEditReqDTO;
import framework.admin.api.config.domain.DTO.ArgumentListReqDTO;
import framework.admin.api.config.domain.VO.ArgumentVO;
import framework.domain.domain.VO.BasePageVO;

import java.util.List;

public interface ArgumentService {
    Long add(ArgumentAddReqDTO argumentAddReqDTO);

    BasePageVO<ArgumentVO> list(ArgumentListReqDTO argumentListReqDTO);

    Long edit(ArgumentEditReqDTO argumentEditReqDTO);

    ArgumentDTO getConfigKey(String configKey);

    List<ArgumentDTO> getConfigKeys(List<String> configKeys);
}
