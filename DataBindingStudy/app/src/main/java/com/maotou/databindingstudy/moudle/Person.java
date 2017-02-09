package com.maotou.databindingstudy.moudle;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.maotou.databindingstudy.BR;

/**
 * Created by wuchundu on 17-2-9.
 */

public class Person extends BaseObservable {

    public Person(String name,String age,String gender){
        this.name = name;
        this.age = age;
        this.gender = gender;
    }

    private String name;
    private String age;
    private String gender;

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    @Bindable
    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
        notifyPropertyChanged(BR.age);
    }

    @Bindable
    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
        notifyPropertyChanged(BR.gender);
    }
}
