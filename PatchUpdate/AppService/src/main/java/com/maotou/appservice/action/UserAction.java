package com.maotou.appservice.action;

import com.maotou.appservice.bean.RestFulBean;
import com.maotou.appservice.bean.UserBean;
import com.maotou.appservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * 注册接口:/user/register.do
 * 登录接口:/user/loginbypwd.do
 * Created by lichun on 18-4-12.
 */

@Controller
@RequestMapping("/user")
public class UserAction {

    @Autowired
    private UserService userService;

    @ResponseBody
    @RequestMapping(value="/loginbypwd.do", method= RequestMethod.POST)
    public RestFulBean<UserBean> loginByPwd(@RequestParam String phone, @RequestParam String password){
        return userService.login(phone, password);
    }

    @ResponseBody
    @RequestMapping(value = "/register.do", method = RequestMethod.POST)
    public RestFulBean<UserBean> register(@RequestParam String phone, @RequestParam String password,
                                          @RequestParam String username, @RequestParam int sex,
                                          @RequestParam int age){
        UserBean userBean = new UserBean();
        userBean.setAge(age);
        userBean.setPassword(password);
        userBean.setPhone(phone);
        userBean.setSex(sex);
        userBean.setUsername(username);
        return userService.registerServer(userBean);
    }

    @ResponseBody
    @RequestMapping(value = "/userinfo.do", method = RequestMethod.GET)
    public  RestFulBean<UserBean> userinfo(String phone){
        System.out.println("phone:" + phone);
        return userService.userinfo(phone);
    }

}
