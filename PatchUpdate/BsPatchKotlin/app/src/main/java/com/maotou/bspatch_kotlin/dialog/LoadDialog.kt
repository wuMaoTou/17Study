package com.maotou.bspatch_kotlin.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.maotou.bspatch_kotlin.R
import kotlinx.android.synthetic.main.loading_layout.*

/**
 * Created by wuchundu on 18-4-12.
 */
class LoadDialog(context: Context) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loading_layout)
    }

    fun setLoadMsg(msg: CharSequence){
        if (!msg.isEmpty()){
            tv_load_msg.text= msg;
        }
    }
}