package com.maotou.dagger2study.module;

import dagger.Module;
import dagger.Provides;

/**
 * Created by wuchundu on 17-6-29.
 */
@Module
public class MarkModuleActivityModule {


    private String name;
    private int age;
    private String sex;

    MarkModuleActivityModule(String name,int age,String sex){
        this.age = age;
        this.name = name;
        this.sex = sex;
    }

    @Provides
    User providerUserBean(){
        return new User(name,age,sex);
    }

}
