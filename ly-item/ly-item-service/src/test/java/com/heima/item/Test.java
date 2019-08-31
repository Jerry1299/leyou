package com.heima.item;

import com.heima.LyItem;
import com.heima.item.controller.GoodsController;
import com.heima.item.dto.SkuDTO;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @Classname Test
 * @Description TODO
 * @Date 2019/8/23 22:54
 * @Created by YJF
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = LyItem.class)
public class Test {

    @Autowired
    GoodsController goodsController;

    @org.junit.Test
    public void querySkuBySpuIdTest() {
        ResponseEntity<List<SkuDTO>> skuList = goodsController.querySkuDTOBySpuId(2L);
        System.out.println("skuList = " + skuList);
    }

}
