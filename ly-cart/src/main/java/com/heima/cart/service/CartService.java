package com.heima.cart.service;

import com.heima.cart.entity.Cart;
import com.heima.common.constants.ExceptionEnum;
import com.heima.common.exception.LyException;
import com.heima.common.threadlocals.UserHolder;
import com.heima.common.utils.JsonUtils;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;


/**
 * @Classname CartService
 * @Description TODO
 * @Date 2019/9/1 17:06
 * @Created by YJF
 */
@Service
public class CartService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String KEY_PREFIX = "ly:cart:uid";

    /**
     * 添加商品到购物车
     * @param cart
     */
    public void addCart(Cart cart) {

//        获取当前用户,作为要操作的redis对象的key
        String key = KEY_PREFIX + UserHolder.getUser().getId();
//        通过key来获取后面要进行操作的redis对象
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(key);
//        获取商品id,作为hashKey
        String hashKey = cart.getSkuId().toString();
//        获取要添加的商品数量
        Integer num = cart.getNum();
//        判断购物车中是否已存在该商品
        Boolean boo = hashOps.hasKey(hashKey);
        if (BooleanUtils.isTrue(boo)) {
//            购物车中存在,对数量进行修改
            cart = JsonUtils.toBean(hashOps.get(hashKey), Cart.class);
            cart.setNum(cart.getNum()+num);
        }

//        将更改写入redis
        hashOps.put(hashKey,JsonUtils.toString(cart));

    }

    /**
     * 查询购物车中的所有商品
     * @return
     */
    public List<Cart> queryCartList() {
//        获取当前登录用户,作为redis的key
        String key = KEY_PREFIX + UserHolder.getUser().getId();
//        先判断是否存在购物车
        Boolean hasKey = redisTemplate.hasKey(key);
        if (BooleanUtils.isFalse(hasKey)) {
//            若不存在购物车,抛异常
            throw new LyException(ExceptionEnum.CARTS_NOT_FOUND);
        }
//        根据key获取redis操作对象,即购物车
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(key);
        List<String> values = hashOps.values();
        if (CollectionUtils.isEmpty(values)) {
            throw new LyException(ExceptionEnum.CARTS_NOT_FOUND);
        }

//        利用stream流将json集合转为Cart集合

        List<Cart> cartList = values.stream()
                .map(json -> JsonUtils.toBean(json, Cart.class))
                .collect(Collectors.toList());

        return cartList;
    }
}
