package com.maotou.bspatch_kotlin.httpservice.beans

/**
 * Created by lichun on 18-4-16.
 */
data class UpdateBean(

    /**
     * 旧apk的md5值
     */
    var md5value: String,

    /**
     * 旧版本号
     */
    var versionCode: Int,

    /**
     * 旧版本名称
     */
    var versionName: String,

    /**
     * 新版本号
     */
    var newVersionCode: Int,

    /**
     * 新版本名称
     */
    var newVersionName: String,

    /**
     * 文件总大小
     */
    var fileSize: Long,

    /**
     * patch包大小
     */
    var patchSize: Long,

    /**
     * 下载地址
     */
    var downloadPath: String,

    /**
     * patch下载包地址
     */
    var patchDownloadPath: String,

    /**
     * 渠道标识
     */
    var channelId: String
)
