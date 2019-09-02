package com.heima.cart.interceptors;

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
 * @Date 2019/9/1 15:48
 * @Created by YJF
 */
@Slf4j
public class UserInterceptor implements HandlerInterceptor {

    private static final String COOKIE_NAME = "LY_TOKEN";

    /**
     * 请求进来时进行拦截,将用户信息保存到ThreadLocal中
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        try {
//        获取cookie中的token
            String token = CookieUtils.getCookieValue(request, COOKIE_NAME);
//        解析token中的用户信息
            Payload<UserInfo> payload = JwtUtils.getInfoFromToken(token, UserInfo.class);
//        将用户信息保存到ThreadLocal中
            UserHolder.setUser(payload.getUserInfo());
            return true;
        } catch (UnsupportedEncodingException e) {
            log.error("[购物车服务]解析用户信息失败", e);
            return false;
        }
    }

    /**
     * 请求在向前端返回视图之前,删除ThreadLocal中的用户信息
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex){
        UserHolder.removeUser();
    }
}
