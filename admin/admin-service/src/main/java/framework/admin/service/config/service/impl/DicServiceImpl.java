package framework.admin.service.config.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import framework.admin.api.config.domain.DTO.*;
import framework.admin.api.config.domain.VO.DicDataVO;
import framework.admin.api.config.domain.VO.DicTypeVO;
import framework.admin.service.config.domain.entity.SysDicData;
import framework.admin.service.config.domain.entity.SysDicType;
import framework.admin.service.config.mapper.SysDicDataMapper;
import framework.admin.service.config.mapper.SysDicTypeMapper;
import framework.admin.service.config.service.DicService;
import framework.core.utils.BeanCopyUtil;
import framework.core.utils.StringUtil;
import framework.domain.ServiceException;
import framework.domain.domain.VO.BasePageVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DicServiceImpl implements DicService {
    @Autowired
    private SysDicTypeMapper sysDicTypeMapper;
    @Autowired
    private SysDicDataMapper sysDicDataMapper;

    @Override
    public Long addDicType(DicTypeWriteReqDTO dicTypeWriteReqDTO) {
        //1. 判断键或者值是否已存在
        LambdaQueryWrapper<SysDicType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysDicType::getValue, dicTypeWriteReqDTO.getValue())
                .or().eq(SysDicType::getTypeKey, dicTypeWriteReqDTO.getTypeKey());
        SysDicType sysDicType = sysDicTypeMapper.selectOne(queryWrapper);

        if (sysDicType != null) {
            throw new ServiceException("键或者值已存在");
        }
        //2. 如果不存在新增
        SysDicType sysDicType1 = new SysDicType();
        sysDicType1.setValue(dicTypeWriteReqDTO.getValue());
        sysDicType1.setTypeKey(dicTypeWriteReqDTO.getTypeKey());
        if (dicTypeWriteReqDTO.getRemark() != null) {
            sysDicType1.setRemark(dicTypeWriteReqDTO.getRemark());
        }
        sysDicTypeMapper.insert(sysDicType1);
        return sysDicType1.getId();
    }

    @Override
    public BasePageVO<DicTypeVO> listType(DicTypeListReqDTO dicTypeListReqDTO) {
        //1. 构建查询sql
        LambdaQueryWrapper<SysDicType> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNoneBlank(dicTypeListReqDTO.getValue())) {
            queryWrapper.likeRight(SysDicType::getValue, dicTypeListReqDTO.getValue());
        }
        if (StringUtils.isNoneBlank(dicTypeListReqDTO.getTypeKey())) {
            queryWrapper.eq(SysDicType::getTypeKey, dicTypeListReqDTO.getTypeKey());
        }
        //2. 分页查询
        Page<SysDicType> typePage = sysDicTypeMapper.selectPage(
                new Page<>(dicTypeListReqDTO.getPageNo().longValue(), dicTypeListReqDTO.getPageSize().longValue())
                ,queryWrapper
        );
        //3. 构建返回结果
        BasePageVO<DicTypeVO> res = new BasePageVO<>();
        res.setTotals(Integer.parseInt(String.valueOf(typePage.getTotal())));
        res.setTotalPages((Integer.parseInt(String.valueOf(typePage.getPages()))));
        res.setList(BeanCopyUtil.copyListProperties(typePage.getRecords(), DicTypeVO::new));
        return res;
    }

    @Override
    public Long editType(DicTypeWriteReqDTO dicTypeWriteReqDTO) {
        //1. 查询该字典类型是否存在
        SysDicType sysDicType = sysDicTypeMapper.selectOne(new LambdaQueryWrapper<SysDicType>().eq(SysDicType::getTypeKey, dicTypeWriteReqDTO.getTypeKey()));
        if (sysDicType == null) {
            throw new ServiceException("字典类型不存在");
        }
        //2. 查询要修改的值是否冲突
        if (sysDicTypeMapper.selectOne(
                new LambdaQueryWrapper<SysDicType>()
                        .ne(SysDicType::getTypeKey, dicTypeWriteReqDTO.getTypeKey())
                        .eq(SysDicType::getValue, dicTypeWriteReqDTO.getValue())
        ) != null) {
            throw new ServiceException("字典类型名称已存在");
        }
        //3. 修改操作
        sysDicType.setValue(dicTypeWriteReqDTO.getValue());
        if (dicTypeWriteReqDTO.getRemark() != null) {
            sysDicType.setRemark(dicTypeWriteReqDTO.getRemark());
        }
        sysDicTypeMapper.updateById(sysDicType);
        //4. 返回结果
        return sysDicType.getId();
    }

    @Override
    public Long addDicData(DicDataAddReqDTO dicDataAddReqDTO) {
        //1. 判断字典类型是否存在
        if (sysDicTypeMapper.selectOne(
                new LambdaQueryWrapper<SysDicType>().eq(SysDicType::getTypeKey, dicDataAddReqDTO.getTypeKey())
        ) == null) {
            throw new ServiceException("字典类型不存在");
        }
        //2. 判断键或者值是否已存在
        LambdaQueryWrapper<SysDicData> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysDicData::getValue, dicDataAddReqDTO.getValue())
                .or().eq(SysDicData::getDataKey, dicDataAddReqDTO.getDataKey());
        SysDicData sysDicData = sysDicDataMapper.selectOne(queryWrapper);

        if (sysDicData != null) {
            throw new ServiceException("新增字典数据键或者值已存在");
        }
        //3. 如果不存在新增
        SysDicData sysDicData1 = new SysDicData();
        sysDicData1.setValue(dicDataAddReqDTO.getValue());
        sysDicData1.setTypeKey(dicDataAddReqDTO.getTypeKey());
        sysDicData1.setDataKey(dicDataAddReqDTO.getDataKey());
        if (dicDataAddReqDTO.getRemark() != null) {
            sysDicData1.setRemark(dicDataAddReqDTO.getRemark());
        }
        if (dicDataAddReqDTO.getSort() != null) {
            sysDicData1.setSort(dicDataAddReqDTO.getSort());
        }
        sysDicDataMapper.insert(sysDicData1);
        return sysDicData1.getId();
    }

    @Override
    public BasePageVO<DicDataVO> listDicData(DicDataListReqDTO dicDataListReqDTO) {
        //1. 构建查询sql
        LambdaQueryWrapper<SysDicData> lambdaWrapper = new LambdaQueryWrapper<>();
        lambdaWrapper.eq(SysDicData::getTypeKey, dicDataListReqDTO.getTypeKey());
        if (StringUtils.isNoneBlank(dicDataListReqDTO.getValue())) {
            lambdaWrapper.likeRight(SysDicData::getValue, dicDataListReqDTO.getValue());
        }
        //排序
        lambdaWrapper.orderByAsc(SysDicData::getSort);
        lambdaWrapper.orderByAsc(SysDicData::getId);
        //2. 分页查询
        Page<SysDicData> page = sysDicDataMapper.selectPage(
                new Page<>(dicDataListReqDTO.getPageNo().longValue(),dicDataListReqDTO.getPageSize().longValue())
                ,lambdaWrapper
        );
        //3. 构建返回结果
        BasePageVO<DicDataVO> res = new BasePageVO<>();
        res.setTotals(((Long)page.getTotal()).intValue());
        res.setTotalPages(((Long)page.getPages()).intValue());
        res.setList(BeanCopyUtil.copyListProperties(page.getRecords(), DicDataVO::new));
        return res;
    }

    @Override
    public Long editDicData(DicDataEditReqDTO dicDataEditReqDTO) {
        //1. 判断字典数据是否存在
        SysDicData sysDicData = sysDicDataMapper.selectOne(
                new LambdaQueryWrapper<SysDicData>().eq(SysDicData::getDataKey, dicDataEditReqDTO.getDataKey())
        );
        if (sysDicData == null) {
            throw new ServiceException("字典数据不存在");
        }
        //2. 判断新增字典数据值是否冲突
        if (sysDicDataMapper.selectOne(
                new LambdaQueryWrapper<SysDicData>()
                        .ne(SysDicData::getDataKey, dicDataEditReqDTO.getDataKey())
                        .eq(SysDicData::getValue, dicDataEditReqDTO.getValue())
        ) != null) {
            throw new ServiceException("该字典数据值已存在");
        }
        //3. 执行编辑
        sysDicData.setValue(dicDataEditReqDTO.getValue());
        if (dicDataEditReqDTO.getRemark() != null) {
            sysDicData.setRemark(dicDataEditReqDTO.getRemark());
        }
        if (dicDataEditReqDTO.getSort() != null) {
            sysDicData.setSort(dicDataEditReqDTO.getSort());
        }
        sysDicDataMapper.updateById(sysDicData);
        //4. 返回结果
        return sysDicData.getId();
    }

    @Override
    public List<DicDataDTO> getDicDataByType(String typeKey) {
        //1. 获取数据
        List<SysDicData> datas = sysDicDataMapper.selectList(new LambdaQueryWrapper<SysDicData>().eq(SysDicData::getTypeKey, typeKey));
        //2. 类型转换
        List<DicDataDTO> res = BeanCopyUtil.copyListProperties(datas, DicDataDTO::new);
        return res;
    }

    @Override
    public Map<String, List<DicDataDTO>> getDicDataByTypes(List<String> typeKeys) {
        //1. 获取数据
        List<SysDicData> data = sysDicDataMapper.selectList(new LambdaQueryWrapper<SysDicData>().in(SysDicData::getTypeKey, typeKeys));
        //2. 转换类型
        List<DicDataDTO> dicDataDTOS = BeanCopyUtil.copyListProperties(data, DicDataDTO::new);
        //3. 转换成map
        Map<String, List<DicDataDTO>> res = new HashMap<>();
        for (DicDataDTO dicDataDTO : dicDataDTOS) {
            List<DicDataDTO> value;
            if (res.get(dicDataDTO.getTypeKey()) == null) {
                value = new ArrayList<>();
                value.add(dicDataDTO);
                res.put(dicDataDTO.getTypeKey(), value);
            } else {
                value = res.get(dicDataDTO.getTypeKey());
                value.add(dicDataDTO);
            }
        }
        return res;
    }

    @Override
    public DicDataDTO getDicDataByKey(String datakey) {
        //1. 获取
        SysDicData data = sysDicDataMapper.selectOne(new LambdaQueryWrapper<SysDicData>().eq(SysDicData::getDataKey, datakey));
        //2. 转换
        DicDataDTO res = new DicDataDTO();
        //防止data==null时候转换报错
        if  (data == null) {
            return null;
        }
        BeanUtils.copyProperties(data, res);
        return res;
    }

    @Override
    public List<DicDataDTO> getDicDataByKeys(List<String> datakeys) {
        List<SysDicData> datas = sysDicDataMapper.selectList(new LambdaQueryWrapper<SysDicData>().in(SysDicData::getDataKey, datakeys));
        List<DicDataDTO> res = BeanCopyUtil.copyListProperties(datas, DicDataDTO::new);

        return res;
    }
}
