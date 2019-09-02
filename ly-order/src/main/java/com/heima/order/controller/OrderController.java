package com.heima.order.controller;

import com.heima.order.dto.CartDTO;
import com.heima.order.dto.OrderDTO;
import com.heima.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @Classname OrderController
 * @Description TODO
 * @Date 2019/9/1 23:04
 * @Created by YJF
 */
@RestController
@RequestMapping
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 创建订单
     * @param orderDTO
     * @return
     */
    @PostMapping
    public ResponseEntity<Long> creatOrder(@RequestBody @Valid OrderDTO orderDTO) {
        return ResponseEntity.ok(orderService.creatOrder(orderDTO));
    }


}
