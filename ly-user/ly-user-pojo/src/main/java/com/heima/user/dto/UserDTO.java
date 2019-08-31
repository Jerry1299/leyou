package com.heima.user.dto;

import lombok.Data;

import java.util.Date;

/**
 * @Classname UserDTO
 * @Description TODO
 * @Date 2019/8/29 0:56
 * @Created by YJF
 */
@Data
public class UserDTO {
    private Long id;
    private String username;
    private String phone;
    private Date createTime;
}
