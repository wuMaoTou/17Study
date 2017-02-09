package com.maotou.databindingstudy;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.maotou.databindingstudy.databinding.ActivityBindingincludeBinding;


public class BindingIncludeActivity extends AppCompatActivity {
    private ActivityBindingincludeBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_bindinginclude);
        mBinding.setText("测试include传递参数");
    }
}
