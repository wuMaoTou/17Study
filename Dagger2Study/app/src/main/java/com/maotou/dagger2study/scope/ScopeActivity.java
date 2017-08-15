package com.maotou.dagger2study.scope;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import javax.inject.Inject;

/**
 * Created by wuchundu on 17-6-28.
 */

public class ScopeActivity extends Activity {

    @Inject
    User usera;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerScopeActivityComponent.builder().markScopeActivityModule(new MarkScopeActivityModule("猫头",20,"男")).build().Inject(this);

        TextView textView = new TextView(this);
        textView.setText(usera.toString());
        setContentView(textView);
    }
}
