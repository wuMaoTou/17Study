package com.maotou.dagger2study.Lazy;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import javax.inject.Inject;

import dagger.Lazy;

/**
 * Created by wuchundu on 17-6-28.
 */

public class LazyActivity extends Activity {

    @Inject
    Lazy<User> lUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerLazyActivityComponent.builder().build().Inject(this);

        TextView textView = new TextView(this);
        textView.setText("usera:"+lUser.get().toString()+"\n userb:" + lUser.get().toString());
        setContentView(textView);

    }

}
