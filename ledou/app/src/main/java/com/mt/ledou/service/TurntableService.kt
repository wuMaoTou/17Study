package com.mt.ledou.service

import android.text.TextUtils
import com.mt.ledou.Request
import com.mt.ledou.utils.LogUtils

/**
 * Created by wuchundu on 18-1-26.
 */
class TurntableService {
    fun init() {

        val respone = Request.request("activity", "cmd=activity&aid=24&sub=0")
        if (TextUtils.isEmpty(respone.text)) {
            return
        }
        LogUtils.d("[黄金转盘-初始化]---" + respone.text)

        val daynum = respone.jsonObject.getInt("daynum")

        for (i in 0..daynum) {
            val respone = Request.request("activity", "cmd=activity&aid=24&sub=1")
            LogUtils.d("[黄金转盘-启动转盘]---" + respone.text)
            Thread.sleep(200)
        }
    }
}