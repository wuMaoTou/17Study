package com.maotou.multiimageuploaddemo;

import java.io.Serializable;

/**
 * Created by wuchundu on 2018/6/29.
 * 任务线程回调接口
 */

public interface OnThreadResultListener<F extends Serializable> {

    void onStart();

    void onProgress(int percent);

    void onFinish(F f);

    void onInterrupted(Exception e);
}
