package com.maotou.bspatch_kotlin.httpservice.service

import com.maotou.bspatch_kotlin.httpservice.exception.ApiException
import com.maotou.bspatch_kotlin.httpservice.httpentity.HttpResult
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers

/**
 * Created by wuchundu on 18-4-13.
 */
open class BaseApi {

    fun <T> toSubscribe(o: Observable<T>, s: Observer<T>) {
        o.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s)
    }

    class HttpResultFunc<T> : Function<HttpResult<T>, T> {
        override fun apply(t: HttpResult<T>): T {
            if (t.status === 0){
                return t.data
            }
            throw ApiException(t.status,t.msg)
        }
    }

}