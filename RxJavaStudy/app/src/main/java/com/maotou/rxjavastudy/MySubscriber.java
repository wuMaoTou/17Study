package com.maotou.rxjavastudy;

import android.util.Log;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * Created by Administrator on 2017/2/23.
 */

public class MySubscriber implements Subscriber {

    public static final String TAG = "RxJava";

    @Override
    public void onSubscribe(Subscription s) {
        s.request(Integer.MAX_VALUE);
    }

    @Override
    public void onNext(Object o) {
        Log.d(TAG,"onNext:" + o);
    }

    @Override
    public void onError(Throwable t) {
        Log.d(TAG,"onError" + t);
    }

    @Override
    public void onComplete() {
        Log.d(TAG,"onComplete");
    }
}
