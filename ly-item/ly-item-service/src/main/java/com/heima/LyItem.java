package com.heima;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @Classname LyItem
 * @Description TODO
 * @Date 2019/8/13 16:52
 * @Created by YJF
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.heima.item.mapper")  //扫描Mapper包
public class LyItem {
    public static void main(String[] args) {
        SpringApplication.run(LyItem.class, args);
    }
}
