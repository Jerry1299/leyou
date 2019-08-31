package com.heima.upload.web;

import com.heima.upload.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.ws.Response;

/**
 * @Classname UploadController
 * @Description TODO
 * @Date 2019/8/17 15:12
 * @Created by YJF
 */
@RestController
@RequestMapping
public class UploadController {

    @Autowired
    UploadService uploadService;

    /**
     * 请求方式:POST
     * 请求路径:/upload/image
     * 请求参数:file MultipartFile
     * 返回值:String
     */
    @PostMapping("image")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {

        return ResponseEntity.ok(uploadService.uploadImage(file));
    }

}
