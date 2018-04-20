package com.maotou.bspatch_kotlin.httpservice.service

import android.util.Log
import com.maotou.bspatch_kotlin.App
import com.maotou.bspatch_kotlin.util.NetUtil
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

/**
 * Created by wuchundu on 18-4-13.
 */
class HttpMethod private constructor() {

    companion object {
        const val BASE_URL = "http://172.16.5.247:8989/"
        const val Download_URL = "http://172.16.5.247:8889/apk/update/patch/"
        var token = "";
        var retrofit: Retrofit? = null
        val instance by lazy { HttpMethod()}
        fun genericClient(): OkHttpClient {
            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            //设置缓存路径
            val httpCacheDir = File(App.instance.externalCacheDir?.absolutePath, "responses")
            val cache = Cache(httpCacheDir, 50 * 1024 * 1024)
            return OkHttpClient.Builder()
                    .addInterceptor(object : Interceptor {
                        override fun intercept(chain: Interceptor.Chain?): Response? {
                            if (chain == null) {
                                return null
                            }

                            var request = chain.request()
                                    .newBuilder()
                                    .addHeader("token", App.instance.getToken())
                                    .addHeader("phone", App.instance.getPhone())
                                    .build()

                            if (!NetUtil.getNetworkIsConnected(App.instance)) {
                                request = request.newBuilder()
                                        .cacheControl(CacheControl.FORCE_CACHE)
                                        .build()
                            }

                            val response = chain.proceed(request);

                            Log.d("HttpMethod",response.body().toString())

                            if (!NetUtil.getNetworkIsConnected(App.instance)) {
                                // 有网络时 设置缓存超时时间0个小时
                                val maxAge = 0 * 60
                                response.newBuilder()
                                        .addHeader("Cache-Control", "public, max-age=" + maxAge)
                                        .removeHeader("Pragma")// 清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
                                        .build()
                            } else {
                                val maxStale = 60 * 60 * 24 * 7
                                response.newBuilder()
                                        .addHeader("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                                        .removeHeader("Pragma")
                                        .build()
                            }

                            return response
                        }
                    })
                    .addInterceptor(interceptor)
                    .cache(cache)
                    .build()
        }
    }

    init {
        retrofit = Retrofit.Builder()
                .client(genericClient())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .build()
    }

    fun <T> createApi(clazz: Class<T>): T? {
        return retrofit?.create(clazz)
    }

}

