package com.heima.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Classname SearchApplication
 * @Description TODO
 * @Date 2019/8/21 23:33
 * @Created by YJF
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.heima.item")
public class LySearch {
    public static void main(String[] args) {
        SpringApplication.run(LySearch.class, args);
    }
}
