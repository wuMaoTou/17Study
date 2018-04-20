package com.maotou.bspatch_kotlin

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.maotou.bspatch_kotlin.dialog.LoadDialog
import com.maotou.bspatch_kotlin.dialog.NormalDialog
import com.maotou.bspatch_kotlin.dialog.UpdateDialog
import com.maotou.bspatch_kotlin.httpservice.beans.UpdateBean
import com.maotou.bspatch_kotlin.httpservice.service.HttpMethod
import com.maotou.bspatch_kotlin.httpservice.serviceapi.UserApi
import com.maotou.bspatch_kotlin.httpservice.subscribers.HttpSubscriber
import com.maotou.bspatch_kotlin.httpservice.subscribers.SubscriberOnListener
import com.maotou.bspatch_kotlin.ui.UserServiceTestActivity
import com.maotou.bspatch_kotlin.util.ApkUtils
import com.maotou.bspatch_kotlin.util.CommonUtil
import com.maotou.bspatch_kotlin.util.SignUtils
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast
import java.io.File


class MainActivity : AppCompatActivity() {

    var loadDialog: LoadDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)
        }

        bt_user_test.setOnClickListener {
            startActivity(Intent(this,UserServiceTestActivity::class.java))
        }

        bt_bspatch.setOnClickListener{
            patch()
        }
    }

    fun patch(){
        showDialog("检查更新")
        val path = ApkUtils.getCurApkPath(this)
        Log.d("patch-path",path)
        val file = File(path)
        val md5 = SignUtils.getMd5ByFile(file)
        Log.d("patch-md5",md5)
        UserApi.instance.checkUpdate(md5,ApkUtils.getVersionCode(this),"xiaomi",
                HttpSubscriber<UpdateBean>(object : SubscriberOnListener<UpdateBean>{
            override fun onSucceed(data: UpdateBean) {
                hideLoadDialog()

                val normalDialog = NormalDialog(this@MainActivity)
                normalDialog.show()
                val updatestr = "有新版本：${data.newVersionName}\n完整大小：${CommonUtil.getFormatSize(data.fileSize.toDouble())}\n增量大小：${CommonUtil.getFormatSize(data.patchSize.toDouble())}"
                normalDialog.setData("更新提醒",updatestr,"暂不更新","立即更新",object : NormalDialog.OnNoOrYesListener{
                    override fun onNo() {
                        //TODO 暂不更新
                    }

                    override fun onYes() {

                        if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                            toast("没有读写权限")
                            return
                        }

                        try {
                            val baseDir = "/maotou/"
                            val newApkPath = CommonUtil.makeDir(baseDir)
                            val newApkFile = File("$newApkPath/${packageName}_${data.newVersionName}.apk")
                            if (newApkFile.exists()) {
                                if (newApkFile.length() == data.fileSize) {
                                    ApkUtils.installApk(this@MainActivity, newApkFile.absolutePath)
                                    return
                                }
                            } else {
                                newApkFile.createNewFile()
                            }

                            val newPatchPath = CommonUtil.makeDir("${baseDir}patch")
                            val newPatchFile = File("${newPatchPath}/${packageName}_patch_${data.newVersionCode}_${data.versionCode}.patch")
                            if (!newPatchFile.exists()) {
                                newPatchFile.createNewFile()
                            }
                            val updateDialog = UpdateDialog(this@MainActivity)
                            updateDialog.show()
                            updateDialog.setInfo(HttpMethod.Download_URL + data.patchDownloadPath, newPatchFile.absolutePath, newApkFile.absolutePath, data.patchSize, data.fileSize)
                        }catch (e:Exception){
                            e.printStackTrace()
                        }
                    }

                })
            }

            override fun onError(code: Int, msg: String) {
                hideLoadDialog()
                toast(msg)
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
