package com.maotou.bspatch_kotlin.httpservice.exception

/**
 * Created by wuchundu on 18-4-12.
 */
class ApiException(var code: Int, var msg: String) : RuntimeException() {}