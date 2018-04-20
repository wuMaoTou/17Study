package com.maotou.bspatch;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.PermissionRequest;
import android.widget.Toast;

import com.maotou.bspatch.databinding.ActivityMainBinding;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    public static final String SDCARD_PATH = Environment.getExternalStorageDirectory() + File.separator + "Download"  + File.separator;
    public static final String PATCH_FILE = "old-to-new.patch";
    public static final String NEW_APK_FILE = "new.apk";
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
        }

        
    }

    private void patch(){
        if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            new ApkUpdateTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }else{
            Toast.makeText(this,"没有读写权限",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 合并增量文件任务
     */
    private class ApkUpdateTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            String oldApkPath = ApkUtils.getCurApkPath(MainActivity.this);
            File oldApkFile = new File(oldApkPath);
            File patchFile = new File(getPatchFilePath());
            if(oldApkFile.exists() && patchFile.exists()) {
                Log("正在合并增量文件...");
                String newApkPath = getNewApkFilePath();
                BsPatch.applyPatch(oldApkPath, newApkPath, getPatchFilePath());
//                //检验文件MD5值
//                return Signtils.checkMd5(oldApkFile, MD5);

                Log("增量文件的MD5值为：" + SignUtils.getMd5ByFile(patchFile));
                Log("新文件的MD5值为：" + SignUtils.getMd5ByFile(new File(newApkPath)));

                return true;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if(result) {
                Log("合并成功，开始安装");
                ApkUtils.installApk(MainActivity.this, getNewApkFilePath());
            } else {
                Log("合并失败");
            }
        }
    }

    private String getPatchFilePath() {
        return SDCARD_PATH + PATCH_FILE;
    }

    private String getNewApkFilePath() {
        return SDCARD_PATH + NEW_APK_FILE;
    }

    /**
     * 打印日志
     * @param log
     */
    private void Log(String log) {
        Log.e("MainActivity", log);
    }

}
