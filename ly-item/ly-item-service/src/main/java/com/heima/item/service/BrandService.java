package com.heima.item.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.heima.common.constants.ExceptionEnum;
import com.heima.common.exception.LyException;
import com.heima.common.utils.BeanHelper;
import com.heima.common.vo.PageResult;
import com.heima.item.dto.BrandDTO;
import com.heima.item.entity.Brand;
import com.heima.item.mapper.BrandMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @Classname BrandService
 * @Description TODO
 * @Date 2019/8/14 22:29
 * @Created by YJF
 */
@Service
public class BrandService {

    @Autowired
    BrandMapper brandMapper;

    /**
     * 分页查询Brand
     * @param key
     * @param page
     * @param rows
     * @param sortBy
     * @param desc
     * @return
     */
    public PageResult<BrandDTO> queryBrandPage(String key, Integer page,Integer rows, String sortBy, Boolean desc) {
//        分页助手进行分页
        Page<Object> objects = PageHelper.startPage(page, rows);
//        创建查询example
        Example example = new Example(Brand.class);

//        判断key是否为空,不为空加入example
        if (StringUtils.isNoneBlank(key)) {
            example.createCriteria().orLike("name", "%" + key + "%")
                    .orEqualTo("letter", key);
        }

//        判断sortBy是否为空,不为空则加入example
        if (StringUtils.isNoneBlank(sortBy)) {
            String orderByClause = sortBy + (desc ? " DESC" : " ASC");
            example.setOrderByClause(orderByClause);
        }
//        按照example进行分页查询
        List<Brand> brands = brandMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(brands)) {
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }

//        将Brand集合转化为BrandDTO集合
        List<BrandDTO> brandDTOS = BeanHelper.copyWithCollection(brands, BrandDTO.class);

        return new PageResult<BrandDTO>((int) objects.getTotal(),brandDTOS);

    }

    /**
     * 添加Brand
     * @param brandDTO
     * @param ids
     * @return
     */
    public void addBrand(BrandDTO brandDTO, List<Integer> ids) {

//        向tb_brand表中添加Brand,返回主键id
        Brand brand = BeanHelper.copyProperties(brandDTO, Brand.class);
        int count = brandMapper.insert(brand);
        Long brandId = brand.getId();
        if (count != 1) {
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }

//        向tb_brand_category表中添加ids
        count = brandMapper.insertBrandIdAndCategoryIds(brandId, ids);
        if (count != ids.size()) {
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }

    }

    /**
     * 根据id查询BrandDTO
     * @param id
     * @return
     */
    public BrandDTO queryBrandById(Long id) {
        Brand brand = brandMapper.selectByPrimaryKey(id);
        if (brand == null) {
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return BeanHelper.copyProperties(brand, BrandDTO.class);
    }

    /**
     * 根据CategoryId查询BrandDTOList
     * @param id
     * @return
     */
    public List<BrandDTO> queryBrandsByCategoryId(Long id) {
        List<Brand> brandList = brandMapper.queryBrandsByCateoryId(id);
        if (CollectionUtils.isEmpty(brandList)) {
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return BeanHelper.copyWithCollection(brandList, BrandDTO.class);
    }


}
