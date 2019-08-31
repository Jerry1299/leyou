package com.heima.item.controller;

import com.heima.item.dto.SpuDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GoodsControllerTest {


    @Autowired
    GoodsController goodsController;

    @Test
    public void querySpuById() {
        ResponseEntity<SpuDTO> spuDTOResponseEntity = goodsController.querySpuById(141L);
    }


}