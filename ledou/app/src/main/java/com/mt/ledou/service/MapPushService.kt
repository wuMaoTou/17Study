package com.mt.ledou.service

import android.text.TextUtils
import com.mt.ledou.Request
import com.mt.ledou.utils.LogUtils

/**
 * Created by wuchundu on 18-1-26.
 * 历练
 */
class MapPushService {


    fun init() {

        normal()
        Thread.sleep(200);
        heroic();
    }

    /**
     * 普通历练
     * @return bool
     */
    private fun normal() {
        val respone = Request.request("mappush", "cmd=mappush&subcmd=GetUser&dup=0")
        if (TextUtils.isEmpty(respone.text)) {
            return
        }
        LogUtils.d("[历练-初始化]---" + respone.text)
        val curdup = respone.jsonObject.getJSONObject("userinfo").getInt("curdup")
        val curlevel = respone.jsonObject.getJSONObject("userinfo").getJSONObject("info").getInt("curlevel")
        mapPushPk(curdup, curlevel);
    }

    /**
     * 英雄历练
     */
    private fun heroic() {

        var arr = arrayOf(3, 6, 9, 12)
        var map = mapOf("10001" to arr, "10002" to arr, "10002" to arr)

        for ((k, v) in map) {
            for (item in v) {
                val respone = Request.request("mappush", "cmd=mappush&subcmd=DoPk&dup=${k}&level=${item}")
                LogUtils.d("[历练-英雄试炼]---" + respone.text)
                Thread.sleep(200)
            }
        }
    }

    /**
     * 执行历练pk
     */
    private fun mapPushPk(curdup: Int, curlevel: Int) {

        var innerCurdup = curdup
        var innerCurlevel = curlevel

        while (true) {

            val respone = Request.request("mappush", "cmd=mappush&subcmd=DoPk&dup=${innerCurdup}&level=${innerCurlevel}")
            if (TextUtils.isEmpty(respone.text)) {
                return
            }
            LogUtils.d("[历练-PK]---" + respone.text)
            val result = respone.jsonObject.getInt("result")
            if (result == 100 && mapPushLimit()) {
                if (mapPushStorage()){
                    continue
                }else{
                    break
                }
            }
            if (result != 0) {
                break;
            }

            val win = respone.jsonObject.getInt("win")

            if (win == 0) {
                //如果输了，就降级执行
                innerCurlevel--;
                if (innerCurlevel < 0) {
                    innerCurdup--;
                    innerCurlevel = 15;
                }
            } else {
                //如果赢了，就升级执行
                innerCurlevel++;
                if (innerCurlevel > 15) {
                    innerCurlevel = 0;
                    innerCurdup++;
                }
            }
            Thread.sleep(200)
        }

    }

    /**
     * 获取江湖令信息
     * @return bool
     */
    private fun mapPushLimit(): Boolean {
        val respone = Request.request("limit", "cmd=limit&goodslist=100015")
        if (TextUtils.isEmpty(respone.text)) {
            return false
        }
        LogUtils.d("[历练-获取江湖令信息]---" + respone.text)
        val limit_num = respone.jsonObject.getJSONArray("limit_info").getJSONObject(0).getInt("limit")
        if (limit_num > 0) {
            return true;
        }
        return false;
    }

    /**
     * 使用江湖令牌
     * @return bool
     */
    private fun mapPushStorage(): Boolean {
        val respone = Request.request("storage", "cmd=storage&op=use&id=100015")
        if (TextUtils.isEmpty(respone.text)) {
            return false
        }
        LogUtils.d("[历练-使用江湖令牌]---" + respone.text)
        return true;
    }

}