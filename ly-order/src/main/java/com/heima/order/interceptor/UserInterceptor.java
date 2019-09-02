package com.heima.order.interceptor;

import com.heima.common.auth.dto.Payload;
import com.heima.common.auth.dto.UserInfo;
import com.heima.common.auth.utils.JwtUtils;
import com.heima.common.threadlocals.UserHolder;
import com.heima.common.utils.CookieUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;

/**
 * @Classname UserInterceptor
 * @Description TODO
 * @Date 2019/9/1 22:21
 * @Created by YJF
 */
@Slf4j
public class UserInterceptor implements HandlerInterceptor {

    private static final String  TOKEN_NAME ="LY_TOKEN";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
//        获取token
            String token = CookieUtils.getCookieValue(request, TOKEN_NAME);
//        从token中获取用户信息
            Payload<UserInfo> payload = JwtUtils.getInfoFromToken(token, UserInfo.class);
            UserInfo userInfo = payload.getUserInfo();
            UserHolder.setUser(userInfo);
            return true;
        } catch (UnsupportedEncodingException e) {
            log.error("[订单服务]解析用户信息失败]", e);
            return false;
        }

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserHolder.removeUser();
    }
}
