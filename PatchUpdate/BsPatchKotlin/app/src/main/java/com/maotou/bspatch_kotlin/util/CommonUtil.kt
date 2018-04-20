package com.maotou.bspatch_kotlin.util

import android.os.Environment
import java.math.BigDecimal
import java.nio.file.Files.exists
import android.os.Environment.getExternalStorageDirectory
import android.util.Log
import java.io.File


/**
 * Created by wuchundu on 18-4-17.
 */
object CommonUtil {
    /**
     * 字节单位转换
     * @param size
     * @return
     */
    fun getFormatSize(size: Double): String {
        val kiloByte = size / 1024
        if (kiloByte < 1) {
            return "0M"
        }

        val megaByte = kiloByte / 1024
        if (megaByte < 1) {
            val result1 = BigDecimal(java.lang.Double.toString(kiloByte))
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "K"
        }

        val gigaByte = megaByte / 1024
        if (gigaByte < 1) {
            val result2 = BigDecimal(java.lang.Double.toString(megaByte))
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "M"
        }

        val teraBytes = gigaByte / 1024
        if (teraBytes < 1) {
            val result3 = BigDecimal(java.lang.Double.toString(gigaByte))
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "G"
        }
        val result4 = BigDecimal(teraBytes)
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "T"
    }

    /**
     * @param saveDir
     * @return
     * @throws IOException
     * 判断下载目录是否存在
     */
    fun makeDir(saveDir: String): String {
        // 下载位置
        val file = File(Environment.getExternalStorageDirectory().absolutePath + File.separator + "Download$saveDir")
        if (!file.exists()) {
            file.mkdirs()
        }
        val savePath = file.getAbsolutePath()
        Log.d("CommonUtil", savePath)
        return savePath
    }
}