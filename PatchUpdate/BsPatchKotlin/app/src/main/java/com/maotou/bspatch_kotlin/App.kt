package com.maotou.bspatch_kotlin

import android.app.Application
import com.maotou.bspatch_kotlin.httpservice.beans.UserBean

/**
 * Created by wuchundu on 18-4-12.
 */
class App : Application() {

    companion object {
        private var instanceTmp: App? = null
        val instance by lazy { instanceTmp!! }
    }

    var userBean: UserBean? = null

    override fun onCreate() {
        super.onCreate()
        instanceTmp = this
    }

    fun getToken(): String{
        if (userBean != null){
            return userBean!!.token
        }
        return ""
    }

    fun getPhone(): String{
        if (userBean != null){
            if (userBean!!.phone.isEmpty()){
                return ""
            }
            return userBean!!.phone
        }
        return ""
    }

}