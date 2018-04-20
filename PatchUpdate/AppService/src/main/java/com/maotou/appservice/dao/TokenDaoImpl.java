package com.maotou.appservice.dao;

import com.maotou.appservice.bean.TokenBean;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import java.util.List;

/**
 * Created by lichun on 18-4-16.
 */
public class TokenDaoImpl extends HibernateDaoSupport implements TokenDao {

    public void saveOrUpdateToken(TokenBean tokenBean) {
        this.getHibernateTemplate().saveOrUpdate(tokenBean);
    }

    public TokenBean isTokenAvailable(String phone) {
        List<TokenBean> list = (List<TokenBean>) this.getHibernateTemplate().find("from TokenBean where phone=?", phone);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }
}
