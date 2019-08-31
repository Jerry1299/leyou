package com.heima.auth.config;

import com.heima.common.auth.utils.RsaUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @Classname JwtProperties
 * @Description TODO
 * @Date 2019/8/29 22:31
 * @Created by YJF
 */
@Data
@Slf4j
@ConfigurationProperties(prefix = "ly.jwt")
public class JwtProperties implements InitializingBean {

    //    公钥地址
    private String pubKeyPath;
    //    私钥地址
    private String priKeyPath;
    //    公钥
    private PublicKey publicKey;
    //    私钥
    private PrivateKey privateKey;

    private UserTokenProperties user = new UserTokenProperties();

    @Data

    public class UserTokenProperties {
        private int expire; //token过期时长
        private String cookieName; //token的cookie名称
        private String cookieDomain; //token的cookie的domain
        private int minRefreshInterval; //cokie的最小刷新时间
    }


    //    将私钥和公钥配置到属性中
    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
            this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
        } catch (Exception e) {
            log.error("初始化公钥和私钥失败", e);
            throw new RuntimeException(e);
        }
    }
}
