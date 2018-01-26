package com.mt.ledou

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

/**
 * Created by wuchundu on 18-1-25.
 */
class App : Application() {


    override fun onCreate() {
        super.onCreate()
        instanceTmp = this
    }

    companion object{

        @SuppressLint("StaticFieldLeak")
        private var instanceTmp : App? = null

        val instance : App by lazy{
            instanceTmp!!
        }
    }
}