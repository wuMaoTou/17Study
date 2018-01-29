package com.mt.ledou.service

import android.text.TextUtils
import com.mt.ledou.Request
import com.mt.ledou.utils.LogUtils
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by wuchundu on 18-1-25.
 */

class VisitService {
    fun init() {

        limitAndUse()

        val respone = Request.request("meridian", "cmd=meridian&op=visitpage")
        if (TextUtils.isEmpty(respone.text)) {
            return
        }
        LogUtils.d("[造访-初始化]---" + respone.text)
        var act_npc = respone.jsonObject.getString("act_npc")
        while (true) {
            val meridianRespone = Request.request("meridian", "cmd=meridian&op=visit&id=${act_npc}")
            LogUtils.d("[造访中]---" + meridianRespone.text)
            val result = meridianRespone.jsonObject.getInt("result")
            if (result == 0) {
                act_npc = meridianRespone.jsonObject.getString("act_npc")
                val awards = meridianRespone.jsonObject.getJSONArray("awards")
                if (awards.length() > 0){
                    use(awards)
                }
            }else{
                break
            }
            Thread.sleep(200)
        }
    }

    /**
     * 获取体力药水信息并使用
     */
    private fun limitAndUse(): Boolean {

        val respone = Request.request("limit", "cmd=limit&goodslist=100012|100013|100014")
        if (TextUtils.isEmpty(respone.text)) {
            return false
        }
        LogUtils.d("[获取威望旗信息]---" + respone.text)
        val limit_info = respone.jsonObject.getJSONArray("limit_info")
        for (i in 0..limit_info.length() - 1) {
            val jsonObject = limit_info.get(i) as JSONObject
            val Num = jsonObject.getInt("Num")
            val Goods = jsonObject.getString("Goods")
            if (Num == 0) {
                if (i == limit_info.length() - 1) {
                    return false
                } else {
                    continue
                }
            }
            for (j in 0..Num) {
                val storageRespone = Request.request("storage", "cmd=storage&op=use&id=${Goods}")
                LogUtils.d("[使用${Goods}威望旗]---" + storageRespone.text)
            }
        }
        return true;
    }

    private fun use(awards : JSONArray){
        for (i in 0..awards.length()-1){
            val jsonObject = awards.get(i) as JSONObject
            val index = jsonObject.getString("index")
            val respone = Request.request("meridian", "cmd=meridian&op=award&index=${index}")
            LogUtils.d("[使用天山雪莲]---" + respone.text)
        }
    }
}