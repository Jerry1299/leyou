package com.heima.search.mq;

import com.heima.common.constants.MQConstants;
import com.heima.search.service.SearchService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.ws.BindingType;

/**
 * @Classname ItemListener
 * @Description TODO
 * @Date 2019/8/26 22:40
 * @Created by YJF
 */
@Component
public class ItemListener {

    @Autowired
    SearchService searchService;

    /**
     * 监听商品上架,添加商品索引
     * @param spuId
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = MQConstants.Queue.SEARCH_ITEM_UP,durable = "true"),
            exchange = @Exchange(name = MQConstants.Exchange.ITEM_EXCHANGE_NAME,type = ExchangeTypes.TOPIC),
            key = MQConstants.RoutingKey.ITEM_UP_KEY
    ))
    public void listenItemUp(Long spuId) {
//        商品上架时,新增商品索引
        if (spuId != null) {
            searchService.createIndex(spuId);
            System.out.println("索引已新增");
        }
    }

    /**
     * 监听商品下架,删除商品索引
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = MQConstants.Queue.SEARCH_ITEM_DOWN,durable = "true"),
            exchange = @Exchange(name = MQConstants.Exchange.ITEM_EXCHANGE_NAME,type = ExchangeTypes.TOPIC),
            key = MQConstants.RoutingKey.ITEM_DOWN_KEY
    ))
    public void listenItemDown(Long spuId) {
//        商品下架时,删除商品索引
        if (spuId != null) {
            searchService.deleteById(spuId);
            System.out.println("索引已删除");
        }
    }

}
