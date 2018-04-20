package com.maotou.bspatch;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import java.io.File;

/**
 * Created by wuchundu on 18-4-11.
 */
public class ApkUtils {

    /**
     * 获取当前应用的Apk路径
     * @param context 上下文
     * @return
     */
    public static String getCurApkPath(Context context) {
        context = context.getApplicationContext();
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        String apkPath = applicationInfo.sourceDir;
        return apkPath;
    }

    /**
     * 安装Apk
     * @param context 上下文
     * @param apkPath Apk路径
     */
    public static void installApk(Context context, String apkPath) {
        File apkfile = new File(apkPath);
        if (!apkfile.exists()) {
            return;
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 24) { //判读版本是否在7.0以上
            //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
            Uri apkUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider.download", apkfile);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            i.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            i.setDataAndType(Uri.fromFile(apkfile), "application/vnd.android.package-archive");
        }
        context.startActivity(i);
    }
}
