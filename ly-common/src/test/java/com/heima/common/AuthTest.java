package com.heima.common;

import com.heima.common.auth.dto.Payload;
import com.heima.common.auth.dto.UserInfo;
import com.heima.common.auth.utils.JwtUtils;
import com.heima.common.auth.utils.RsaUtils;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;

/**
 * @Classname AuthTest
 * @Description TODO
 * @Date 2019/8/29 21:07
 * @Created by YJF
 */
public class AuthTest {

    private String privateFilePath = "D:\\develop\\ssh\\id_rsa";
    private String publicFilePath = "D:\\develop\\ssh\\id_rsa.pub";

    /**
     * 生成密钥
     * @throws Exception
     */
    @Test
    public void testRSA() throws Exception {

//        生成密钥对
        RsaUtils.generateKey(publicFilePath,privateFilePath,"hello",2048);

//        获取私钥
        PrivateKey privateKey = RsaUtils.getPrivateKey(privateFilePath);
        System.out.println("privateKey = " + privateKey);
//        获取公钥
        PublicKey publicKey = RsaUtils.getPublicKey(publicFilePath);
        System.out.println("publicKey = " + publicKey);
    }

    /**
     * 解析密钥
     */
    @Test
    public void testJWT() throws Exception {
//        获取私钥
        PrivateKey privateKey = RsaUtils.getPrivateKey(privateFilePath);
//        生成token
        String token = JwtUtils.generateTokenExpireInMinutes(
                new UserInfo(1L, "jack", "guest"),
                privateKey, 5);
//        获取公钥
        PublicKey publicKey = RsaUtils.getPublicKey(publicFilePath);
//        解析token
        Payload<UserInfo> info = JwtUtils.getInfoFromToken(token, publicKey,UserInfo.class);
        Date expiration = info.getExpiration();
        System.out.println("expiration = " + expiration);
        UserInfo userInfo = info.getUserInfo();
        System.out.println("userInfo = " + userInfo);

    }

}
