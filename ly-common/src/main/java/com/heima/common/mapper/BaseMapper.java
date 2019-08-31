package com.heima.common.mapper;


import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.common.Mapper;

public interface BaseMapper<T> extends Mapper<T>, IdListMapper<T,Long> {
}
