package com.heima.upload.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.heima.common.constants.ExceptionEnum;
import com.heima.common.exception.LyException;
import com.heima.upload.entity.OSSProperties;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Classname AliyunUploadService
 * @Description TODO
 * @Date 2019/8/17 20:24
 * @Created by YJF
 */
@Service
public class AliyunUploadService {

    @Autowired
    OSSProperties ossProperties;


    public Map<String, Object> getSignature() {

        String endpoint = ossProperties.getEndpoint();
        String accessId = ossProperties.getAccessKeyId();
        String accessKey = ossProperties.getAccessKeySecret();
        String dir = ossProperties.getDir();
        String host = ossProperties.getHost();


//        OSSClient client = new OSSClient(endpoint, accessId, accessKey);
        OSS client = new OSSClientBuilder().build(endpoint, accessId, accessKey);

        try {
            long expireTime = 30;
            long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
            Date expiration = new Date(expireEndTime);
            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);

            String postPolicy = client.generatePostPolicy(expiration, policyConds);
            byte[] binaryData = postPolicy.getBytes("utf-8");
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = client.calculatePostSignature(postPolicy);

            Map<String, Object> respMap = new LinkedHashMap<>();
            respMap.put("accessId", accessId);
            respMap.put("policy", encodedPolicy);
            respMap.put("signature", postSignature);
            respMap.put("dir", dir);
            respMap.put("host", host);
            respMap.put("expire", String.valueOf(expireEndTime));

            return  respMap;

        } catch (Exception e) {
            throw new LyException(ExceptionEnum.UPDATE_OPERATION_FAIL);
        }

    }
}
