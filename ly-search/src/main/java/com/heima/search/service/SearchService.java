package com.heima.search.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.heima.common.utils.BeanHelper;
import com.heima.common.utils.JsonUtils;
import com.heima.common.vo.PageResult;
import com.heima.item.client.ItemClient;
import com.heima.item.dto.*;
import com.heima.search.dto.GoodsDTO;
import com.heima.search.dto.SearchRequest;
import com.heima.search.pojo.Goods;
import com.heima.search.repository.GoodsRepository;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Classname SearchService
 * @Description TODO
 * @Date 2019/8/22 20:23
 * @Created by YJF
 */
@Service
public class SearchService {

    @Autowired
    ItemClient itemClient;

    @Transactional
    public Goods buildGoods(SpuDTO spuDTO) {

//        1.搜索信息的拼接
//        1.1分类信息拼接
        String cateogoryNames = itemClient.queryListById(spuDTO.getCategoryIds())
                .stream().map(CategoryDTO::getName).collect(Collectors.joining(","));
//        1.2品牌信息
        BrandDTO brandDTO = itemClient.queryById(spuDTO.getBrandId());
        String brandName = brandDTO.getName();
//        1.3名称信息
        String spuDTOName = spuDTO.getName();
        String all = cateogoryNames + brandName + spuDTOName;
//        2.spu下所有sku的json
        List<SkuDTO> skuDTOS = itemClient.querySkuDTOBySpuId(spuDTO.getId());

//        2.1将sku中的id,price,title,image保存到map中
        List<Map<String,Object>> skuMap = new ArrayList<>();

        for (SkuDTO skuDTO : skuDTOS) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("id", skuDTO.getId());
            map.put("price", skuDTO.getPrice());
            map.put("title", skuDTO.getTitle());
            map.put("image", StringUtils.substringBefore(skuDTO.getImages(), ","));
            skuMap.add(map);
        }
//        3.所有sku的价格集合
        Set<Long> prices = skuDTOS.stream().map(SkuDTO::getPrice).collect(Collectors.toSet());
//        4.spu的规格参数
        Map<String, Object> specs = new HashMap<>();
//        4.1获取规格参数的name为key,为此specParam中的内容
        List<SpecParamDTO> specParamDTOS = itemClient.queryParamsByGroup(null, spuDTO.getCid3(), true);
//        4.2获取规格参数的值,为spuDetail中的内容
        SpuDetailDTO spuDetailDTO = itemClient.querySpuDetailById(spuDTO.getId());
//        4.2.1通用规格参数
        Map<Long, Object> genericSpec = JsonUtils.toMap(spuDetailDTO.getGenericSpec(), Long.class, Object.class);
//        4.2.2特殊规格参数
        Map<Long, Object> specialSpec = JsonUtils.toMap(spuDetailDTO.getSpecialSpec(), Long.class, Object.class);

//        5.将key和value放入map中
        for (SpecParamDTO specParamDTO : specParamDTOS) {
            String key = specParamDTO.getName();
            Object value = null;
//            判断是否为通用规格
            if (specParamDTO.getGeneric()) {
                value = genericSpec.get(specParamDTO.getId());
            } else {
                value = specialSpec.get(specParamDTO.getId());
            }

//            如果是数字类型,进行分段
            if (specParamDTO.getNumeric()) {
                value = chooseSegment(specParamDTO, value);

            }

//            将key和value保存到map中
            specs.put(key, value);
        }

        Goods goods = new Goods();
//        设置goods中的属性值
//        @Id
//        @Field(type = FieldType.Keyword)
//        private Long id;//spuid
        goods.setId(spuDTO.getId());
//        @Field(type = FieldType.Keyword,index = false)
//        private String subTitle;//副标题
        goods.setSubTitle(spuDTO.getSubTitle());
//        @Field(type = FieldType.Keyword,index = false)
//        private String skus;//所有sku
        goods.setSkus(JsonUtils.toString(skuMap));
//        @Field(type = FieldType.Text, analyzer = "ik_max_word")
//        private String all;//所有需要被搜索的信息,标题,分类,品牌
        goods.setAll(all);
//
//        private Long brandId;//品牌id
        goods.setBrandId(spuDTO.getBrandId());
//        private Long categoryId;//3级分类id
        goods.setCategoryId(spuDTO.getCid3());
//        private Long createTime;//spu创建时间

//        private Set<Long> price;//价格
        goods.setPrice(prices);
//        private Map<String ,Object> specs;//可以被搜索的规格参数
        goods.setSpecs(specs);

        return goods;
    }

//
    private String chooseSegment(SpecParamDTO specParamDTO, Object value) {
        if (value == null || StringUtils.isBlank(value.toString())) {
            return "其他";
        }

        double val = NumberUtils.toDouble(value.toString());
        String result = "其他";
//                获取所有分段值
        String[] segments = specParamDTO.getSegments().split(",");
        for (String segment : segments) {
//                    获取该分段的前后值
            String[] segs = segment.split("-");
            double begin = NumberUtils.toDouble(segs[0]);
//                    如果无上限,设置end为double无穷,有上限则end为上限
            double end = Double.MAX_VALUE;
            if (segs.length == 2) {
                end = NumberUtils.toDouble(segs[1]);
            }
//                    判断val是否在当前范围内
            if (val >= begin && val < end) {
                if (segs.length == 1) {
                    result = segs[0] + specParamDTO.getUnit() + "以上";
                } else if (begin == 0) {
                    result = segs[1] + specParamDTO.getUnit() + "以下";
                } else {
                    result = segment + specParamDTO.getUnit();
                }
                break;
            }
        }

        return result;
    }


    @Autowired
    GoodsRepository goodsRepository;

    @Transactional
    public PageResult<GoodsDTO> queryGoodsPage(SearchRequest request) {

//        创建查询构造器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
//        设置查询字段
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id", "skus", "subTitle"}, null));
//        设置搜索关键字
        String key = request.getKey();
        queryBuilder.withQuery(QueryBuilders.matchQuery("all", key).operator(Operator.AND));
//        设置分页
        int page = request.getPage();
        int size = request.getSize();
        queryBuilder.withPageable(PageRequest.of(page - 1, size));

//        进行查询
        Page<Goods> result = goodsRepository.search(queryBuilder.build());
        int totalPages = result.getTotalPages();
        long total = result.getTotalElements();
        List<Goods> list = result.getContent();
        List<GoodsDTO> goodsDTOS = BeanHelper.copyWithCollection(list, GoodsDTO.class);

        return new PageResult((int) total,totalPages,goodsDTOS);
    }

    /**
     * 根据spuId创建索引
     * @param id
     */
    public void createIndex(Long id) {
//        查询spuDTO
        SpuDTO spuDTO = itemClient.querySpuById(id);
//        构建goods对象
        Goods goods = buildGoods(spuDTO);
//        将goods对象保存到索引库中
        goodsRepository.save(goods);
    }

    /**
     * 根据spuId删除索引
     */
    public void deleteById(Long id) {
        goodsRepository.deleteById(id);
    }


}
