package com.heima.page.mq;

import com.heima.common.constants.MQConstants;
import com.heima.page.service.PageService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Classname ItemListener
 * @Description TODO
 * @Date 2019/8/25 23:46
 * @Created by YJF
 */
@Component
public class ItemListener {

    @Autowired
    PageService pageService;

    /**
     * 监听商品上架
     * @param spuId
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = MQConstants.Queue.PAGE_ITEM_UP,durable = "true"),
            exchange = @Exchange(name = MQConstants.Exchange.ITEM_EXCHANGE_NAME,type = ExchangeTypes.TOPIC),
            key = MQConstants.RoutingKey.ITEM_UP_KEY
    ))
    public void listenItemUp(Long spuId) {
        if (spuId != null) {
//            商品上架,创建新静态页面
            pageService.createItemHtml(spuId);
            System.out.println("静态页面已创建");
        }
    }

    /**
     * 监听商品下架
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = MQConstants.Queue.PAGE_ITEM_DOWN,durable = "true"),
            exchange = @Exchange(name = MQConstants.Exchange.ITEM_EXCHANGE_NAME,type = ExchangeTypes.TOPIC),
            key = MQConstants.RoutingKey.ITEM_DOWN_KEY
    ))
    public void listenItemDown(Long spuId) {
//        商品下架,删除静态页面
        if (spuId != null) {
            pageService.deleteItemHtml(spuId);
            System.out.println("静态页面已删除");
        }
    }



}
