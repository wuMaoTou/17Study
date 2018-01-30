package com.mt.ledou

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import com.mt.ledou.Bean.ResponseFilterRule
import com.mt.ledou.utils.DeviceUtils
import com.mt.ledou.utils.SharedPreferenceUtils
import net.lightbody.bmp.BrowserMobProxy
import net.lightbody.bmp.BrowserMobProxyServer
import net.lightbody.bmp.proxy.CaptureType
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by wuchundu on 18-1-25.
 */
class App : Application() {

    lateinit var proxy: BrowserMobProxy
    var ruleList: MutableList<ResponseFilterRule> = ArrayList<ResponseFilterRule>()

    override fun onCreate() {
        super.onCreate()
        instanceTmp = this
        initProxy()
    }

    companion object {

        var isInitProxy: Boolean? = false
        var proxyPort = 8888
        private var instanceTmp: App? = null

        val instance: App by lazy {
            instanceTmp!!
        }
    }

    fun initProxy() {
        try {
            FileUtils.forceMkdir(File(Environment.getExternalStorageDirectory().toString() + "/har"))
            //            FileUtils.cleanDirectory(new File(Environment.getExternalStorageDirectory() + "/har"));
            //            FileUtils.forceDelete(new File(Environment.getExternalStorageDirectory() + "/test.har"));
        } catch (e: IOException) {
            // test.har文件不存在
        }

        Thread(Runnable {
            startProxy()
            val intent = Intent()
            intent.action = "proxyfinished"
            sendBroadcast(intent)
        }).start()
    }

    fun startProxy() {
        try {
            proxy = BrowserMobProxyServer()
            proxy.setTrustAllServers(true)
            proxy.start(8888)
        } catch (e: Exception) {
            // 防止8888已被占用
            val rand = Random()
            val randNum = rand.nextInt(1000) + 8000
            proxyPort = randNum

            proxy = BrowserMobProxyServer()
            proxy.setTrustAllServers(true)
            proxy.start(randNum)
        }

        Log.e("~~~", "${proxy.getPort()}")


        val response = SharedPreferenceUtils.get(this.applicationContext, "response_filter")
        if (response != null && response is List<*>) {
            ruleList = response as MutableList<ResponseFilterRule>
        }

        val shp = PreferenceManager.getDefaultSharedPreferences(applicationContext)

        if (shp.getBoolean("enable_filter", false)) {
            Log.e("~~~enable_filter", "")
            initResponseFilter()
        }

        // 设置hosts
        if (shp.getString("system_host", "")!!.length > 0) {
            val advancedHostResolver = proxy.getHostNameResolver()
            for (temp in shp.getString("system_host", "")!!.split("\\n")) {
                if (temp.split(" ").size == 2) {
                    advancedHostResolver.remapHost(temp.split(" ")[1], temp.split(" ")[0])
                    Log.e("~~~~remapHost ", "${temp.split(" ")[1]} ${temp.split(" ")[0]}")
                }
            }
            proxy.setHostNameResolver(advancedHostResolver)
        }

        proxy.enableHarCaptureTypes(CaptureType.REQUEST_HEADERS, CaptureType.REQUEST_COOKIES,
                CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_HEADERS, CaptureType.REQUEST_COOKIES,
                CaptureType.RESPONSE_CONTENT)

        val time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
                .format(Date(System.currentTimeMillis()))
        proxy.newHar(time)
        isInitProxy = true
    }

    private fun initResponseFilter() {
        try {
            if (ruleList == null) {
                val rule = ResponseFilterRule()
                rule.url = "xw.qq.com/index.htm"
                rule.replaceRegex = "</head>"
                rule.replaceContent = "<script>alert('修改包测试')</script></head>"

                ruleList = ArrayList()
                ruleList.add(rule)
            }

            DeviceUtils.changeResponseFilter(this, ruleList)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onTerminate() {
        super.onTerminate()

        Thread(Runnable {
            Log.e("~~~", "onTerminate")
            proxy.stop()
        }).start()
    }
}