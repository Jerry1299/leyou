package com.heima.item.service;

import com.heima.common.constants.ExceptionEnum;
import com.heima.common.exception.LyException;
import com.heima.common.utils.BeanHelper;
import com.heima.item.dto.CategoryDTO;
import com.heima.item.entity.Category;
import com.heima.item.mapper.CategoryMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Classname CategoryService
 * @Description TODO
 * @Date 2019/8/14 16:23
 * @Created by YJF
 */
@Service
public class CategoryService {

    @Autowired
    CategoryMapper categoryMapper;

    public List<CategoryDTO> queryListByParent(Long id) {
        Category category = new Category();
        category.setParentId(id);
        List<Category> categoryList = categoryMapper.select(category);
        if (CollectionUtils.isEmpty(categoryList)) {
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }

        return BeanHelper.copyWithCollection(categoryList, CategoryDTO.class);
    }

    /**
     * 根据ids查询CategoryList
     * @param ids
     * @return
     */
    public List<CategoryDTO> queryCategoryByIds(List<Long> ids) {

        List<Category> categoryList = categoryMapper.selectByIdList(ids);
        if (CollectionUtils.isEmpty(categoryList)) {
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        return BeanHelper.copyWithCollection(categoryList, CategoryDTO.class);

    }

}
