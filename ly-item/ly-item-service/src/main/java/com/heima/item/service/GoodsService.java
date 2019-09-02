package com.heima.item.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.heima.common.constants.ExceptionEnum;
import com.heima.common.constants.MQConstants;
import com.heima.common.exception.LyException;
import com.heima.common.utils.BeanHelper;
import com.heima.common.vo.PageResult;
import com.heima.item.dto.*;
import com.heima.item.entity.Sku;
import com.heima.item.entity.Spu;
import com.heima.item.entity.SpuDetail;
import com.heima.item.mapper.SkuMapper;
import com.heima.item.mapper.SpuDetailMapper;
import com.heima.item.mapper.SpuMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Classname GoodsService
 * @Description TODO
 * @Date 2019/8/19 21:57
 * @Created by YJF
 */
@Service
@Slf4j
public class GoodsService {

    @Autowired
    SpuMapper spuMapper;

    @Autowired
    SpuDetailMapper spuDetailMapper;

    @Autowired
    SkuMapper skuMapper;

    @Autowired
    CategoryService categoryService;

    @Autowired
    BrandService brandService;

    /**
     * 分页查询所有SpuDTO
     * @param key
     * @param saleable
     * @param page
     * @param rows
     * @return
     */
    @Transactional
    public PageResult<SpuDTO> getSpuPage(String key, Boolean saleable, Integer page, Integer rows) {
//        1.使用PageHelper进行分页查询
        Page<Object> objects = PageHelper.startPage(page, rows);
//        2.设置搜索条件,查询所有Spu
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
//        2.1搜索条件过滤
        if (StringUtils.isNotBlank(key)) {
            criteria.andLike("name", "%" + key + "%");
        }
//        2.2上下架过滤
        if (saleable != null) {
            criteria.andEqualTo("saleable",saleable);
        }
//        2.3默认按时间排序
        example.setOrderByClause("update_time DESC");
        List<Spu> spuList = spuMapper.selectByExample(example);

        List<SpuDTO> spuDTOS = BeanHelper.copyWithCollection(spuList, SpuDTO.class);

//        查询Brand和Category
        for (SpuDTO spuDTO : spuDTOS) {

//            查询BrandName
            BrandDTO brandDTO = brandService.queryBrandById(spuDTO.getBrandId());
            spuDTO.setBrandName(brandDTO.getName());

//            查询CategoryName拼接字符串
            String categoryNames = categoryService.queryCategoryByIds(spuDTO.getCategoryIds())
                    .stream().map(CategoryDTO::getName).collect(Collectors.joining("/"));
            spuDTO.setCategoryName(categoryNames);
        }

        return new PageResult<>((int) objects.getTotal(), spuDTOS);
    }

    /**
     * 添加Goods
     * @param spuDTO
     */

    @Transactional
    public void addGoods(SpuDTO spuDTO) {
//        1.添加Spu
        Spu spu = BeanHelper.copyProperties(spuDTO, Spu.class);
        spu.setSaleable(false);
        int count = spuMapper.insertSelective(spu);
        if (count != 1) {
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
//        2.添加SpuDetail
        SpuDetail spuDetail = BeanHelper.copyProperties(spuDTO.getSpuDetail(), SpuDetail.class);
        Long spuId = spu.getId();
        spuDetail.setSpuId(spuId);
//        spuDetail.setCreateTime(null);
        count = spuDetailMapper.insertSelective(spuDetail);
        if (count != 1) {
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
//        3.添加Sku
        List<SkuDTO> skuDTOS = spuDTO.getSkus();
        for (SkuDTO skuDTO : skuDTOS) {
            Sku sku = BeanHelper.copyProperties(skuDTO, Sku.class);
            sku.setSpuId(spuId);
            sku.setEnable(false);
            count = skuMapper.insertSelective(sku);
            if (count != 1) {
                throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
            }
        }
    }


    @Autowired
    AmqpTemplate amqpTemplate;

    /**
     * 根据id将商品进行上下架
     * @param id
     * @param saleable
     */
    @Transactional
    public void saleableSpu(Long id, Boolean saleable) {

        try {
//        更新spu的上下架
            Spu spu = new Spu();
            spu.setId(id);
            spu.setSaleable(saleable);

            int count = spuMapper.updateByPrimaryKeySelective(spu);
            if (count != 1) {
                throw new LyException(ExceptionEnum.UPDATE_OPERATION_FAIL);
            }

//        更新sku的上下架
            Sku sku = new Sku();
            sku.setEnable(saleable);
            Example example = new Example(Sku.class);
            example.createCriteria().andEqualTo("spuId", id);
            skuMapper.updateByExampleSelective(sku, example);


//        更新上下架后发送消息
            String routingKey = saleable ? MQConstants.RoutingKey.ITEM_UP_KEY : MQConstants.RoutingKey.ITEM_DOWN_KEY;
            amqpTemplate.convertAndSend(MQConstants.Exchange.ITEM_EXCHANGE_NAME, routingKey, id);
        } catch (RuntimeException e) {
            log.error("更新商品上下架属性失败,原因:{}", e.getMessage());
            throw new LyException(ExceptionEnum.UPDATE_OPERATION_FAIL);
        }


    }

    /**
     * 根据spuId查询SpuDetail
     * @param id
     * @return
     */
    @Transactional
    public SpuDetailDTO querySpuDetailById(Long id) {
        SpuDetail spuDetail = spuDetailMapper.selectByPrimaryKey(id);
        if (spuDetail == null) {
            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        return BeanHelper.copyProperties(spuDetail, SpuDetailDTO.class);
    }

    /**
     * 根据spuId查询sku
     * @param id
     * @return
     */
    @Transactional
    public List<SkuDTO> querySkuDTOBySpuId(Long id) {

        Sku sku = new Sku();
        sku.setSpuId(id);
        List<Sku> skus = skuMapper.select(sku);
        if (CollectionUtils.isEmpty(skus)) {
            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        return BeanHelper.copyWithCollection(skus, SkuDTO.class);
    }

    /**
     * 根据spuId查询Spu
     * @param id
     * @return
     */
    @Transactional
    public SpuDTO querySpuById(Long id) {
        Spu spu = spuMapper.selectByPrimaryKey(id);
        SpuDTO spuDTO = BeanHelper.copyProperties(spu, SpuDTO.class);
        spuDTO.setSpuDetail(querySpuDetailById(id));
        spuDTO.setSkus(querySkuDTOBySpuId(id));
        return spuDTO;
    }


    @Transactional
    public void updataGoods(SpuDTO spuDTO) {
//        1.判断spuDTO是否为空
        if (spuDTO == null || spuDTO.getId() == null) {
            throw new LyException(ExceptionEnum.INVALID_PARAM_ERROR);
        }
        Long spuDTOId = spuDTO.getId();
//        2.删除和添加skus
        Sku sku = new Sku();
        sku.setSpuId(spuDTOId);
        int count = skuMapper.delete(sku);
        if (count < 1) {
            throw new LyException(ExceptionEnum.UPDATE_OPERATION_FAIL);
        }
        List<SkuDTO> skus = spuDTO.getSkus();
        for (SkuDTO skuDTO : skus) {
            Sku sku_new = BeanHelper.copyProperties(skuDTO, Sku.class);
            sku_new.setSpuId(spuDTOId);
            count = skuMapper.insert(sku_new);
            if (count != 1) {
                throw new LyException(ExceptionEnum.UPDATE_OPERATION_FAIL);
            }
        }
//        3.删除和添加spuDetail
        SpuDetail spuDetail = new SpuDetail();
        spuDetail.setSpuId(spuDTOId);
        count = spuDetailMapper.delete(spuDetail);
        if (count != 1) {
            throw new LyException(ExceptionEnum.UPDATE_OPERATION_FAIL);
        }
        SpuDetailDTO spuDetailDTO = spuDTO.getSpuDetail();
        SpuDetail spuDetail_new = BeanHelper.copyProperties(spuDetailDTO, SpuDetail.class);
        spuDetail_new.setSpuId(spuDTOId);
        count = spuDetailMapper.insert(spuDetail_new);
        if (count != 1) {
            throw new LyException(ExceptionEnum.UPDATE_OPERATION_FAIL);
        }

//        4.修改spu
        Spu spu = BeanHelper.copyProperties(spuDTO, Spu.class);
        count = spuMapper.updateByPrimaryKeySelective(spu);
        if (count != 1) {
            throw new LyException(ExceptionEnum.UPDATE_OPERATION_FAIL);
        }


    }


    /**
     * 根据SkuIds查询Skus
     * @param ids
     * @return
     */
    public List<SkuDTO> querySkusBySkuIds(List<Long> ids) {
        List<Sku> skus = skuMapper.selectByIdList(ids);
        if (CollectionUtils.isEmpty(skus)) {
            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        return BeanHelper.copyWithCollection(skus,SkuDTO.class);
    }
}
