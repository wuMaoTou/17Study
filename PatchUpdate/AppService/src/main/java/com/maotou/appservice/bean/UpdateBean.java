package com.maotou.appservice.bean;

/**
 * Created by lichun on 18-4-16.
 */
public class UpdateBean extends BaseBean{

    private Integer id;

    /**
     * 旧apk的md5值
     */
    private String md5value;

    /**
     * 旧版本号
     */
    private Integer versionCode;

    /**
     * 旧版本名称
     */
    private String versionName;

    /**
     * 新版本号
     */
    private Integer newVersionCode;

    /**
     * 新版本名称
     */
    private String newVersionName;

    /**
     * 文件总大小
     */
    private Long fileSize;

    /**
     * patch包大小
     */
    private Long patchSize;

    /**
     * 下载地址
     */
    private String downloadPath;

    /**
     * patch下载包地址
     */
    private String patchDownloadPath;

    /**
     * 渠道标识
     */
    private String channelId;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMd5value() {
        return md5value;
    }

    public void setMd5value(String md5value) {
        this.md5value = md5value;
    }

    public Integer getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(Integer versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public Integer getNewVersionCode() {
        return newVersionCode;
    }

    public void setNewVersionCode(Integer newVersionCode) {
        this.newVersionCode = newVersionCode;
    }

    public String getNewVersionName() {
        return newVersionName;
    }

    public void setNewVersionName(String newVersionName) {
        this.newVersionName = newVersionName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public Long getPatchSize() {
        return patchSize;
    }

    public void setPatchSize(Long patchSize) {
        this.patchSize = patchSize;
    }

    public String getDownloadPath() {
        return downloadPath;
    }

    public void setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
    }

    public String getPatchDownloadPath() {
        return patchDownloadPath;
    }

    public void setPatchDownloadPath(String patchDownloadPath) {
        this.patchDownloadPath = patchDownloadPath;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }
}
