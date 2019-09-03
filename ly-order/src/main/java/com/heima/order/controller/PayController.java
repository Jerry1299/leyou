package com.heima.order.controller;

import com.heima.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @Classname PayController
 * @Description TODO
 * @Date 2019/9/3 21:53
 * @Created by YJF
 */
@Slf4j
@RestController
@RequestMapping("pay")
public class PayController {

    @Autowired
    private OrderService orderService;

    /**
     * 支付成功后回调
     *
     * @return
     */
    @PostMapping(value = "/wxpay/notify",produces = "application/xml")
    public Map<String, String> hello(@RequestBody Map<String, String> result) {

//        接收微信请求数据
        log.info("[支付回调]接收微信支付回调,结果:{}", result);
        orderService.handleNotify(result);

//        向微信返回结果
        HashMap<String, String> msg = new HashMap<>();
        msg.put("return_code", "SUCCESS");
        msg.put("return_msg", "OK");

        return msg;

    }
}
