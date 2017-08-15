package com.maotou.dagger2study.scope;

import dagger.Module;
import dagger.Provides;

/**
 * Created by wuchundu on 17-6-29.
 */
@Module
public class MarkScopeActivityModule {

    private String name;
    private int age;
    private String sex;

    MarkScopeActivityModule(String name, int age, String sex){
        this.age = age;
        this.name = name;
        this.sex = sex;
    }

    @UserScope
    @Provides
    User providerUserBean(){
        return new User(name,age,sex);
    }
}
