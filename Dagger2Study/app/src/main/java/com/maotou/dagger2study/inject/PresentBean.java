package com.maotou.dagger2study.inject;

import javax.inject.Inject;

/**
 * Created by wuchundu on 17-6-28.
 */

public class PresentBean {

    private String name;
    private int age;

    @Inject
    public AreaBean area;

    @Inject
    public ScoreBean score;

    @Inject
    public PresentBean(){
        this.name = "猫头2";
        this.age = 22;
    }

    @Override
    public String toString() {
        return "PresentBean{" +
                "name='" + name + '\'' +
                ", age=" + age +
                "," + area.toString() +
                "," + score.toString() +
                '}';
    }
}
