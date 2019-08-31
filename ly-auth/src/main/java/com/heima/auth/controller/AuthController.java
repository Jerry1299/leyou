package com.heima.auth.controller;

import com.heima.auth.service.AuthService;
import com.heima.common.auth.dto.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Classname AuthController
 * @Description TODO
 * @Date 2019/8/30 16:48
 * @Created by YJF
 */
@RestController
public class AuthController {

    @Autowired
    private AuthService authService;


    /**
     * 登录授权
     * @param username 用户名
     * @param password 密码
     * @return 无
     */
    @PostMapping("login")
    public ResponseEntity<Void> login(@RequestParam("username")String username,
                                      @RequestParam("password")String password,
                                      HttpServletResponse response) {

        authService.login(username, password,response);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

    }

    /**
     * 验证用户信息
     * @param request
     * @param response
     * @return
     */
    @GetMapping("verify")
    public ResponseEntity<UserInfo> verifyUser(HttpServletRequest request,
                                               HttpServletResponse response) {

        return ResponseEntity.ok(authService.verifyUser(request, response));

    }

    /**
     * 退出登录
     * @param request
     * @param response
     * @return
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {

        authService.logout(request, response);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

    }


}
