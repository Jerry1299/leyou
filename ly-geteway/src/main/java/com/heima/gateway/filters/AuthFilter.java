package com.heima.gateway.filters;

import com.heima.common.auth.dto.Payload;
import com.heima.common.auth.dto.UserInfo;
import com.heima.common.auth.utils.JwtUtils;
import com.heima.common.utils.CookieUtils;
import com.heima.gateway.config.FilterProperties;
import com.heima.gateway.config.JwtProperties;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @Classname AuthFilter
 * @Description TODO
 * @Date 2019/8/31 17:16
 * @Created by YJF
 */
@Slf4j
@Component
@EnableConfigurationProperties(JwtProperties.class)
public class AuthFilter extends ZuulFilter {

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private FilterProperties filterProperties;

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return FilterConstants.FORM_BODY_WRAPPER_FILTER_ORDER -1;
    }

    @Override
    public boolean shouldFilter() {
//        获取上下文
        RequestContext currentContext = RequestContext.getCurrentContext();
//        获取request
        HttpServletRequest request = currentContext.getRequest();
//        获取路径
        String requestURI = request.getRequestURI();
//        判断是否在白名单
        for (String path : filterProperties.getAllowPaths()) {
            if (requestURI.startsWith(path)) {
                return false;
            }
        }

        return true;

    }

    @Override
    public Object run() throws ZuulException {


//        获取上下文
            RequestContext currentContext = RequestContext.getCurrentContext();
//        获取request
            HttpServletRequest request = currentContext.getRequest();
//        获取token
            String token = CookieUtils.getCookieValue(request, jwtProperties.getUser().getCookieName());
        try {
//        解析token
            Payload<UserInfo> payload = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey(), UserInfo.class);
//        获取用户角色
            UserInfo userInfo = payload.getUserInfo();
            String role = userInfo.getRole();
//        获取当前资源的路径
            String path = request.getRequestURI();
            String method = request.getMethod();
//        TODO 权限判断

            log.info("[网关]用户{},角色{}.访问服务{}:{},",userInfo.getUsername(),role,method,path);
        } catch (Exception e) {
//            未通过校验时,返回403
            currentContext.setSendZuulResponse(false);
            currentContext.setResponseStatusCode(403);
            log.error("非法访问,未登录,地址:{}", request.getRemoteHost(), e);

        }

        return null;
    }
}
