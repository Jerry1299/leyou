package com.heima.upload.web;

import com.aliyun.oss.OSSClient;
import com.heima.upload.service.AliyunUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @Classname AliyunUploadService
 * @Description TODO
 * @Date 2019/8/17 20:25
 * @Created by YJF
 */
@RestController
@RequestMapping
public class AliyunUploadController {

    @Autowired
    AliyunUploadService aliyunUploadService;


    /**
     * 文件上传阿里云,前端获取标签
     * 请求类型:get
     * 请求路径:upload/signature
     * 请求参数:无
     * 返回值:String
     */
    @GetMapping("signature")
    public ResponseEntity<Map<String, Object>> getSignature() {
        return ResponseEntity.ok(aliyunUploadService.getSignature());
    }

}
