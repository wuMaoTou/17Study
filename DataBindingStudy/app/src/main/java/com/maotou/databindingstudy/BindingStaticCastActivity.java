package com.maotou.databindingstudy;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.maotou.databindingstudy.databinding.ActivityBindingstaticcastBinding;
import com.maotou.databindingstudy.moudle.User;

public class BindingStaticCastActivity extends AppCompatActivity {
    private ActivityBindingstaticcastBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_bindingstaticcast);
        User user = new User();
        user.setName("test");
        mBinding.setUser(user);
    }
}
