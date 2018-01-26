package com.mt.ledou.service

import android.text.TextUtils
import com.mt.ledou.Request
import com.mt.ledou.utils.LogUtils
import org.json.JSONObject

/**
 * Created by wuchundu on 18-1-25.
 */

class VisitService {
    fun init(){
        val respone = Request.request("meridian", "cmd=meridian&op=visitpage")
        if (TextUtils.isEmpty(respone.text)) {
            return
        }
        LogUtils.d("[造访-初始化]---" + respone.text)
        val act_npc = respone.jsonObject.getString("act_npc")
        while (true){
            var npcid = act_npc;
            val meridianRespone = Request.request("meridian", "cmd=meridian&op=visit&id=${npcid}")
            val result = meridianRespone.jsonObject.getInt("reault")
            if (result != 0){
                if (limitAndUse()){
                    continue
                }else{
                    return
                }
            }
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
        for (i in 0..limit_info.length()) {
            val jsonObject = limit_info.get(i) as JSONObject
            val Num = jsonObject.getInt("Num")
            val Goods = jsonObject.getString("Goods")
            if (Num == 0){
                if (i == limit_info.length()){
                    return false
                }else {
                    continue
                }
            }
            for (j in 0..Num){
                val storageRespone = Request.request("storage", "cmd=storage&op=use&id=${Goods}")
                LogUtils.d("[使用${Goods}威望旗]---" + storageRespone.text)
            }
        }
        return true;
    }
}