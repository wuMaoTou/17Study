package com.maotou.bspatch_kotlin.httpservice.subscribers

/**
 * Created by wuchundu on 18-4-13.
 */
fun getResponseCode(code: Int, msg: String): String = when{
    code == 1 -> "1"
    code == -1 -> "参数不合法"
    else -> msg
}