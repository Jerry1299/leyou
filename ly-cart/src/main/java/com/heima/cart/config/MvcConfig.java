package com.heima.cart.config;

import com.heima.cart.interceptors.UserInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Classname MvcConfig
 * @Description TODO
 * @Date 2019/9/1 16:11
 * @Created by YJF
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    /**
     * 向SpringMVC中添加定义好的Interceptor
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new UserInterceptor()).addPathPatterns("/**");
    }
}
