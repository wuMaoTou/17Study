package com.maotou.appservice.service;

import com.maotou.appservice.bean.RestFulBean;
import com.maotou.appservice.bean.TokenBean;
import com.maotou.appservice.bean.UserBean;
import com.maotou.appservice.dao.TokenDao;
import com.maotou.appservice.dao.UserDao;
import com.maotou.appservice.util.MD5;
import com.maotou.appservice.util.ResetFulUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户操作服务类
 * 注解@Transactional和@Autowired来自动扫描service和注入实体类
 * Created by lichun on 18-4-12.
 */
@Transactional
public class UserService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private TokenDao tokenDao;

    public RestFulBean<UserBean> registerServer(UserBean userBean) {
        UserBean user = userDao.getUser(userBean.getPhone());
        if (user != null) {
            return ResetFulUtil.getInstance().getRestFulBean(null, 1, "该手机号码已注册过了");
        } else {
            user = userDao.register(userBean);
            if (user != null) {
                saveOrUpdateToken(user);
                return ResetFulUtil.getInstance().getRestFulBean(user, 0, "注册成功");
            } else {
                return ResetFulUtil.getInstance().getRestFulBean(null, 1, "注册失败");
            }
        }
    }

    public RestFulBean<UserBean> login(String phone, String password) {
        UserBean user = userDao.login(phone, password);
        if (user != null) {
            saveOrUpdateToken(user);
            return ResetFulUtil.getInstance().getRestFulBean(user, 0, "登录成功");
        } else {
            return ResetFulUtil.getInstance().getRestFulBean(null, 1, "用户不存在");
        }
    }

    public RestFulBean<UserBean> userinfo(String phone) {
        UserBean userBean = userDao.getUser(phone);
        if (userBean != null) {
            return ResetFulUtil.getInstance().getRestFulBean(userBean, 0, "获取成功");
        } else {
            return ResetFulUtil.getInstance().getRestFulBean(null,1,"用户不存在");
        }
    }

    private void saveOrUpdateToken(UserBean userBean){
        String token = null;

        try{
            token = MD5.encryptMD5(String.valueOf(System.currentTimeMillis() + "appservice.02154778ke783dad34"));
        }catch (Exception e){
            e.printStackTrace();
        }

        userBean.setToken(token);
        TokenBean tokenBean = tokenDao.isTokenAvailable(userBean.getPhone());
        if (tokenBean != null){
            tokenBean.setToken(token);
        }else{
            tokenBean = new TokenBean();
            tokenBean.setToken(token);
            tokenBean.setPhone(userBean.getPhone());
        }
        tokenDao.saveOrUpdateToken(tokenBean);
    }

}
