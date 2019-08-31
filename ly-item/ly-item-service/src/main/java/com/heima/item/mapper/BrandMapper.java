package com.heima.item.mapper;

import com.heima.item.entity.Brand;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BrandMapper extends Mapper<Brand> {

    /**
     * 向tb_brand_category中间表添加数据
     * @param brandId
     * @param ids
     * @return
     */
    int insertBrandIdAndCategoryIds(@Param("brandId") Long brandId, @Param("categoryIds") List<Integer> ids);

    /**
     * 根据CategoryId查询BrandList
     * @param id
     * @return
     */
    List<Brand> queryBrandsByCateoryId(@Param("id") Long id);
}
