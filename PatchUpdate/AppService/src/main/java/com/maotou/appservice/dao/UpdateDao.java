package com.maotou.appservice.dao;

import com.maotou.appservice.bean.UpdateBean;

/**
 * Created by lichun on 18-4-16.
 */
public interface UpdateDao {

    /**
     * 获取更新信息
     * @param md5value
     * @param versionCode
     * @param channelId
     * @return
     */
    UpdateBean getUpdateInfo(String md5value, int versionCode, String channelId);

    /**
     * 存储更新信息
     * @param udateBean
     */
    void saveUpdateInfo(UpdateBean udateBean);

    /**
     * 删除更新信息
     * @param updateBean
     */
    void deleteUpdateInfo(UpdateBean updateBean);

}
