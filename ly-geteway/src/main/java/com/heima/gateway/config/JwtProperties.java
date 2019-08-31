package com.heima.gateway.config;

import com.heima.common.auth.utils.RsaUtils;
import com.heima.common.exception.LyException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.PublicKey;

/**
 * @Classname JwtProperties
 * @Description TODO
 * @Date 2019/8/31 17:01
 * @Created by YJF
 */
@Data
@Slf4j
@ConfigurationProperties(prefix = "ly.jwt")
public class JwtProperties implements InitializingBean {

//    公钥地址
    private String pubKeyPath;

//    公钥
    private PublicKey publicKey;

    private UserTokenProperties user = new UserTokenProperties();

    @Data
    public class UserTokenProperties {
        private String cookieName;
    }

    @Override
    public void afterPropertiesSet() {
        try {
            this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        } catch (Exception e) {
            log.error("初始化公钥失败", e);
            throw new RuntimeException(e);
        }
    }
}
