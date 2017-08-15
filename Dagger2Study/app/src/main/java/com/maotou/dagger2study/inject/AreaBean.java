package com.maotou.dagger2study.inject;

import javax.inject.Inject;

/**
 * Created by wuchundu on 17-6-28.
 */

public class AreaBean {

    private String provice;
    private String city;

    @Inject
    public AreaBean() {
        this.provice = "四川";
        this.city = "成都";
    }

    @Override
    public String toString() {
        return "AreaBean{" +
                "provice='" + provice + '\'' +
                ", city='" + city + '\'' +
                '}';
    }
}
