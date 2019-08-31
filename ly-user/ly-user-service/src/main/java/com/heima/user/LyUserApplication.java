package com.heima.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @Classname LyUserApplication
 * @Description TODO
 * @Date 2019/8/28 21:32
 * @Created by YJF
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.heima.user.mapper")
public class LyUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(LyUserApplication.class, args);
    }
}
