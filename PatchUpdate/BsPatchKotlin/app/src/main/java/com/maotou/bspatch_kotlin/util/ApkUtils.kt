package com.maotou.bspatch_kotlin.util

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.net.Uri
import android.os.Build
import android.support.v4.content.FileProvider

import java.io.File
import android.content.pm.PackageManager
import android.R.attr.versionName
import android.content.pm.PackageInfo



/**
 * Created by wuchundu on 18-4-11.
 */
object ApkUtils {

    /**
     * 获取当前应用的Apk路径
     * @param context 上下文
     * @return
     */
    fun getCurApkPath(context: Context): String {
        var context = context
        context = context.applicationContext
        val applicationInfo = context.applicationInfo
        return applicationInfo.sourceDir
    }

    /**
     * 安装Apk
     * @param context 上下文
     * @param apkPath Apk路径
     */
    fun installApk(context: Context, apkPath: String) {
        val apkfile = File(apkPath)
        if (!apkfile.exists()) {
            return
        }
        val i = Intent(Intent.ACTION_VIEW)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (Build.VERSION.SDK_INT >= 24) { //判读版本是否在7.0以上
            //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
            val apkUri = FileProvider.getUriForFile(context, context.applicationContext.packageName + ".provider.download", apkfile)
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            i.setDataAndType(apkUri, "application/vnd.android.package-archive")
        } else {
            i.setDataAndType(Uri.fromFile(apkfile), "application/vnd.android.package-archive")
        }
        context.startActivity(i)
    }

    fun getVersionCode(context: Context): Int {
        val packageManager = context.packageManager
        val packageInfo: PackageInfo
        var versionCode = 0
        try {
            packageInfo = packageManager.getPackageInfo(context.packageName, 0)
            versionCode = packageInfo.versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return versionCode
    }

    fun getVersionName(context: Context): String {
        val packageManager = context.packageManager
        val packageInfo: PackageInfo
        var versionName = ""
        try {
            packageInfo = packageManager.getPackageInfo(context.packageName, 0)
            versionName = packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return versionName
    }
}
