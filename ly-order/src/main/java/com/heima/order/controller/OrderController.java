package com.heima.order.controller;

import com.heima.order.dto.CartDTO;
import com.heima.order.dto.OrderDTO;
import com.heima.order.service.OrderService;
import com.heima.order.utils.PayHelper;
import com.heima.order.vo.OrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.xml.ws.Response;
import java.util.List;

/**
 * @Classname OrderController
 * @Description TODO
 * @Date 2019/9/1 23:04
 * @Created by YJF
 */
@RestController
@RequestMapping("order")
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

    /**
     * 根据订单Id查询订单
     * @param orderId 订单Id
     * @return 订单Json
     */
    @GetMapping("{id}")
    public ResponseEntity<OrderVO> queryOrderById(@PathVariable("id") Long orderId) {

        return ResponseEntity.ok(orderService.queryOrderById(orderId));

    }

    /**
     * 根据OrderId获取支付链接
     * @param orderId
     * @return
     */
    @GetMapping("url/{id}")
    public ResponseEntity<String> getPayUrl(@PathVariable("id") Long orderId) {
        return ResponseEntity.ok(orderService.createPayUrl(orderId));
    }


    /**
     * 根据orderId查询订单状态
     * @param orderId
     * @return
     */
    @GetMapping("/state/{id}")
    public ResponseEntity<Integer> queryPayState(@PathVariable("id") Long orderId) {
        return ResponseEntity.ok(orderService.queryPayState(orderId));
    }




}
