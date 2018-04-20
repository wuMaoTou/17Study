package com.maotou.bspatch_kotlin.ui

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import com.maotou.bspatch_kotlin.App
import com.maotou.bspatch_kotlin.R
import com.maotou.bspatch_kotlin.dialog.LoadDialog
import com.maotou.bspatch_kotlin.httpservice.beans.UserBean
import com.maotou.bspatch_kotlin.httpservice.serviceapi.UserApi
import com.maotou.bspatch_kotlin.httpservice.subscribers.HttpSubscriber
import com.maotou.bspatch_kotlin.httpservice.subscribers.SubscriberOnListener
import kotlinx.android.synthetic.main.activity_userservice_test.*
import org.jetbrains.anko.toast
import java.util.*

/**
 * Created by wuchundu on 18-4-13.
 */
class UserServiceTestActivity : AppCompatActivity() {

    var loadDialog: LoadDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_userservice_test)

        bt_register.setOnClickListener {
            showDialog("注册中...")
            register()
        }

        bt_login.setOnClickListener{
            showDialog("登录中...")
            login()
        }

        bt_getinfo.setOnClickListener{
            showDialog("获取信息中..")
            getInfo();
        }
    }

    private fun login() {
        val phone = et_phone.text.toString()
        if (phone.isEmpty()) {
            toast("请输入手机号码")
            return
        }
        val passwork = et_password.text.toString()
        if (passwork.isEmpty()) {
            toast("请输入密码")
            return
        }
        UserApi.instance.login(phone,passwork,HttpSubscriber<UserBean>(object : SubscriberOnListener<UserBean>{
            override fun onSucceed(data: UserBean) {
                hideLoadDialog()
                tv_response.text = data.toString()
                App.instance.userBean = data
            }

            override fun onError(code: Int, msg: String) {
                hideLoadDialog()
                tv_response.text = "status:$code,msg:$msg"
            }

        },this))
    }

    private fun register() {
        val phone = et_phone.text.toString()
        if (phone.isEmpty()) {
            toast("请输入手机号码")
            return
        }
        val passwork = et_password.text.toString()
        if (passwork.isEmpty()) {
            toast("请输入密码")
            return
        }
        val r = Random()
        UserApi.instance.register(
                phone,
                passwork,
                "user${r.nextInt()}",
                r.nextInt(2),
                r.nextInt(30),
                HttpSubscriber<UserBean>(object : SubscriberOnListener<UserBean>{
                    override fun onSucceed(data: UserBean) {
                        hideLoadDialog()
                        toast("注册成功")
                        App.instance.userBean = data
                    }

                    override fun onError(code: Int, msg: String) {
                        hideLoadDialog()
                        toast("status:$code,msg:$msg")
                    }

                },this)
        )
    }

    fun getInfo(){
        val phone = et_phone.text.toString()
        if (phone.isEmpty()) {
            toast("请输入手机号码")
            return
        }

        UserApi.instance.userinfo(phone,HttpSubscriber<UserBean>(object: SubscriberOnListener<UserBean>{

            override fun onSucceed(data: UserBean) {
                hideLoadDialog()
                tv_response.setText(data.toString())
            }

            override fun onError(code: Int, msg: String) {
                hideLoadDialog()
                toast("status:$code,msg:$msg")
            }

        },this))
    }

    fun showDialog(msg: String) {
        if (loadDialog == null) {
            loadDialog = LoadDialog(this)
        }
        loadDialog?.show()
        loadDialog?.setLoadMsg(msg)
    }

    fun hideLoadDialog() {
        if (loadDialog !== null && loadDialog?.isShowing!!) {
            loadDialog?.dismiss()
        }
    }
}