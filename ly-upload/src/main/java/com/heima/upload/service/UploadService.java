package com.heima.upload.service;

import com.heima.common.constants.ExceptionEnum;
import com.heima.common.exception.LyException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @Classname UploadService
 * @Description TODO
 * @Date 2019/8/17 15:12
 * @Created by YJF
 */
@Service
public class UploadService {

    //    设置支持上传的文件类型集合
    private static final List<String> TYPES = Arrays.asList("image/png", "image/jpeg", "image/bmp");

    public String uploadImage(MultipartFile file) {

//        1.校验文件类型
        String type = file.getContentType();
        if (!TYPES.contains(type)) {
            throw new LyException(ExceptionEnum.INVALID_FILE_TYPE);
        }
//        2.校验文件内容,异常时处理和空值处理
        BufferedImage image = null;
        try {
            image = ImageIO.read(file.getInputStream());
        } catch (IOException e) {
            throw new LyException(ExceptionEnum.INVALID_FILE_TYPE);
        }

        if (image == null) {
            throw new LyException(ExceptionEnum.INVALID_FILE_TYPE);
        }

//        3.保存图片到本地
        File dir = new File("I:\\develop\\nginx-1.12.2\\html");

        if (!dir.exists()) {
            dir.mkdirs();
        }

        try {
//            调用MutipartFile中的transferTo()方法,进行文件保存
            file.transferTo(new File(dir,file.getOriginalFilename()));
//            返回保存文件的路径
            return "http://image.leyou.com/" + file.getOriginalFilename();
        } catch (IOException e) {
            throw new LyException(ExceptionEnum.FILE_UPLOAD_ERROR);
        }

    }
}
