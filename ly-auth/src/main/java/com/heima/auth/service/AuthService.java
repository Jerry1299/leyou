package com.heima.auth.service;

import com.heima.auth.config.JwtProperties;
import com.heima.common.auth.dto.Payload;
import com.heima.common.auth.dto.UserInfo;
import com.heima.common.auth.utils.JwtUtils;
import com.heima.common.constants.ExceptionEnum;
import com.heima.common.exception.LyException;
import com.heima.common.utils.CookieUtils;
import com.heima.user.client.UserClient;
import com.heima.user.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @Classname AuthService
 * @Description TODO
 * @Date 2019/8/30 16:48
 * @Created by YJF
 */
@Slf4j
@Service
public class AuthService {

    @Autowired
    private JwtProperties prop;

    @Autowired
    private UserClient userClient;

    private static final String USER_ROLE = "role_user";

    /**
     * 登录授权
     * @param username
     * @param password
     */
    public void login(String username, String password, HttpServletResponse response) {
        try {
//        1.查询用户
            UserDTO userDto = userClient.queryUserByNameAndPwd(username, password);
            UserInfo userInfo = new UserInfo(userDto.getId(),
                    userDto.getUsername(),
                    USER_ROLE);
//        2.生成token
            String token = JwtUtils.generateTokenExpireInMinutes(userInfo,
                    prop.getPrivateKey(),
                    prop.getUser().getExpire());
//        3.写入cookie
            CookieUtils.newBuilder()
                    .response(response)
                    .httpOnly(true)
                    .domain(prop.getUser().getCookieDomain())
                    .name(prop.getUser().getCookieName())
                    .value(token)
                    .build();
        } catch (Exception e) {
            throw new LyException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
        }

    }

    /**
     * 验证用户信息
     * @param request
     * @param response
     * @return
     */
    public UserInfo verifyUser(HttpServletRequest request, HttpServletResponse response) {

        try {
//        读取token
            String token = CookieUtils.getCookieValue(request, prop.getUser().getCookieName());
//        获取token信息
            Payload<UserInfo> payLoad = JwtUtils.getInfoFromToken(token, prop.getPublicKey(), UserInfo.class);
//            获取token的id,校验黑名单
            String id = payLoad.getId();
            Boolean hasId = redisTemplate.hasKey(id);
            if (hasId != null && hasId) {
//                若id在redis中存在,则抛异常
                throw new LyException(ExceptionEnum.UNAUTHORIZED);
            }
//            获过期时间取
            Date expiration = payLoad.getExpiration();
//            获取刷新时间
            DateTime refreshTime = new DateTime(expiration.getTime()).plusMinutes(prop.getUser().getMinRefreshInterval());
//            判断是否过了刷新时间
            if (refreshTime.isBefore(System.currentTimeMillis())) {
//                过了刷新时间,重新生成token,并存入cokie
                token = JwtUtils.generateTokenExpireInMinutes(payLoad.getUserInfo(), prop.getPrivateKey(), prop.getUser().getExpire());
                CookieUtils.newBuilder()
                        .response(response)
                        .httpOnly(true)
                        .domain(prop.getUser().getCookieDomain())
                        .name(prop.getUser().getCookieName())
                        .build();
            }

            return payLoad.getUserInfo();
        } catch (Exception e) {
//            登录失效,401
            log.error("用户信息认证失败",e);
            throw new LyException(ExceptionEnum.UNAUTHORIZED);

        }

    }


    @Autowired
    private StringRedisTemplate redisTemplate;
    /**
     * 退出登录
     * @param request
     * @param response
     */
    public void logout(HttpServletRequest request, HttpServletResponse response) {

//        1.获取token
        String token = CookieUtils.getCookieValue(request, prop.getUser().getCookieName());
//        2.解析token
        Payload<Object> payload = JwtUtils.getInfoFromToken(token, prop.getPublicKey());
//        3.获取id和有效时长
        String id = payload.getId();
        long time = payload.getExpiration().getTime()-System.currentTimeMillis();
//        4.剩余时间超过5秒的写入redis
        if (time > 5000) {
            redisTemplate.opsForValue().set(id,"",time, TimeUnit.MILLISECONDS);
        }
//        5.删除cookie
        CookieUtils.deleteCookie(prop.getUser().getCookieName(),prop.getUser().getCookieDomain(),response);

    }
}
