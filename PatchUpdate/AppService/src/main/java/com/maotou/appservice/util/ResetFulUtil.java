package com.maotou.appservice.util;

import com.maotou.appservice.bean.RestFulBean;

/**
 * Created by lichun on 18-4-12.
 */
public class ResetFulUtil<T> {
    private ResetFulUtil(){}

    public static ResetFulUtil getInstance(){
        return new ResetFulUtil();
    }

    public RestFulBean<T> getRestFulBean(T t, int status, String msg){
        RestFulBean<T> bean = new RestFulBean<T>();
        bean.setStatus(status);
        bean.setMsg(msg);
        bean.setData(t);
        return bean;
    }
}
