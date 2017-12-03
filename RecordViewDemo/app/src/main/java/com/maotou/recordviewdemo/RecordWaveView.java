package com.maotou.recordviewdemo;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by lyzhang3 on 2017/11/28.
 */

public class RecordWaveView extends LinearLayout implements View.OnClickListener{

    private RecordWaveBaseView newDotWave;

    private Context context;


    private ArrayList<Short> dataList = new ArrayList<>();
    private ArrayList<Integer> dataList1;
    private ArrayList dotList;

    public RecordWaveView(Context context) {
        super(context);
        this.context = context;
    }

    public RecordWaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        //加载布局
        LayoutInflater.from(context).inflate(R.layout.layout_record_wave, this);
        newDotWave = (RecordWaveBaseView) findViewById(R.id.newDotWave);

        findViewById(R.id.tv_stop).setOnClickListener(this);
        findViewById(R.id.tv_start).setOnClickListener(this);


    }

    public void setDateList(ArrayList<Short> mRecDataList, ArrayList<Integer> dotList) {

//        if(dataList.size()*dip2px(context,1)>(this.getWidth()-dip2px(context,90))){
//            audioScr.scrollBy(mRecDataList.size()*dip2px(context,1),0);
//        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_start:
                startRecord();
                newDotWave.startView();
                break;
            case R.id.tv_stop:
                stopRecord();
                newDotWave.stopView();
                break;
        }
    }

    private final Handler mHandler = new Handler();

    private Runnable scrollRunnable = new Runnable() {
        @Override
        public void run() {
            dataList1 = new ArrayList<Integer>();
            dataList1.add(getRandom());
            dotList = new ArrayList<Integer>();
            dotList.add(12);
            dotList.add(46);
            newDotWave.setDataList(dataList1, dotList);
            mHandler.postDelayed(this, 50);
        }
    };

    private void startRecord(){
        mHandler.post(scrollRunnable);
    }

    private void stopRecord(){
        mHandler.removeCallbacks(scrollRunnable);
        dataList1.clear();
        dotList.clear();
    }

    private int dip2px(Context context, float dipValue) {
        float fontScale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * fontScale + 0.5f);
    }

    private int getRandom(){
        int max = 80;
        int min = dip2px(context,3.5f);
        Random random = new Random();

        int s = (Integer)(random.nextInt(max)%(max-min+1) + min);
        return s;
    }
}
