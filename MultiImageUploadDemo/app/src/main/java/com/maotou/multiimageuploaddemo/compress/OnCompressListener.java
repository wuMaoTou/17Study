package com.maotou.multiimageuploaddemo.compress;

/**
 * Created by wuchundu on 2018/7/4.
 * 图片压缩事件监听
 */

public interface OnCompressListener<T> {

    /**
     * 开始压缩
     */
    void onStart();

    /**
     * 压缩完成
     */
    void onSuccess(T t);

    /**
     * 压缩失败
     */
    void onError(Throwable e);
}
