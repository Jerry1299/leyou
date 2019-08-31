package com.heima.user.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.SecureRandom;

/**
 * @Classname PasswordConfig
 * @Description TODO
 * @Date 2019/8/29 15:11
 * @Created by YJF
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "ly.encoder.crypt")
public class PasswordConfig {

    private int strength;
    private String secret;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
//        利用秘钥生成随机安全码
        SecureRandom secureRandom = new SecureRandom(secret.getBytes());
//        初始化BCryptPasswordEncoder
        return new BCryptPasswordEncoder(strength, secureRandom);
    }


}
