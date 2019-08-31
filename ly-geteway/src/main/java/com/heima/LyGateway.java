package com.heima;

import com.heima.gateway.config.FilterProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * @Classname LyGateway
 * @Description TODO
 * @Date 2019/8/13 16:26
 * @Created by YJF
 */
@SpringCloudApplication
@EnableZuulProxy
@EnableConfigurationProperties(FilterProperties.class)
public class LyGateway {
    public static void main(String[] args) {
        SpringApplication.run(LyGateway.class, args);
    }
}
