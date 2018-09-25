package com.maotou.multiimageuploaddemo;

import android.os.Bundle;

import java.io.Serializable;
import java.util.concurrent.CountDownLatch;

/**
 * Created by wuchundu on 2018/7/3.
 */

public interface ITask<T,F extends Serializable> {
    void run(CountDownLatch downLatch, T t, OnThreadResultListener<F> listener);
}
