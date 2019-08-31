package com.heima.test;

import com.heima.common.vo.PageResult;
import com.heima.item.client.ItemClient;
import com.heima.item.dto.CategoryDTO;
import com.heima.item.dto.SpuDTO;
import com.heima.search.LySearch;
import com.heima.search.pojo.Goods;
import com.heima.search.repository.GoodsRepository;
import com.heima.search.service.SearchService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Classname CategoryClientTest
 * @Description TODO
 * @Date 2019/8/22 15:48
 * @Created by YJF
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = LySearch.class)
public class CategoryClientTest {

    @Autowired
    private ItemClient itemClient;

    @Autowired
    private SearchService searchService;

    @Autowired
    GoodsRepository goodsRepository;

    @Test
    public void queryByIdList() {
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        List<CategoryDTO> categoryDTOS = itemClient.queryListById(ids);
        for (CategoryDTO categoryDTO : categoryDTOS) {
            System.out.println("categoryDTO = " + categoryDTO);
        }
        Assert.assertEquals(3,categoryDTOS.size());
    }

    @Test
    public void loadData() {
        Integer page = 1 ,row = 100 , size =0;

        do {
//            查询spu
            try {
                PageResult<SpuDTO> spuPage = itemClient.getSpuPage(null, true, page, row);
                List<SpuDTO> spuDTOS = spuPage.getItems();
//            根据spu构建goods
                List<Goods> goodsList = spuDTOS.stream().map(searchService::buildGoods).collect(Collectors.toList());
//            将goods保存到es索引库中
                goodsRepository.saveAll(goodsList);
//            当当前页面无法全部展示时,再加一页
                page++;
                size = goodsList.size();
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }

        } while (size ==100);

    }


    @Test
    public void querySpuByIdTest() {
        SpuDTO spuDTO = itemClient.querySpuById(141L);
        System.out.println(spuDTO.toString());
    }


}
