package com.maotou.dagger2study.inject;

import javax.inject.Inject;

/**
 * Created by wuchundu on 17-6-28.
 */

public class User {

    private String name;
    private int age;
    private String sex;

    @Inject
    public User(){
        this.age = 20;
        this.name = "猫头";
        this.sex = "男";
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", sex='" + sex + '\'' +
                '}';
    }
}
