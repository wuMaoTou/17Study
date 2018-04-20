package com.maotou.appservice.dao;

import com.maotou.appservice.bean.UserBean;

/**
 * Created by lichun on 18-4-12.
 */
public interface UserDao {

    /**
     * 注册
     * @param userBean
     * @return
     */
    UserBean register(UserBean userBean);

    /**
     * 登录
     * @param phone
     * @param password
     * @return
     */
    UserBean login(String phone, String password);

    /**
     * 根据名字获取用户信息
     * @param phone
     * @return
     */
    UserBean getUser(String phone);

}
