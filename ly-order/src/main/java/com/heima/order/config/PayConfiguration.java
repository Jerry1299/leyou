package com.heima.order.config;

import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConfigImpl;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Classname PayConfiguration
 * @Description TODO
 * @Date 2019/9/3 19:55
 * @Created by YJF
 */
@Configuration
public class PayConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "ly.pay.wx")
    public WXPayConfigImpl payConfig() {
        return new WXPayConfigImpl();
    }

    @Bean
    public WXPay wxPay(WXPayConfigImpl payConfig) throws Exception {
        return new WXPay(payConfig);
    }


}
