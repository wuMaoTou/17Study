package com.maotou.dagger2study.inject;

import javax.inject.Inject;

/**
 * Created by wuchundu on 17-6-28.
 */

public class ScoreBean {
    private double chinese;
    private double math;

    @Inject
    public ScoreBean() {
        this.chinese = 90.5;
        this.math = 78.0;
    }

    @Override
    public String toString() {
        return "ScoreBean{" +
                "chinese=" + chinese +
                ", math=" + math +
                '}';
    }
}
