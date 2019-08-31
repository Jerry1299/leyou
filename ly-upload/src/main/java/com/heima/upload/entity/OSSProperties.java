package com.heima.upload.entity;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Classname OSSProperties
 * @Description TODO
 * @Date 2019/8/17 20:19
 * @Created by YJF
 */
@Data
@Component
@ConfigurationProperties("ly.oss")
public class OSSProperties {
    private String accessKeyId;
    private String accessKeySecret;
    private String bucket;
    private String host;
    private String endpoint;
    private String dir;
    private long expireTime;
    private long maxFileSize;
}
