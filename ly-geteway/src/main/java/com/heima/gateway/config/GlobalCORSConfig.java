package com.heima.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * @Classname GlobalCORSConfig
 * @Description TODO
 * @Date 2019/8/14 21:15
 * @Created by YJF
 */
@Configuration
public class GlobalCORSConfig {
    @Bean
    public CorsFilter corsFilter() {
//        创建CORS配置对象
        CorsConfiguration config = new CorsConfiguration();
        //        添加允许的域
        config.addAllowedOrigin("http://manage.leyou.com");
        config.addAllowedOrigin("http://www.leyou.com");
//        config.addAllowedOrigin("http://api.leyou.com");
    //        是否发送Cookie信息
        config.setAllowCredentials(true);
    //        允许的请求方式
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("PUT");
    //        允许的头信息
        config.addAllowedHeader("*");
//        添加映射路径
        UrlBasedCorsConfigurationSource configSource = new UrlBasedCorsConfigurationSource();
        configSource.registerCorsConfiguration("/**",config);

//        返回新的CorsFilter
        return new CorsFilter(configSource);
    }
}
