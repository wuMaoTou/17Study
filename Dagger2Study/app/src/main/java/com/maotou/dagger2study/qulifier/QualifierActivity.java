package com.maotou.dagger2study.qulifier;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import javax.inject.Inject;

/**
 * Created by wuchundu on 17-6-28.
 */

public class QualifierActivity extends Activity {

    @QualifierA
    @Inject
    User usera;

    @QualifierB
    @Inject
    User userb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerQualifierActivityComponent.builder().markQualifierActivityModule(new MarkQualifierActivityModule("猫头",20,"男")).build().Inject(this);

        TextView textView = new TextView(this);
        textView.setText(usera.toString()+"\n"+userb.toString());
        setContentView(textView);
    }
}
