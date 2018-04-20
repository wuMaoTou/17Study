package com.maotou.bspatch_kotlin.httpservice.httpentity

/**
 * Created by wuchundu on 18-4-12.
 */
class HttpResult<T>(var status: Int, var msg: String, var data: T) {}