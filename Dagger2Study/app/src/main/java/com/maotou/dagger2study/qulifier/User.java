package com.maotou.dagger2study.qulifier;

/**
 * Created by wuchundu on 17-6-28.
 */

public class User {

    private String name;
    private int age;
    private String sex;

    public User(String name, int age, String sex){
        this.age = age;
        this.name = name;
        this.sex = sex;
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
