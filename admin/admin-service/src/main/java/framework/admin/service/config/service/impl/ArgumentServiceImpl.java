package framework.admin.service.config.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import framework.admin.api.config.domain.DTO.ArgumentAddReqDTO;
import framework.admin.api.config.domain.DTO.ArgumentDTO;
import framework.admin.api.config.domain.DTO.ArgumentEditReqDTO;
import framework.admin.api.config.domain.DTO.ArgumentListReqDTO;
import framework.admin.api.config.domain.VO.ArgumentVO;
import framework.admin.service.config.domain.entity.SysArgument;
import framework.admin.service.config.mapper.SysArgumentMapper;
import framework.admin.service.config.service.ArgumentService;
import framework.core.utils.BeanCopyUtil;
import framework.domain.ServiceException;
import framework.domain.domain.VO.BasePageVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Service
@Slf4j
public class ArgumentServiceImpl implements ArgumentService {
    @Autowired
    private SysArgumentMapper sysArgumentMapper;

    @Override
    public Long add(ArgumentAddReqDTO argumentAddReqDTO) {
        //1. 查询名称和key是否唯一
        LambdaQueryWrapper<SysArgument> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .eq(SysArgument::getName, argumentAddReqDTO.getName())
                .or()
                .eq(SysArgument::getConfigKey, argumentAddReqDTO.getConfigKey());
        SysArgument sysArgument = sysArgumentMapper.selectOne(lambdaQueryWrapper);
        //2. 如果存在，抛异常
        if (sysArgument != null) {
            throw new ServiceException("参数名称或者参数key已存在");
        }
        //3. 如果不存在，新增
        sysArgument = new SysArgument();
        sysArgument.setName(argumentAddReqDTO.getName());
        sysArgument.setConfigKey(argumentAddReqDTO.getConfigKey());
        sysArgument.setValue(argumentAddReqDTO.getValue());
        if (StringUtils.isNotBlank(argumentAddReqDTO.getRemark())) {
            sysArgument.setRemark(argumentAddReqDTO.getRemark());
        }
        sysArgumentMapper.insert(sysArgument);
        return sysArgument.getId();
    }

    @Override
    public BasePageVO<ArgumentVO> list(ArgumentListReqDTO argumentListReqDTO) {
        //1. 查询
        LambdaQueryWrapper<SysArgument> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //右查询
        if (StringUtils.isNotBlank(argumentListReqDTO.getName())) {
            lambdaQueryWrapper.likeRight(SysArgument::getName, argumentListReqDTO.getName());
        }
        //相等
        if (StringUtils.isNotBlank(argumentListReqDTO.getConfigKey())) {
            lambdaQueryWrapper.eq(SysArgument::getConfigKey, argumentListReqDTO.getConfigKey());
        }
        Page<SysArgument> page = sysArgumentMapper.selectPage(
                new Page<>(argumentListReqDTO.getPageNo().longValue(), argumentListReqDTO.getPageSize().longValue())
                ,lambdaQueryWrapper
        );
        //2. 构建返回结果
        BasePageVO<ArgumentVO> res = new BasePageVO<>();
        res.setTotals((int) page.getTotal());
        res.setTotalPages((int) page.getPages());
        res.setList(BeanCopyUtil.copyListProperties(page.getRecords(), ArgumentVO::new));
        return res;
    }

    @Override
    public Long edit(ArgumentEditReqDTO argumentEditReqDTO) {
        //1. 查询参数信息是否存在
        LambdaQueryWrapper<SysArgument> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SysArgument::getConfigKey, argumentEditReqDTO.getConfigKey());
        SysArgument sysArgument = sysArgumentMapper.selectOne(lambdaQueryWrapper);
        //1.1 如果不存在
        if (sysArgument == null) {
            throw new ServiceException("参数不存在");
        }
        //2. 查询修改的信息是否唯一
        if (sysArgumentMapper.selectOne(new LambdaQueryWrapper<SysArgument>()
                .eq(SysArgument::getName, argumentEditReqDTO.getName())
                .ne(SysArgument::getConfigKey, argumentEditReqDTO.getConfigKey())
        ) != null) {
            throw new ServiceException("参数名称冲突");
        }
        //3. 执行结果
        sysArgument.setName(argumentEditReqDTO.getName());
        sysArgument.setValue(argumentEditReqDTO.getValue());
        if (StringUtils.isNotBlank(argumentEditReqDTO.getRemark())) {
            sysArgument.setRemark(argumentEditReqDTO.getRemark());
        }
        sysArgumentMapper.updateById(sysArgument);
        return sysArgument.getId();
    }

    @Override
    public ArgumentDTO getConfigKey(String configKey) {
        //1. 查询
        SysArgument sysArgument = sysArgumentMapper.selectOne(new LambdaQueryWrapper<SysArgument>().eq(SysArgument::getConfigKey, configKey));
        //2. 转换
        if (sysArgument != null) {
            ArgumentDTO res = new ArgumentDTO();
            BeanCopyUtil.copyProperties(sysArgument, res);
            return res;
        }
        return null;
    }

    @Override
    public List<ArgumentDTO> getConfigKeys(List<String> configKeys) {
        //1. 查询
        LambdaQueryWrapper queryWrapper = new LambdaQueryWrapper<SysArgument>().in(SysArgument::getConfigKey, configKeys);
        List<SysArgument> sysArguments = sysArgumentMapper.selectList(queryWrapper);
        //2. 转换
        List<ArgumentDTO> res = BeanCopyUtil.copyListProperties(sysArguments, ArgumentDTO::new);
        return res;
    }
}
