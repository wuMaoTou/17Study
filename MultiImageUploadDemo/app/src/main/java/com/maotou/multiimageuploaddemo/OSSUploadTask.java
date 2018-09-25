package com.maotou.multiimageuploaddemo;

import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;

import java.io.File;
import java.util.concurrent.CountDownLatch;

/**
 * Created by wuchundu on 2018/7/3.
 */

public class OSSUploadTask extends AbsTask<String,String> {

    @Override
    public String execut(String s, final OnThreadResultListener<String> listener) throws Exception {
        File file = Luban.getInstant().thirdCompress(new File(s));
        LogUtils.d(FileUtils.getFileSize(file));
        String objectKeyPath = Util.getObjectKeyPath(s);
        PutObjectRequest put = new PutObjectRequest("test-chezhency", objectKeyPath, file.getAbsolutePath());
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                listener.onProgress((int) ((double) currentSize / (double) totalSize * 100));
            }
        });
        OSSClientUtil.getInstance().getOSSClient().putObject(put);
        return objectKeyPath;
    }
}
