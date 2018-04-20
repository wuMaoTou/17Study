package com.maotou.appservice.dao;

import com.maotou.appservice.bean.TokenBean;

/**
 * Created by lichun on 18-4-16.
 */
public interface TokenDao {

    /**
     * 登录或注册是写入Token
     * @param tokenBean
     */
    void saveOrUpdateToken(TokenBean tokenBean);

    /**
     * 根据电话获取token
     * @param phone
     */
    TokenBean isTokenAvailable(String phone);

}
