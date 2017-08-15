package com.maotou.dagger2study.qulifier;

import dagger.Module;
import dagger.Provides;

/**
 * Created by wuchundu on 17-6-29.
 */
@Module
public class MarkQualifierActivityModule {

    private String name;
    private int age;
    private String sex;

    MarkQualifierActivityModule(String name, int age, String sex){
        this.age = age;
        this.name = name;
        this.sex = sex;
    }

    @QualifierA
    @Provides
    User providerUserBeanA(){
        return new User(name+"A",age,sex);
    }

    @QualifierB
    @Provides
    User providerUserBeanB(){
        return new User(name+"B",age+1,sex);
    }

}
