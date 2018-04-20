package com.maotou.bspatch_kotlin.dialog

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.KeyEvent
import com.maotou.bspatch_kotlin.R
import com.maotou.bspatch_kotlin.util.ApkUtils
import com.maotou.bspatch.BsPatch
import com.maotou.bspatch_kotlin.util.CommonUtil
import com.maotou.bspatch_kotlin.util.DownloadUtil
import kotlinx.android.synthetic.main.dialog_update_layout.*
import org.jetbrains.anko.toast
import java.io.File

/**
 * Created by wuchundu on 18-4-17.
 */
class UpdateDialog(context: Context) : BaseDialog(context) {

    var patchSize = 0L
    var fileSize = 0L
    var url = ""
    var patchPath = ""
    var newApkPath = ""
    var isFinish = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_update_layout)
        setOnKeyListener { dialog, keyCode, event ->
            keyCode == KeyEvent.KEYCODE_BACK && event.repeatCount == 0
        }

        iv_close.setOnClickListener {
            DownloadUtil.instance.cancelDownload()
            dismiss()
        }

        btn_status.setOnClickListener {
            when(isFinish){
                //失败重试
                -1 -> setInfo(url,patchPath,newApkPath,patchSize,fileSize)
                //下载中
                0 -> {
                    DownloadUtil.instance.cancelDownload()
                    dismiss()
                }
                //下载成功
                1 -> {
                    val file = File(newApkPath)
                    if (file.exists()){
                        ApkUtils.installApk(context,newApkPath)
                    }else{
                        Log.d("UpdateDialog","apk不存在-->$newApkPath")
                        isFinish = -1
                        tv_title.text = "安装包不存在"
                        btn_status.text = "下载"
                        context.toast("安装包不存在,请重新下载")
                    }
                }
            }
        }
    }

    fun setInfo(url: String, patchPath: String, newApkPath: String, patchSize: Long, fileSize: Long){
        this.url = url
        this.patchPath = patchPath
        this.newApkPath = newApkPath
        this.patchSize = patchSize
        this.fileSize = fileSize

        Log.d("UpdateDialog", "url:$url patchPath:$patchPath newApkPath:${this.newApkPath} patchSize: $patchSize fileSize:$fileSize")

        progressbar.max = 100
        progressbar.progressDrawable = context.resources.getDrawable(R.drawable.color_progressbar)
        progressbar.progress = 0
        tv_downsize.text = "0M/${CommonUtil.getFormatSize(patchSize.toDouble())}"
        btn_status.text = "取消"
        tv_title.text = "下载新版本"
        tv_p_progress.text = "0%"
        DownloadUtil.instance.download(url,patchPath,patchSize,object : DownloadUtil.OnDownloadListener{
            override fun onDownloadSuccess() {
                if (context != null){
                    val file = File(this@UpdateDialog.newApkPath)
                    if (!file.exists()){
                        try{
                            file.createNewFile()
                        }catch (e:Exception){
                            e.printStackTrace()
                        }
                    }

                    var restus = BsPatch.applyPatch(ApkUtils.getCurApkPath(context), this@UpdateDialog.newApkPath,patchPath)
                    if (restus == 0){
                        isFinish = 1
                        Message.obtain(handler,1,100).sendToTarget()
                    }else{
                        isFinish = -1
                        Message.obtain(handler,1).sendToTarget()
                    }
                }
            }

            override fun onDownloading(progress: Int) {
                if (context != null){
                    if (progress < 100){
                        isFinish = 0
                    }else{
                        isFinish = 1
                    }
                    Message.obtain(handler,0,progress).sendToTarget()
                }
            }

            override fun onDownloadFailed() {
                if (context != null){
                    isFinish = -1
                    Message.obtain(handler,-1).sendToTarget()
                }
            }

        })
    }

    val handler = object : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            when(msg?.what){
                -1 -> {
                    btn_status.text = "重试"
                    tv_title.text = "下载失败"
                }

                0 -> {
                    var progress = msg.obj as Int
                    tv_p_progress.text = "$progress%"
                    progressbar.progress = progress
                    tv_downsize.text = "${CommonUtil.getFormatSize((patchSize * progress / 100).toDouble())}/${CommonUtil.getFormatSize(patchSize.toDouble())}"
                }

                1 -> {
                    btn_status.text = "安装"
                }

            }
        }
    }

}