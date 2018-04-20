package com.maotou.bspatch_kotlin.httpservice.subscribers

import android.content.Context
import com.maotou.bspatch_kotlin.httpservice.exception.ApiException
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import java.net.ConnectException
import java.net.SocketTimeoutException

/**
 * Created by wuchundu on 18-4-13.
 */
class HttpSubscriber<T>(var subscriberOnListener: SubscriberOnListener<T>, var context: Context) : Observer<T> {

    var disposable : Disposable? = null

    override fun onSubscribe(d: Disposable) {
        disposable = d
    }

    override fun onComplete() {
        if (disposable != null && !disposable?.isDisposed!!){
            disposable?.dispose()
        }
    }

    override fun onNext(t: T) {
        subscriberOnListener.onSucceed(t)
    }

    override fun onError(e: Throwable) {
        when(e){
            is SocketTimeoutException -> subscriberOnListener.onError(-1001,"网络连接超时,请检查您的网络状态")
            is ConnectException -> subscriberOnListener.onError(-1002, "网络连接中断,请检查您的网络状态")
            is ApiException -> subscriberOnListener.onError(e.code, e.msg)
            else -> subscriberOnListener.onError(-1003, "未知错误${e.message}")
        }
    }
}