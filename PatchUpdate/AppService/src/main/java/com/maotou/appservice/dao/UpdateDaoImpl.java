package com.maotou.appservice.dao;

import com.maotou.appservice.bean.UpdateBean;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import java.util.List;

/**
 * Created by lichun on 18-4-16.
 */
public class UpdateDaoImpl extends HibernateDaoSupport implements UpdateDao {

    public UpdateBean getUpdateInfo(String md5value, int versionCode, String channelId) {
        List<UpdateBean> updates = (List<UpdateBean>) getHibernateTemplate().find(
                "from UpdateBean where md5value=? and versionCode=? and channelId=?",
                md5value, versionCode, channelId);

        if (updates != null && updates.size() > 0){
            return updates.get(0);
        }
        return null;
    }

    public void saveUpdateInfo(UpdateBean updateBean) {
        getHibernateTemplate().save(updateBean);
    }

    public void deleteUpdateInfo(UpdateBean updateBean) {
        getHibernateTemplate().delete(updateBean);
    }
}
