package com.heima.item.entity;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @Classname Brand
 * @Description TODO
 * @Date 2019/8/14 22:30
 * @Created by YJF
 */
@Table(name = "tb_brand")
@Data
public class Brand {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;
    private String name;
    private String image;
    private String letter;
    private Date createTime;
    private Date updateTime;

}
