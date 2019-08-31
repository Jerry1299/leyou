package com.heima.search.pojo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Map;
import java.util.Set;

/**
 * @Classname Goods
 * @Description TODO
 * @Date 2019/8/22 15:15
 * @Created by YJF
 */
@Data
@Document(indexName = "goods",type = "docs",shards = 1,replicas = 1)
public class Goods {
    @Id
    @Field(type = FieldType.Keyword)
    private Long id;//spuid
    @Field(type = FieldType.Keyword,index = false)
    private String subTitle;//副标题
    @Field(type = FieldType.Keyword,index = false)
    private String skus;//所有sku
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String all;//所有需要被搜索的信息,标题,分类,品牌

    private Long brandId;//品牌id
    private Long categoryId;//3级分类id
    private Long createTime;//spu创建时间
    private Set<Long> price;//价格
    private Map<String ,Object> specs;//可以被搜索的规格参数


}
