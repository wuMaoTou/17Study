package com.maotou.multiimageuploaddemo;

import java.io.Serializable;

/**
 * Created by wuchundu on 2018/6/29.
 * 主线程回调接口
 */

public interface OnUploadListener<F extends Serializable> {
    void onAllSuccess();

    void onAllFailed();

    void onThreadStart(int position);

    void onThreadProgress(int percent, int position);

    void onThreadFinish(int position, F f);

    void onThreadInterrupted(Exception e, int position);
}
