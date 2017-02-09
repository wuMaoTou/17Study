package com.maotou.databindingstudy;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.maotou.databindingstudy.databinding.ActivityBindingmoudleobserverBinding;
import com.maotou.databindingstudy.moudle.Person;

public class BindingModuleObserverActivity extends AppCompatActivity {
    private ActivityBindingmoudleobserverBinding mBinding;
    private Person person;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_bindingmoudleobserver);
        person = new Person("小吴", "28", "男");
        mBinding.setPerson(person);
        mBinding.tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                person.setName("小黄");
                person.setAge("27");
                person.setGender("女");
            }
        });
    }
}
