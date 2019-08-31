package com.heima.item.controller;


import com.heima.common.constants.ExceptionEnum;
import com.heima.common.exception.LyException;
import com.heima.pojo.Item;
import com.heima.item.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Classname ItemController
 * @Description TODO
 * @Date 2019/8/13 17:20
 * @Created by YJF
 */
@RestController
public class ItemController {

    @Autowired
    ItemService itemService;

    @PostMapping("item")
    public ResponseEntity<Item> saveItem(Item item) {
        if (item.getPrice() == null) {
            throw new LyException(ExceptionEnum.PRICE_NOT_BE_NULL);
        }

        if (item.getName().isEmpty()) {
            throw new LyException(ExceptionEnum.NAME_NOT_BE_NULL);
        }

        Item result = itemService.saveItem(item);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

}
