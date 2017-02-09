package com.maotou.databindingstudy;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.maotou.databindingstudy.databinding.ActivityBindingdataBinding;

public class BindingDataActivity extends AppCompatActivity {
    private ActivityBindingdataBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_bindingdata);
        mBinding.setEnabled(true);
        mBinding.setText("用DataBinding绑定数据;enabled:"+mBinding.getEnabled());
        mBinding.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBinding.getEnabled()){
                    mBinding.setEnabled(false);
                }else{
                    mBinding.setEnabled(true);
                }
                mBinding.setText(mBinding.getText()+";\r\n点击后enabled:"+mBinding.getEnabled());

            }
        });
    }
}
