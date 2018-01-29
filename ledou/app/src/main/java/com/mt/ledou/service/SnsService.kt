package com.mt.ledou

import android.text.TextUtils
import com.mt.ledou.utils.LogUtils
import khttp.get
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL

/**
 * Created by wuchundu on 18-1-25.
 * 挑战好友
 */
class SnsService {

    fun init() {
        // 0 => '好友', 1 => '帮友', 2 => '斗友',
        var typeMap = mapOf(0 to "好友", 1 to "帮友", 2 to "斗友")

        for ((k, v) in typeMap) {
            val respone = Request.request("sns", "cmd=sns&op=query&needreload=1&type=${k}")
            if (TextUtils.isEmpty(respone.text)) {
                return
            }
            LogUtils.d("[${v}PK-初始化]---" + respone.text)
            val friendlist = respone.jsonObject.getJSONArray("friendlist")

            snsFight(friendlist, k, v);
        }
        shopReelGold()
    }

    /**
     * PK
     */
    private fun snsFight(friendlist: JSONArray, type: Int, typeName: String) {
        var fightNum = 0;
        for (i in 0..friendlist.length()-1) {
            fightNum++
            if (fightNum > 15) {
                break
            }
            val jsonObject = friendlist.get(i) as JSONObject
            if (jsonObject.getInt("can_fight") == 0) {
                continue
            }
            val uid = jsonObject.getString("uid")
            val respone = Request.request("sns", "cmd=sns&op=fight&target_uid=${uid}&type=${type}")
            LogUtils.d("[${typeName}PK-执行]---" + respone.text)
            val result = respone.jsonObject.getInt("result")
            if (result != 0) {
                snsLimitAndUse()
            }
            Thread.sleep(200)
        }
    }

    /**
     * 获取体力药水信息并使用
     */
    private fun snsLimitAndUse(): Boolean {

        val respone = Request.request("limit", "cmd=limit&goodslist=100001|100002|100003")
        if (TextUtils.isEmpty(respone.text)) {
            return false
        }
        LogUtils.d("[获取体力药水信息]---" + respone.text)
        val limit_info = respone.jsonObject.getJSONArray("limit_info")
        for (i in 0..limit_info.length()-1) {
            val jsonObject = limit_info.get(i) as JSONObject
            val limit = jsonObject.getInt("limit")
            val Goods = jsonObject.getString("Goods")
            if (limit > 0) {
                val limitRespone = Request.request("storage", "cmd=storage&op=use&id=${Goods}")
                LogUtils.d("[使用体力药水]---" + limitRespone.text)
            }
        }
        return true;
    }

    /**
     * 胜点商城-兑换黄金卷轴
     * @return bool
     */
    private fun shopReelGold() {

        val respone = Request.request("shop", "cmd=shop&subtype=1&num=6&id=100023&price=120")
        if (TextUtils.isEmpty(respone.text)) {
            return
        }
        LogUtils.d("[胜点商城-兑换黄金卷轴]---" + respone.text)
    }

}