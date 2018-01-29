package com.mt.ledou

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Environment
import android.security.KeyChain
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.mt.ledou.service.MapPushService
import com.mt.ledou.service.QualifyingService
import com.mt.ledou.service.TurntableService
import com.mt.ledou.service.VisitService
import com.mt.ledou.utils.SharedPreferenceUtils
import kotlinx.android.synthetic.main.activity_main.*
import org.apache.commons.io.IOUtils
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.onUiThread
import org.jetbrains.anko.toast
import java.io.FileInputStream

class MainActivity : AppCompatActivity() {

    lateinit var receiver : Receiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        EventBus.getDefault().register(this)
        start.setOnClickListener {
            if (getUserToken(edittext.text.toString())) {
//                async { MapPushService().init() }
                start.setEnabled(false)
                val t = Thread(Runnable {
                    kotlin.run {
//                        try {
                            //好友挑战
                            SnsService().init()
                            runOnUiThread {
                                info.text = "完成好友挑战\n"
                            }
                            //历练
                            MapPushService().init()
                            runOnUiThread {
                                info.text = info.text.toString() + "完成历练\n"
                            }
                            //王者争霸
                            QualifyingService().init()
                            runOnUiThread {
                                info.text = info.text.toString() + "完成王者争霸\n"
                            }
                            //造访
                            VisitService().init()
                            runOnUiThread {
                                info.text = info.text.toString() + "完成造访\n"
                            }
                            //黄金转盘
                            TurntableService().init()
                            runOnUiThread {
                                info.text = info.text.toString() + "完成黄金转盘\n"
                                start.setEnabled(true)
                            }
//                        } catch (e : Exception) {
//                            runOnUiThread {
//                                e.printStackTrace()
//                                toast(e.toString())
//                                start.setEnabled(true)
//                            }
//                        }
                    }
                })
                t.start()
            }
        }
    }

    /**
     * 输入拦截请求的post数据
     */
    fun getUserToken(cookie: String): Boolean {

        if (TextUtils.isEmpty(cookie)) {
            onUiThread {
                toast("输入token信息")
            }
            return false;
        }

        val params = cookie.trim().split("&")

        for (param in params) {
            val split = param.split("=")
            Contacts.TOKEN_PARAMS.put(split[0], split[1])
        }

        if (TextUtils.isEmpty(Contacts.TOKEN_PARAMS.get("uid"))) {
            onUiThread {
                toast("获取用户uid失败")
            }
            return false;
        } else {
            Contacts.UID = Contacts.TOKEN_PARAMS.get("uid")!!
        }

        if (Contacts.TOKEN_PARAMS.get("pf") == "sq") {
            Contacts.BASE_URL = "https://zone1.ledou.qq.com/" + Contacts.DOMAIN
        } else if (Contacts.TOKEN_PARAMS.get("pf") == "wx2") {
            Contacts.BASE_URL = "https://zone4.ledou.qq.com/" + Contacts.DOMAIN
        } else if (Contacts.TOKEN_PARAMS.get("pf") == "wb") {
            Contacts.BASE_URL = "https://zone3.ledou.qq.com/" + Contacts.DOMAIN
        } else {
            onUiThread {
                toast("获取主机链接失败")
            }
            return false;
        }

        return true;
    }

    override fun onStart() {
        super.onStart()
        receiver = Receiver()
        registerReceiver(receiver, IntentFilter("proxyfinished"))
    }

    override fun onStop() {
        unregisterReceiver(receiver)
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: EventCenter) {
        if (event.eventCode == Contacts.CATCH_EVENT){
            toast("系统异常")
        }
    }

    inner class Receiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            installCert()
            Log.i("~~~~", "Receiver installCert")
        }
    }

    fun installCert() {
        val CERTIFICATE_RESOURCE = Environment.getExternalStorageDirectory().toString() + "/har/littleproxy-mitm.pem"
        val isInstallCert = SharedPreferenceUtils.getBoolean(this, "isInstallNewCert", false)
        if (!isInstallCert) {
            Toast.makeText(this, "必须安装证书才可实现HTTPS抓包", Toast.LENGTH_LONG).show()
            try {
                val keychainBytes: ByteArray

                var fis: FileInputStream? = null
                try {
                    fis = FileInputStream(CERTIFICATE_RESOURCE)
                    keychainBytes = ByteArray(fis.available())
                    fis.read(keychainBytes)
                } finally {
                    IOUtils.closeQuietly(fis)
                }

                val intent = KeyChain.createInstallIntent()
                intent.putExtra(KeyChain.EXTRA_CERTIFICATE, keychainBytes)
                intent.putExtra(KeyChain.EXTRA_NAME, "NetworkDiagnosis CA Certificate")
                startActivityForResult(intent, 3)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }


}
