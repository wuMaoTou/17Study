package com.maotou.databindingstudy;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.maotou.databindingstudy.databinding.ActivityBindingviewidBinding;

public class BindingViewIdActivity extends AppCompatActivity {
    private ActivityBindingviewidBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_bindingviewid);
        mBinding.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBinding.textView.setText("用了DataBinding,不需要findViewById了");
            }
        });
    }
}
