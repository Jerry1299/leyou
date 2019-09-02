package com.heima.order.config;

import com.heima.common.utils.IdWorker;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Classname IdWorkerConfig
 * @Description TODO
 * @Date 2019/9/2 17:29
 * @Created by YJF
 */
@Configuration
@EnableConfigurationProperties(IdWorkerProperties.class)
public class IdWorkerConfig {

    @Bean
    public IdWorker idWorker(IdWorkerProperties prop) {

        return new IdWorker(prop.getWorkerId(), prop.getDataCenterId());

    }

}
