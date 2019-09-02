package com.heima.gateway.config;

import com.sun.org.apache.xml.internal.security.Init;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @Classname FilterProperties
 * @Description TODO
 * @Date 2019/8/31 20:32
 * @Created by YJF
 */
@ConfigurationProperties(prefix = "ly.filter")
@Data
public class FilterProperties {

    private List<String> allowPaths;

}
