package com.maotou.bspatch_kotlin.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowManager
import com.maotou.bspatch_kotlin.R

/**
 * Created by wuchundu on 18-4-17.
 */
open class BaseDialog(context: Context, style: Int = R.style.StyleDialog) : Dialog(context,style) {

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
    }

}