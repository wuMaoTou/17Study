package com.maotou.appservice.dao;

import com.maotou.appservice.bean.UserBean;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import java.util.List;

/**
 * 操作用户表的类
 * 继承于HibernateDaoSupport,使用hibernate的语句操作数据库
 * Created by lichun on 18-4-12.
 */
public class UserDaoImpl extends HibernateDaoSupport implements UserDao {

    public UserBean register(UserBean userBean) {
        this.getHibernateTemplate().save(userBean);
        return getUser(userBean.getPhone());
    }

    public UserBean login(String phone, String password) {
        List<UserBean> users = (List<UserBean>) this.getHibernateTemplate().find("from UserBean where phone=? and password=?", phone, password);
        if (users != null && users.size() > 0){
            return users.get(0);
        }
        return null;
    }

    public UserBean getUser(String phone) {
        List<UserBean> users = (List<UserBean>) this.getHibernateTemplate().find("from UserBean where phone=?", phone);
        if (users != null && users.size() > 0){
            return users.get(0);
        }
        return null;
    }
}
