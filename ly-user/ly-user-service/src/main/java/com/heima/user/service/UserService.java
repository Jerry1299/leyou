package com.heima.user.service;

import com.heima.common.constants.ExceptionEnum;
import com.heima.common.constants.MQConstants;
import com.heima.common.exception.LyException;
import com.heima.common.utils.BeanHelper;
import com.heima.common.utils.RegexUtils;
import com.heima.user.entity.User;
import com.heima.user.mapper.UserMapper;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.heima.user.dto.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * @Classname UserService
 * @Description TODO
 * @Date 2019/8/28 22:06
 * @Created by YJF
 */
@Service
@Transactional
public class UserService {

    @Autowired
    private UserMapper userMapper;


    /**
     * 验证用户名或密码是否可用
     * @param data
     * @param type
     * @return
     */
    public Boolean checkUserData(String data, Integer type) {

        User user = new User();

        switch (type) {
            case 1:
                user.setUsername(data);
                break;
            case 2:
                user.setPhone(data);
                break;
            default:
                throw new LyException(ExceptionEnum.INVALID_PARAM_ERROR);
        }

        int count = userMapper.selectCount(user);

        return count == 0;
    }



    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private AmqpTemplate amqpTemplate;

    private static final String KEY_PREFIX ="user:verify:code:";

    /**
     * 生成验证码,保存到redis中,调用短信发送
     * @param phone
     */
    public void getCheckcode(String phone) {
//        1.验证手机号格式
        if (!RegexUtils.isPhone(phone)) {
            throw new LyException(ExceptionEnum.INVALID_PHONE_NUMBER);
        }
//        2.生成随机码
        String random = RandomStringUtils.randomNumeric(6);
//        3.验证码保存到redis
        redisTemplate.opsForValue().set(KEY_PREFIX + phone, random,5, TimeUnit.MINUTES);
//        4.发送短信验证码消息
        HashMap<String, String> msg = new HashMap<>();
        msg.put("phone", phone);
        msg.put("code", random);
        amqpTemplate.convertAndSend(
                MQConstants.Exchange.SMS_EXCHANGE_NAME,
                MQConstants.RoutingKey.VERIFY_CODE_KEY,
                msg);

    }

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * 1.验证短信;2.校验表单数据;3.密码加密;4.保存到数据库
     * @param user
     * @param code
     */
    public void userRegister(User user, String code) {
//        1.验证短信
        if (!StringUtils.equals(code, redisTemplate.opsForValue().get(KEY_PREFIX + user.getPhone()))) {
            throw new LyException(ExceptionEnum.INVALID_VERIFY_CODE);
        }
//        2.校验表单数据
//        3.密码加密
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
//        4.保存到数据库
        userMapper.insertSelective(user);

    }

    /**
     * 根据用户名和密码查询用户信息
     * @param username
     * @param password
     * @return
     */
    public UserDTO queryUserByNameAndPsw(String username, String password) {

//        1.username和password验证

//        2.从数据库中查询User
        User  u= new User();
        u.setUsername(username);
        User user = userMapper.selectOne(u);
        if (user == null) {
            throw new LyException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
        }
        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new LyException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
        }
//        3.将User转为UserDTO
        return BeanHelper.copyProperties(user, UserDTO.class);

    }
}
