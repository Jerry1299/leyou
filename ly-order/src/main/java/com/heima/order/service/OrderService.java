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
import com.heima.order.utils.PayHelper;
import com.heima.order.vo.OrderDetailVO;
import com.heima.order.vo.OrderLogisticsVO;
import com.heima.order.vo.OrderVO;
import com.heima.user.client.UserClient;
import com.heima.user.dto.AddressDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Classname OrderService
 * @Description TODO
 * @Date 2019/9/1 23:04
 * @Created by YJF
 */
@Service
@Slf4j
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
        order.setUserId(userId);
//        4.保存订单状态
        order.setStatus(OrderStatusEnum.INIT.value());
//        将订单保存到数据库
        int count = orderMapper.insertSelective(order);
        if (count != 1) {
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
//        将订单详情保存到数据库
        count = orderDetailMapper.insertList(orderDetails);
        if (count != orderDetails.size() ) {
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }

//        获取地址
        Long addressId = orderDTO.getAddressId();
        AddressDTO addressDTO = userClient.queryAddressById(userId,addressId);
        OrderLogistics orderLogistics = BeanHelper.copyProperties(addressDTO, OrderLogistics.class);
        orderLogistics.setOrderId(orderId);
//        将订单地址保存到数据库中
        count = orderLogisticMapper.insertSelective(orderLogistics);
        if (count != 1) {
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }

//        5.减库存
        itemClient.minusStock(numMap);


        return orderId;
    }

    /**
     * 根据OrderId查询Order
     * @param orderId
     * @return
     */
    public OrderVO queryOrderById(Long orderId) {


//        查询Order数据
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if (order == null) {
            throw new LyException(ExceptionEnum.ORDER_NOT_FOUND);
        }
//        查询OrderDetail数据
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderId(orderId);
        List<OrderDetail> orderDetails = orderDetailMapper.select(orderDetail);
        if (CollectionUtils.isEmpty(orderDetails)) {
            throw new LyException(ExceptionEnum.ORDER_NOT_FOUND);
        }
//        查询OrderLogists数据
        OrderLogistics orderLogistics = new OrderLogistics();
        orderLogistics.setOrderId(orderId);
        OrderLogistics orderLogistics1 = orderLogisticMapper.selectOne(orderLogistics);
        if (orderLogistics1 == null) {
            throw new LyException(ExceptionEnum.ORDER_NOT_FOUND);
        }

        OrderVO orderVO = BeanHelper.copyProperties(order, OrderVO.class);
        List<OrderDetailVO> orderDetailVos = BeanHelper.copyWithCollection(orderDetails, OrderDetailVO.class);
        OrderLogisticsVO orderLogisticsVO = BeanHelper.copyProperties(orderLogistics1, OrderLogisticsVO.class);

        orderVO.setDetailList(orderDetailVos);
        orderVO.setLogistics(orderLogisticsVO);

        return orderVO;
    }

    @Autowired
    private PayHelper payHelper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 根据OrderId获取支付链接
     * @param orderId
     * @return
     */
    public String createPayUrl(Long orderId) {
        String url = payHelper.createOrder(orderId, 1L, "喜提玛莎拉蒂");
        redisTemplate.opsForValue().set(orderId.toString(), url, 2, TimeUnit.HOURS);
        return url;
    }

    /**
     * 更改订单状态
     * @param result
     */
    @Transactional
    public void handleNotify(Map<String, String> result) {

//        1.校验签名
        try {
            payHelper.isValidSign(result);
        } catch (Exception e) {
            log.error("[微信支付]签名校验有误,result:{}", result, e);
            throw new LyException(ExceptionEnum.INVALID_NOTIFY_SIGN, e);
        }
//        2.校验支付业务是否成功
        payHelper.checkResultCode(result);
//        3.校验金额是否一致
        String total_feeStr = result.get("total_fee");
        String orderIdStr = result.get("out_trade_no");
        if (StringUtils.isEmpty(total_feeStr) || StringUtils.isEmpty(orderIdStr)) {
            throw new LyException(ExceptionEnum.INVALID_NOTIFY_PARAM);
        }

//        金额不符,抛异常
        Long total_fee = Long.valueOf(total_feeStr);
        Long orderId = Long.valueOf(orderIdStr);
        if (total_fee != 1L) {
            throw new LyException(ExceptionEnum.INVALID_NOTIFY_PARAM);
        }

//        4.修改订单状态
        Order order = new Order();
        order.setStatus(OrderStatusEnum.PAY_UP.value());
        order.setOrderId(orderId);
        order.setPayTime(new Date());
        int count = orderMapper.updateByPrimaryKeySelective(order);
        if (count != 1) {
            log.error("[微信回调]更新订单状态失败,订单id:{}",orderId);
            throw new LyException(ExceptionEnum.UPDATE_OPERATION_FAIL);
        }
        log.info("[微信回调],订单支付成功!订单编号:{}", orderId);
    }

    /**
     * 根据orderId查询订单状态
     * @param orderId
     * @return
     */
    public Integer queryPayState(Long orderId) {

        Order order = orderMapper.selectByPrimaryKey(orderId);
        if (order == null) {
            throw new LyException(ExceptionEnum.ORDER_NOT_FOUND);
        }

        return order.getStatus();

    }
}
