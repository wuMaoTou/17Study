package com.maotou.multiimageuploaddemo;

import java.io.Serializable;
import java.util.concurrent.CountDownLatch;

/**
 * Created by wuchundu on 2018/7/6.
 */

public abstract class AbsTask<T, F extends Serializable> implements ITask<T, F> {

    @Override
    public void run(CountDownLatch downLatch, T t, OnThreadResultListener<F> listener) {
        try {
            listener.onStart();
            F f1 = execut(t, listener);
            downLatch.countDown();
            listener.onFinish(f1);
        } catch (Exception e) {
            listener.onInterrupted(e);
        }
    }

    public abstract F execut(T t, OnThreadResultListener<F> listener) throws Exception;
}
