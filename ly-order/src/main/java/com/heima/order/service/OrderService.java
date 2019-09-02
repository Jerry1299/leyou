package com.heima.order.service;

import com.heima.order.dto.OrderDTO;
import com.heima.order.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Classname OrderService
 * @Description TODO
 * @Date 2019/9/1 23:04
 * @Created by YJF
 */
@Service
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;


    /**
     * 创建订单
     * @param orderDTO
     * @return
     */
    public Long creatOrder(OrderDTO orderDTO) {


        return null;
    }
}
