package com.heima.common.threadlocals;

import com.heima.common.auth.dto.UserInfo;

/**
 * @Classname UserHolder
 * @Description TODO
 * @Date 2019/9/1 15:38
 * @Created by YJF
 */
public class UserHolder {

    private static final ThreadLocal<UserInfo> TL = new ThreadLocal<>();

    public static void setUser(UserInfo user) {
        TL.set(user);
    }

    public static UserInfo getUser() {
        return TL.get();
    }

    public static void removeUser() {
        TL.remove();
    }

}
