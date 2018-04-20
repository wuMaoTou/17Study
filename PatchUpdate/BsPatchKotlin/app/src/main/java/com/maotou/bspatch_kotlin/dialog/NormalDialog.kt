package com.maotou.bspatch_kotlin.dialog

import android.content.Context
import android.os.Bundle
import com.maotou.bspatch_kotlin.R
import kotlinx.android.synthetic.main.dialog_normal_layout.*

/**
 * Created by wuchundu on 18-4-17.
 */
class NormalDialog(context: Context) : BaseDialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_normal_layout)
    }

    fun setData(title: String ?= "提示", msg: String, no: String ?= "取消", yes: String ?= "确定", listener: OnNoOrYesListener) {
        tv_title.text = title
        tv_message.text = msg
        bt_no.text = no
        bt_yes.text = yes
        bt_no.setOnClickListener {
            listener.onNo()
            dismiss()
        }
        bt_yes.setOnClickListener {
            listener.onYes()
            dismiss()
        }
    }

    interface OnNoOrYesListener {
        fun onNo()
        fun onYes()
    }
}