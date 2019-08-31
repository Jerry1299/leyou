package com.heima.item.service;

import com.heima.common.constants.ExceptionEnum;
import com.heima.common.exception.LyException;
import com.heima.common.utils.BeanHelper;
import com.heima.item.dto.SpecGroupDTO;
import com.heima.item.dto.SpecParamDTO;
import com.heima.item.entity.SpecGroup;
import com.heima.item.entity.SpecParam;
import com.heima.item.mapper.SpecGroupMapper;
import com.heima.item.mapper.SpecParamMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Classname SpecGroupService
 * @Description TODO
 * @Date 2019/8/19 15:59
 * @Created by YJF
 */
@Service
public class SpecService {

    @Autowired
    SpecGroupMapper specGroupMapper;


    public List<SpecGroupDTO> queryGroupByCategory(Long id) {

        SpecGroup specGroup = new SpecGroup();
        specGroup.setCid(id);
        List<SpecGroup> specGroups = specGroupMapper.select(specGroup);
        if (CollectionUtils.isEmpty(specGroups)) {
            throw new LyException(ExceptionEnum.SPEC_NOT_FOUND);
        }
        List<SpecGroupDTO> list = new ArrayList<>();
        List<SpecGroupDTO> specGroupDTOS = BeanHelper.copyWithCollection(specGroups, SpecGroupDTO.class);

        return specGroupDTOS;
    }

    @Autowired
    SpecParamMapper specParamMapper;

    /**
     *查询规格参数
     * @param gid 组id
     * @param cid 分类id
     * @param searching 是否搜索
     * @return 规格参数集合
     */
    public List<SpecParamDTO> queryParamsByGroup(Long gid ,Long cid , Boolean searching) {
        SpecParam specParam = new SpecParam();
        specParam.setGroupId(gid);
        specParam.setCid(cid);
        specParam.setSearching(searching);
        List<SpecParam> specParams = specParamMapper.select(specParam);
        if (CollectionUtils.isEmpty(specParams)) {
            throw new LyException(ExceptionEnum.SPEC_NOT_FOUND);
        }
        return BeanHelper.copyWithCollection(specParams,SpecParamDTO.class);
    }


    /**
     * 根据CategoryId查询所有Spec集合
     * @param id
     * @return
     */
    @Transactional
    public List<SpecGroupDTO> querySpecsByCid(Long id) {

//        查询当前分类下所有的规格组
        List<SpecGroupDTO> specGroupDTOS = queryGroupByCategory(id);
//        根据cid查询所有参数
        List<SpecParamDTO> specParamDTOS = queryParamsByGroup(null, id, null);
//        将所有参数按照GroupId进行分组
        Map<Long, List<SpecParamDTO>> paramsMap = specParamDTOS
                .stream()
                .collect(Collectors.groupingBy(SpecParamDTO::getGroupId));
//        将分组后的结果按照GroupId赋值到SpecGroupDTO对象中
        for (SpecGroupDTO specGroupDTO : specGroupDTOS) {
            specGroupDTO.setParams(paramsMap.get(specGroupDTO.getId()));
        }

        return specGroupDTOS;

    }
}
