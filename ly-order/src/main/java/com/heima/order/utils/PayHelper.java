package com.heima.order.utils;

import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConfigImpl;
import com.github.wxpay.sdk.WXPayConstants;
import com.github.wxpay.sdk.WXPayUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @Classname PayHelper
 * @Description TODO
 * @Date 2019/9/3 20:01
 * @Created by YJF
 */
@Slf4j
@Component
public class PayHelper {

    @Autowired
    private WXPay wxPay;

    @Autowired
    private WXPayConfigImpl payConfig;

    /**
     * 创建订单支付url
     * @param orderId
     * @param totalPay
     * @param desc
     * @return
     */
    public String createOrder(Long orderId, Long totalPay, String desc) {

//        创建调用通用支付方法所需要的数据Map
        HashMap<String, String> data = new HashMap<>();
        data.put("body", desc);
        data.put("out_trade_no", orderId.toString());
        data.put("total_fee",totalPay.toString());
        data.put("spbill_create_ip", "127.0.0.1");
        data.put("notify_url", payConfig.getNotifyUrl());
        data.put("trade_type", payConfig.getTradeType());

//        获取支付链接
        Map<String,String> result = null;
        try {
            result = wxPay.unifiedOrder(data);
        } catch (Exception e) {
            log.error("[微信下单]创建预交易订单异常失败",e);
            throw new RuntimeException("微信下单失败", e);
        }

//        检查业务状态
        String result_code = result.get("result_code");
        if ("FAIL".equals(result_code)) {
            log.error("[微信支付]微信支付业务失败,错误码:{},原因:{}",
                    result.get("err_code"),
                    result.get("err_code_des"));
            throw new RuntimeException("[微信支付]微信支付业务失败");
        }

//        下单成功,获取支付链接
        String url = result.get("code_url");
        if (StringUtils.isBlank(url)) {
            throw new RuntimeException("微信下单失败,支付链接为空");
        }

        return url;

    }

    /**
     * 验证微信返回的签名是否正确
     * @param result
     * @throws Exception
     */
    public void isValidSign(Map<String, String> result) throws Exception {
        boolean boo1 = WXPayUtil.isSignatureValid(result, payConfig.getKey(), WXPayConstants.SignType.MD5);
        boolean boo2 = WXPayUtil.isSignatureValid(result, payConfig.getKey(), WXPayConstants.SignType.HMACSHA256);
        if (!boo1 && !boo2) {
            throw new RuntimeException("[微信支付回调]签名有误");
        }
    }

    /**
     * 验证微信返回的支付业务状态码是否正确
     * @param result
     */
    public void checkResultCode(Map<String, String> result) {
        String result_code = result.get("result_code");
        if ("FAIL".equals(result_code)) {
            log.error("[微信支付]微信支付业务失败,错误码:{},原因:{}",result.get("err_code"),result.get("err_code_des"));
            throw new RuntimeException("[微信支付]微信支付业务失败");
        }
    }



}
