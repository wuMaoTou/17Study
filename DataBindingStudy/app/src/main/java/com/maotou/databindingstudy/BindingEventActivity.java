package com.maotou.databindingstudy;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.maotou.databindingstudy.databinding.ActivityBindingeventBinding;
import com.maotou.databindingstudy.moudle.EventListener;

public class BindingEventActivity extends AppCompatActivity {
    private ActivityBindingeventBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_bindingevent);
        mBinding.setStr("事件绑定3");
        mBinding.setEvent(new EventListener() {
            @Override
            public void onClick1(View v) {
                Toast.makeText(BindingEventActivity.this,"事件绑定1",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onClick2(View v) {
                Toast.makeText(BindingEventActivity.this,v.getId()+"事件绑定2",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onClick3(View v,String str) {
                Toast.makeText(BindingEventActivity.this,v.getId()+str,Toast.LENGTH_LONG).show();
            }
        });
    }
}
