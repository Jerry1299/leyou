package com.heima.sms.config;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.profile.DefaultProfile;
import javafx.beans.DefaultProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Classname SmsConfiguration
 * @Description TODO
 * @Date 2019/8/28 19:55
 * @Created by YJF
 */

@Configuration
@EnableConfigurationProperties(SmsProperties.class)
public class SmsConfiguration {

    /**
     * 根据配置文件创建IAcsClient对象
     * @param smsProperties
     * @return
     */
    @Bean
    public IAcsClient acsClient(SmsProperties smsProperties) {
        DefaultProfile profile = DefaultProfile.getProfile(
                smsProperties.getRegionID(),
                smsProperties.getAccessKeyID(),
                smsProperties.getAccessKeySecret());

        return new DefaultAcsClient(profile);
    }


}
