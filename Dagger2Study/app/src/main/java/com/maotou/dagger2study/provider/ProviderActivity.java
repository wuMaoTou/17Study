package com.maotou.dagger2study.provider;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;


import javax.inject.Inject;
import javax.inject.Provider;

/**
 * Created by wuchundu on 17-6-28.
 */

public class ProviderActivity extends Activity {

    @Inject
    Provider<User> pUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerProviderActivityComponent.builder().build().Inject(this);

        TextView textView = new TextView(this);
        textView.setText("usera:"+pUser.get().toString()+"\n userb:" + pUser.get().toString());
        setContentView(textView);

    }

}
