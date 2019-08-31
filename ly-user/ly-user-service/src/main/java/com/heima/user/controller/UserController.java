package com.heima.user.controller;

import com.heima.common.exception.LyException;
import com.heima.user.dto.*;
import com.heima.user.entity.User;
import com.heima.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.stream.Collectors;

/**
 * @Classname UserController
 * @Description TODO
 * @Date 2019/8/28 22:11
 * @Created by YJF
 */
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 校验数据是否可用
     * @param data 要校验的数据
     * @param type 要校验的数据类型
     * @return
     */
    @GetMapping("/check/{data}/{type}")
    public ResponseEntity<Boolean> checkUserData(@PathVariable("data") String data,
                                                 @PathVariable("type") Integer type) {
        return ResponseEntity.ok(userService.checkUserData(data, type));
    }

    /**
     * 生成短信验证码
     * @return
     */
    @PostMapping("/code")
    public ResponseEntity<Void> getCheckcode(@RequestParam("phone")String phone) {
        userService.getCheckcode(phone);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    /**
     * 用户注册
     * @param user
     * @param code
     * @return
     */
    @PostMapping("/register")
    public ResponseEntity<Void> userRegister(@Valid User user, BindingResult result,
                                             @RequestParam("code") String code) {
//        通过BindingResult进行参数校验,自定义错误提醒
        if (result.hasErrors()) {
            String msg = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining("|"));
            throw new LyException(400, msg);
        }

//        参数校验通过,进行注册
        userService.userRegister(user, code);
        return ResponseEntity.status(HttpStatus.CREATED).build();

    }

    /**
     * 根据用户名和密码查询用户json
     * @param username
     * @param password
     * @return
     */
    @GetMapping("/query")
    public ResponseEntity<UserDTO> queryUserByNameAndPwd(@RequestParam("username") String username, @RequestParam("password") String password) {
        return ResponseEntity.ok(userService.queryUserByNameAndPsw(username,password));
    }


}
