package com.maotou.databindingstudy;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.maotou.databindingstudy.databinding.ActivityBindingmoudleBinding;
import com.maotou.databindingstudy.moudle.User;

public class BindingModuleActivity extends AppCompatActivity {
    private ActivityBindingmoudleBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_bindingmoudle);
        User user = new User();
        user.setName("小吴");
        user.setAge("28");
        mBinding.setUser(user);
        mBinding.tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                User user = new User();
                user.setName("小明");
                user.setAge("29");
                mBinding.setUser(user);
            }
        });
    }
}
