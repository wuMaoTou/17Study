package com.maotou.multiimageuploaddemo;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.OSS;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.maotou.multiimageuploaddemo.compress.*;
import com.maotou.multiimageuploaddemo.compress.OnCompressListener;
import com.maotou.multiimageuploaddemo.databinding.ActivityMainBinding;
import com.maotou.multiimageuploaddemo.databinding.ItemUriBinding;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityMainBinding binding;
    private static final int REQUEST_CODE_CHOOSE = 23;

    private UriAdapter adapter;
    private OSS oss;
    private static long startTime = 0;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.zhihu.setOnClickListener(this);
        binding.dracula.setOnClickListener(this);

        binding.recyclerview.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerview.setAdapter(adapter = new UriAdapter());
    }

    @Override
    public void onClick(final View v) {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            switch (v.getId()) {
                                case R.id.zhihu:
                                    uploadImage();
                                    break;
                                case R.id.dracula:
                                    Matisse.from(MainActivity.this)
                                            .choose(MimeType.ofImage(),true)
                                            .showSingleMediaType(true)
                                            .maxSelectable(9)
                                            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                                            .thumbnailScale(0.85f)
                                            .imageEngine(new Glide4Engine())
                                            .forResult(REQUEST_CODE_CHOOSE);
                                    break;
                            }
                        } else {
                            Toast.makeText(MainActivity.this, R.string.permission_request_denied, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            adapter.setData(Matisse.obtainResult(data), Matisse.obtainPathResult(data));
        }
    }

    private static class UriAdapter extends RecyclerView.Adapter<UriAdapter.UriViewHolder> {

        private List<Uri> mUris;
        private List<String> mPaths;
        private Map<Integer, String> map = new HashMap<>();

        void setData(List<Uri> uris, List<String> paths) {
            mUris = uris;
            mPaths = paths;
            notifyDataSetChanged();
        }

        List<String> getData() {
            return mPaths;
        }

        Map<Integer, String> getProgressMap(){
            return map;
        }

        @NonNull
        @Override
        public UriAdapter.UriViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new UriViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_uri, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull UriAdapter.UriViewHolder holder, int position) {
            holder.bind.uri.setText(mUris.get(position).toString());
            holder.bind.path.setText(mPaths.get(position));
            holder.bind.size.setText(FileUtils.getFileSize(mPaths.get(position)));
            String s = map.get(position);
            if (TextUtils.isEmpty(s)){
                holder.bind.progress.setText("");
            }else {
                holder.bind.progress.setText(map.get(position));
            }
            holder.bind.getRoot().setAlpha(position % 2 == 0 ? 1.0f : 0.54f);
        }

        @Override
        public int getItemCount() {
            return mUris != null ? mUris.size() : 0;
        }

        static class UriViewHolder extends RecyclerView.ViewHolder {

            private ItemUriBinding bind;

            public UriViewHolder(View itemView) {
                super(itemView);
                bind = DataBindingUtil.bind(itemView);
            }
        }
    }

    private void uploadImage() {
        CompressUtils.asyncCompress(adapter.getData(), new OnCompressListener<List<File>>() {
            @Override
            public void onStart() {
                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.show();
            }

            @Override
            public void onSuccess(List<File> files) {
                Log.d("Compress","-------------------" + files.toString());
                progressDialog.dismiss();
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                progressDialog.dismiss();
            }
        });
//        MultiTaskUtil<String> uploadUtil = new MultiTaskUtil<String>(new OSSUploadTask());
//        uploadUtil.setOnUploadListener(new OnUploadListener<String>() {
//            @Override
//            public void onAllSuccess() {
//                //TODO 校验是否全部上传成功
//                LogUtils.d("onAllSuccess");
//                long l = System.currentTimeMillis() - startTime;
//                LogUtils.d("耗时 - " + l);
//            }
//
//            @Override
//            public void onAllFailed() {
//                LogUtils.d("onAllFailed");
//            }
//
//            @Override
//            public void onThreadStart(int position) {
//                LogUtils.d("onThreadStart: Thread --" + position);
//                adapter.getProgressMap().put(position,"开始压缩上传");
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onThreadProgress(int percent, int position) {
//                LogUtils.d("onThreadProgress: position --- " + position + "percent--" + percent + "%");
//                adapter.getProgressMap().put(position,percent+"%");
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onThreadFinish(int position, String s) {
//                //TODO 保存上传路径
//                LogUtils.d("onThreadFinish: Thread -- " + position + "-" + s);
//                adapter.getProgressMap().put(position,"上传完成");
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onThreadInterrupted(int position) {
//                LogUtils.d("onThreadInterrupted: Thread --" + position);
//                adapter.getProgressMap().put(position,"上传失败");
//                adapter.notifyDataSetChanged();
//            }
//        });
//
//        startTime = System.currentTimeMillis();
//        if (adapter != null && adapter.getData() != null)
//            uploadUtil.submitAll(adapter.getData());
    }
}
