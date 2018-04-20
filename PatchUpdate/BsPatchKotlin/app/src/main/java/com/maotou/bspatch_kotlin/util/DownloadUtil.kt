package com.maotou.bspatch_kotlin.util

import android.util.Log
import okhttp3.*
import java.io.*
import java.util.concurrent.TimeUnit

/**
 * Created by wuchundu on 18-4-17.
 */
class DownloadUtil {

    companion object {
        val instance by lazy { DownloadUtil() }
    }

    private var okHttpClient: OkHttpClient? = null

    init {
        okHttpClient = OkHttpClient.Builder()
                .connectTimeout(60,TimeUnit.SECONDS)
                .readTimeout(60,TimeUnit.SECONDS)
                .build()
    }

    fun download(url: String,saveApkPath: String, fileSize: Long, listener: OnDownloadListener){
        val downloadCallBack = DownloadCallBack(saveApkPath, fileSize, listener)
        val request = Request.Builder().url(url).build()
        okHttpClient?.newCall(request)?.enqueue(downloadCallBack)
    }

    fun cancelDownload(){
        if (okHttpClient != null){
            okHttpClient!!.dispatcher().cancelAll()
        }
    }

    private class DownloadCallBack(private var saveDir: String, private var fileSize: Long, private var listener: OnDownloadListener) : Callback{

        override fun onFailure(call: Call?, e: IOException?) {
            listener.onDownloadFailed()
            Log.d("DownloadUtil",e.toString())
        }

        override fun onResponse(call: Call?, response: Response?) {
            var ins: InputStream? = null
            var buf = ByteArray(2048)
            var len = 0
            var fos: FileOutputStream? = null

            try {
                if (response!!.code() == 200){
                    ins = response.body()!!.byteStream()
                    Log.d("DownloadUtil","length:${response.body()?.contentLength()}")
                    val file = File(saveDir)
                    fos = FileOutputStream(file)
                    var sum: Long = 0

                    while ((ins.read(buf).apply { len = this}) != -1){
                        fos.write(buf, 0, len)
                        sum += len
                        var progress = (sum * 1.0f / fileSize * 100).toInt()
                        Log.d("DownloadUtil","progress:${progress}")
                        listener.onDownloading(progress)
                    }
                    fos.flush()
                    listener.onDownloadSuccess()
                }
            }catch (e: Exception){
                e.printStackTrace()
                listener.onDownloadFailed()
            }finally {
                try {
                    if (ins != null){
                        ins.close()
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                }

                try {
                    if (fos != null){
                        fos.close()
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }
        }

    }

    interface OnDownloadListener{

        /**
         * 下载成功
         */
        fun onDownloadSuccess()

        /**
         * 下载中
         */
        fun onDownloading(progress: Int)

        /**
         * 下载失败
         */
        fun onDownloadFailed()
    }
}