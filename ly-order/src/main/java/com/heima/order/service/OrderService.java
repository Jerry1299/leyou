package com.heima.order.service;

import com.heima.common.constants.ExceptionEnum;
import com.heima.common.exception.LyException;
import com.heima.common.threadlocals.UserHolder;
import com.heima.common.utils.BeanHelper;
import com.heima.common.utils.IdWorker;
import com.heima.item.client.ItemClient;
import com.heima.item.dto.SkuDTO;
import com.heima.order.dto.CartDTO;
import com.heima.order.dto.OrderDTO;
import com.heima.order.entity.Order;
import com.heima.order.entity.OrderDetail;
import com.heima.order.entity.OrderLogistics;
import com.heima.order.enums.OrderStatusEnum;
import com.heima.order.mapper.OrderDetailMapper;
import com.heima.order.mapper.OrderLogisticMapper;
import com.heima.order.mapper.OrderMapper;
import com.heima.user.client.UserClient;
import com.heima.user.dto.AddressDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private OrderLogisticMapper orderLogisticMapper;

    @Autowired
    private UserClient userClient;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private ItemClient itemClient;

    /**
     * 创建订单
     * @param orderDTO 订单信息
     * @return 订单id
     */
    @Transactional
    public Long creatOrder(OrderDTO orderDTO) {

//        创建要保存的Order
        Order order = new Order();
//        1.生成OrderId
        long orderId = idWorker.nextId();
        order.setOrderId(orderId);
//        2.保存金额信息
//        查询订单中的所有商品
        List<CartDTO> carts = orderDTO.getCarts();
//        获取skuId集合
        List<Long> skuIds = carts.stream().map(CartDTO::getSkuId).collect(Collectors.toList());
//        将skuId和相应的数量转为map
        Map<Long, Integer> numMap = carts.stream().collect(Collectors.toMap(CartDTO::getSkuId, CartDTO::getNum));
//        获取sku集合
        List<SkuDTO> skuDTOS = itemClient.querySkusBySkuIds(skuIds);
        ArrayList<OrderDetail> orderDetails = new ArrayList<>();
        Long totalPrice = 0L ;
        for (SkuDTO skuDTO : skuDTOS) {
            Integer skuNum = numMap.get(skuDTO.getId());
            totalPrice += skuDTO.getPrice() * skuNum;
//            将商品信息保存到OrderDetail中
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setSkuId(skuDTO.getId());
            orderDetail.setNum(skuNum);
            orderDetail.setTitle(skuDTO.getTitle());
            orderDetail.setPrice(skuDTO.getPrice());
            orderDetail.setOwnSpec(skuDTO.getOwnSpec());
            orderDetails.add(orderDetail);
        }
//        2.1保存订单金额
        order.setTotalFee(totalPrice);
//        2.2保存支付类型
        order.setPaymentType(orderDTO.getPaymentType());
//        2.3保存实付金额
        order.setPostFee(0L);
        order.setActualFee(totalPrice+order.getPostFee());
//        3.保存用户信息
//        获取用户id
        Long userId = UserHolder.getUser().getId();
//        4.保存订单状态
        order.setStatus(OrderStatusEnum.INIT.value());
//        将订单保存到数据库
        int count = orderMapper.insert(order);
        if (count != 1) {
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
//        将订单详情保存到数据库
        count = orderDetailMapper.insertList(orderDetails);
        if (count !=1) {
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }

//        获取地址
        Long addressId = orderDTO.getAddressId();
        AddressDTO addressDTO = userClient.queryAddressById(userId,addressId);
        OrderLogistics orderLogistics = BeanHelper.copyProperties(addressDTO, OrderLogistics.class);
        orderLogistics.setOrderId(orderId);
//        将订单地址保存到数据库中
        count = orderLogisticMapper.insert(orderLogistics);
        if (count != 1) {
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }

//        5.减库存
        itemClient.minusStock(numMap);


        return orderId;
    }
}
