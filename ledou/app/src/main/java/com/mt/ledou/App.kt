package com.mt.ledou

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.widget.Toast

/**
 * Created by wuchundu on 18-1-25.
 */
class App : Application() {


    override fun onCreate() {
        super.onCreate()
        instanceTmp = this
    }

    companion object{

        private var instanceTmp : App? = null

        val instance : App by lazy{
            instanceTmp!!
        }

        fun toast(msg : String){
            Toast.makeText(instanceTmp!!.applicationContext,msg,Toast.LENGTH_SHORT).show()
        }
    }
}