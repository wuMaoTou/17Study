package com.maotou.dagger2study.module;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import javax.inject.Inject;

/**
 * Created by wuchundu on 17-6-28.
 */

public class ModuleActivity extends Activity {

    @Inject
    User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerModuleActivityComponent.builder().markModuleActivityModule(new MarkModuleActivityModule("猫头",20,"男")).build().Inject(this);

//        MarkScopeActivityModule module = new MarkScopeActivityModule("猫头",20,"男");
//        MarkModuleActivityModule_ProviderUserBeanFactory providerAreaBeanFactory = new MarkModuleActivityModule_ProviderUserBeanFactory(module);
//        ModuleActivity_MembersInjector moduleActivity_membersInjector = new ModuleActivity_MembersInjector(providerAreaBeanFactory);
//        moduleActivity_membersInjector.injectMembers(this);

        TextView textView = new TextView(this);
        textView.setText(user.toString());
        setContentView(textView);
    }
}
