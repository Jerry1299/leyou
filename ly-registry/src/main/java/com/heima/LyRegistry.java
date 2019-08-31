package com.heima;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @Classname LyRegistry
 * @Description TODO
 * @Date 2019/8/13 16:09
 * @Created by YJF
 */
@SpringBootApplication
@EnableEurekaServer
public class LyRegistry {

    public static void main(String[] args) {
        SpringApplication.run(LyRegistry.class, args);
    }
}
