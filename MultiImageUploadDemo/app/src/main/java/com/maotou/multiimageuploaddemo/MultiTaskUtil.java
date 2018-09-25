package com.maotou.multiimageuploaddemo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by wuchundu on 2018/6/29.
 * 多任务处理工具
 */

public class MultiTaskUtil<F extends Serializable> {
    private final static int THREAD_PROGRESS_CODE = 100;//线程进度回调
    private final static int THREAD_FINISH_CODE = 101;//线程完成
    private final static int THREAD_INTERRUPT_CODE = 102;//线程被中断
    private final static int THREAD_ALL_SUCCESS_CODE = 103;//所有线程完成
    private final static int THREAD_ALL_FAILED_CODE = 104;//所有线程执行失败
    private final static int THREAD_START_CODE = 105;//线程开始
    private final static String THREAD_PERCENT = "THREAD_PERCENT";
    private final static String THREAD_POSITION = "THREAD_POSITION";
    private final static String THREAD_DATA = "THREAD_DATA";
    private final static String THREAD_EXCEPTION = "THREAD_EXCEPTION";

    private int threadCount = 0;//任务数量
    private int threadCore = 2;//线程池核心数

    private ExecutorService executor;//线程池
    private CountDownLatch downLatch;//计数器

    private OnUploadListener uploadListener;
    private UploadHandler<F> handler;
    private ITask mTask;

    public MultiTaskUtil(ITask task) {
        this.mTask = task;
        init();
    }

    public MultiTaskUtil(int threadCore, ITask task) {
        this.threadCore = threadCore;
        this.mTask = task;
        init();
    }

    public void setOnUploadListener(OnUploadListener<F> uploadListener) {
        this.uploadListener = uploadListener;
    }

    public void setUploadTask(ITask task) {
        this.mTask = task;
    }

    public void init() {
        handler = new UploadHandler<F>(this);
        executor = Executors.newFixedThreadPool(threadCore + 1);
    }

    /**
     * 中断所有线程
     */
    public void shutDownNow() {
        executor.shutdownNow();
    }

    /**
     * 提交所有任务
     *
     * @param fileName
     */
    public void submitAll(final List<String> fileName) {
        threadCount = fileName.size();
        downLatch = new CountDownLatch(threadCount);

        executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    downLatch.await();
                    handler.sendEmptyMessage(THREAD_ALL_SUCCESS_CODE);
                } catch (InterruptedException e) {
                    handler.sendEmptyMessage(THREAD_ALL_FAILED_CODE);
                }
            }
        });

        for (int i = 0; i < threadCount; i++) {
            final Bundle bundle = new Bundle();
            bundle.putInt(THREAD_POSITION, i);
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    mTask.run(downLatch, fileName.get(bundle.getInt(THREAD_POSITION)), new OnThreadResultListener<F>() {
                        @Override
                        public void onStart() {
                            Message.obtain(handler, THREAD_START_CODE, bundle).sendToTarget();
                        }

                        @Override
                        public void onProgress(int percent) {
                            bundle.putInt(THREAD_PERCENT, percent);
                            Message.obtain(handler, THREAD_PROGRESS_CODE, bundle).sendToTarget();
                        }

                        @Override
                        public void onFinish(F f) {
                            bundle.putSerializable(THREAD_DATA, f);
                            Message.obtain(handler, THREAD_FINISH_CODE, bundle).sendToTarget();
                        }

                        @Override
                        public void onInterrupted(Exception e) {
                            bundle.putSerializable(THREAD_EXCEPTION, e);
                            Message.obtain(handler, THREAD_INTERRUPT_CODE, bundle).sendToTarget();
                        }
                    });
                }
            });
        }
        executor.shutdown();//关闭线程池
    }

    private static class UploadHandler<F extends Serializable> extends Handler {
        private WeakReference<MultiTaskUtil> weakReference;

        private UploadHandler(MultiTaskUtil object) {
            super(Looper.getMainLooper());
            weakReference = new WeakReference<>(object);
        }

        @Override
        public void handleMessage(Message msg) {
            MultiTaskUtil uploadUtil = weakReference.get();
            if (uploadUtil != null) {
                Bundle data = (Bundle) msg.obj;
                int position;
                int percent;
                F f;
                Exception e;

                switch (msg.what) {
                    case THREAD_PROGRESS_CODE:
                        position = data.getInt(THREAD_POSITION);
                        percent = data.getInt(THREAD_PERCENT);
                        uploadUtil.uploadListener.onThreadProgress(percent, position);
                        break;
                    case THREAD_START_CODE:
                        position = data.getInt(THREAD_POSITION);
                        uploadUtil.uploadListener.onThreadStart(position);
                        break;
                    case THREAD_FINISH_CODE:
                        position = data.getInt(THREAD_POSITION);
                        f = (F) data.getSerializable(THREAD_DATA);
                        uploadUtil.uploadListener.onThreadFinish(position, f);
                        break;
                    case THREAD_INTERRUPT_CODE:
                        position = data.getInt(THREAD_POSITION);
                        e = (Exception) data.getSerializable(THREAD_EXCEPTION);
                        uploadUtil.uploadListener.onThreadInterrupted(e,position);
                        break;
                    case THREAD_ALL_SUCCESS_CODE:
                        uploadUtil.uploadListener.onAllSuccess();
                        break;
                    case THREAD_ALL_FAILED_CODE:
                        uploadUtil.uploadListener.onAllFailed();
                        break;
                }
            }
        }
    }
}
