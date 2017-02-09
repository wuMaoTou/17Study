package com.maotou.databindingstudy;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.maotou.databindingstudy.databinding.ActivityBindingmapBinding;

import java.util.ArrayList;
import java.util.HashMap;

public class BindingMapActivity extends AppCompatActivity {
    private ActivityBindingmapBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_bindingmap);

        ArrayList<String> strings = new ArrayList<>();
        strings.add("List获取方式1");
        strings.add("List获取方式2");
        mBinding.setList(strings);

        HashMap<String, String> stringHashMap = new HashMap<>();
        stringHashMap.put("index","Map获取方式1");
        stringHashMap.put("get","Map获取方式2");
        mBinding.setMap(stringHashMap);

        String[] stringArrays = new String[]{"数组获取方式"};
        mBinding.setArrays(stringArrays);

    }
}
