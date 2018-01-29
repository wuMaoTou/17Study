package com.mt.ledou

import android.widget.Toast
import com.mt.ledou.utils.LogUtils
import khttp.get
import khttp.post
import khttp.responses.Response

/**
 * Created by wuchundu on 18-1-25.
 */
object Request {

    var params = mutableMapOf<String, String>();

    fun request(cmd: String, postData: String): Response {
        val data = makePostData(postData)
//        val url = Contacts.BASE_URL + "uid=${Contacts.UID}&cmd=${cmd}"
        val url = Contacts.BASE_URL + dataToString(data)
//        val response = post(url, mapOf(), data)
        LogUtils.d("[执行出现异常]", "url---{${url}}\n params---{${data}}")
        val response  = get(url)
        val result = response.jsonObject.getInt("result")
        if (result != 0) {
            if (result == 110) {
                LogUtils.d("很抱歉，鉴权失败，请退出重试！")
                return response
            }else{
                LogUtils.d("[执行出现异常]", "url---{${url}}\n params---{${data}}\n response---{${response}}")
                return response
            }
        }
        return response
    }

    /**
     * 组合请求参数
     * @param string|array $mixData
     * @return array|string
     */
    private fun makePostData(data: String): MutableMap<String, String> {

        params.clear()

        for ((k, v) in Contacts.TOKEN_PARAMS) {
            Request.params.put(k, v);
        }

        val datas = data.trim().split("&")

        for (d in datas) {
            val split = d.split("=")
            params.put(split[0], split[1])
        }

        return params
    }

    private fun dataToString(map : MutableMap<String,String>) : String{
        var data = StringBuilder()

        for ((k,v) in map){
            data.append(k)
            data.append("=")
            data.append(v)
            data.append("&")
        }

        return data.substring(0,data.length-1)
    }
}