package com.heima.item.mapper;

import com.heima.common.mapper.BaseMapper;
import com.heima.item.entity.Sku;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface SkuMapper extends BaseMapper<Sku> {

    @Update("UPDATE tb_sku SET stock = stock - #{num} WHERE id = #{id}")
    int minusStock(@Param("id") Long id, @Param("num") Integer num);

}
