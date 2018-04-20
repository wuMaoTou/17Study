package com.maotou.appservice.action;

import com.maotou.appservice.bean.RestFulBean;
import com.maotou.appservice.bean.UpdateBean;
import com.maotou.appservice.service.UpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by lichun on 18-4-16.
 */
@Controller
@RequestMapping("/update")
public class UpdateAction {

    @Autowired
    private UpdateService updateService;

    @ResponseBody
    @RequestMapping(value = "/chackupdate.do", method = RequestMethod.GET)
    public RestFulBean<UpdateBean> checkUpdate(String md5value, int versionCode, String channelId){
        System.out.println("md5value:" + md5value);
        return updateService.checkUpdate(md5value, versionCode, channelId);
    }

}
