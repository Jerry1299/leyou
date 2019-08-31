package com.heima.item.service;

import com.heima.pojo.Item;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * @Classname ItemService
 * @Description TODO
 * @Date 2019/8/13 17:17
 * @Created by YJF
 */
@Service
public class ItemService {

    public Item saveItem(Item item) {
        int id = new Random().nextInt(100);
        item.setId(id);
        return item;
    }

}
