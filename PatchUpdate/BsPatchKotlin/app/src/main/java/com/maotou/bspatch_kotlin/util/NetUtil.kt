package com.maotou.bspatch_kotlin.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

/**
 * Created by wuchundu on 18-4-12.
 */
object NetUtil {

    const val NETWORK_NONE = 0
    const val NETWORK_WIFI = 1
    const val NETWORK_MOBILE = 2

    /**
     * 获取网络状态
     */
    fun getNetworkState(context: Context): Int {
        val connManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        var state = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).state
        if (state === NetworkInfo.State.CONNECTED || state === NetworkInfo.State.CONNECTING) {
            return NETWORK_WIFI
        }

        state = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).state
        if (state === NetworkInfo.State.CONNECTED || state === NetworkInfo.State.CONNECTING) {
            return NETWORK_MOBILE
        }

        return NETWORK_NONE
    }

    /**
     * 获取当前网络状态
     */
    fun getNetworkIsConnected(context: Context): Boolean{
        val connManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkInfo = connManager.activeNetworkInfo

        if (networkInfo === null){
            return false
        }

        return networkInfo.isConnected
    }

    /**
     * 当前是否wifi网络
     */
    fun isWifi(context: Context): Boolean{
        val connManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connManager.activeNetworkInfo
        if (networkInfo === null){
            return false
        }

        return networkInfo.type == ConnectivityManager.TYPE_WIFI
    }
}