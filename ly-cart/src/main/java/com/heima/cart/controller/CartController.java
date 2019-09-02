package com.heima.cart.controller;

import com.heima.cart.entity.Cart;
import com.heima.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Classname CartController
 * @Description TODO
 * @Date 2019/9/1 16:59
 * @Created by YJF
 */
@RestController
@RequestMapping
public class CartController {

    @Autowired
    CartService cartService;

    /**
     * 用户登录后添加商品到购物车
     * @param cart
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> addCart(@RequestBody Cart cart) {
        cartService.addCart(cart);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 查询购物车中的所有商品
     * @return
     */
    @GetMapping("list")
    public ResponseEntity<List<Cart>> queryCartList() {
        return ResponseEntity.ok(cartService.queryCartList());
    }


}
