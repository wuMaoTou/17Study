package com.maotou.bspatch_kotlin.httpservice.subscribers

/**
 * Created by wuchundu on 18-4-13.
 */
interface SubscriberOnListener<T> {

    fun onSucceed(data: T)

    fun onError(code: Int, msg: String)

}