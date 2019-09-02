package com.heima.user.client;

import com.heima.user.dto.AddressDTO;
import com.heima.user.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Classname UserClient
 * @Description TODO
 * @Date 2019/8/29 23:00
 * @Created by YJF
 */
@FeignClient("user-service")
public interface UserClient {

    /**
     * 根据用户名和密码查询用户json
     * @param username
     * @param password
     * @return
     */
    @GetMapping("/query")
    UserDTO queryUserByNameAndPwd(@RequestParam("username") String username,
                                  @RequestParam("password") String password) ;

    /**
     * 根据
     * @param userId 用户id
     * @param id 地址id
     * @return 地址信息
     */
    @GetMapping("/address")
    AddressDTO queryAddressById(@RequestParam("userId") Long userId, @RequestParam("id") Long id);

}
