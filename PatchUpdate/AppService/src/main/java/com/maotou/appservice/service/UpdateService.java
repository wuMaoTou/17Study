package com.maotou.appservice.service;

import com.maotou.appservice.bean.RestFulBean;
import com.maotou.appservice.bean.UpdateBean;
import com.maotou.appservice.dao.UpdateDao;
import com.maotou.appservice.util.CommonUtils;
import com.maotou.appservice.util.ResetFulUtil;
import com.maotou.bsdiff.BsDiffUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;

/**
 * Created by lichun on 18-4-16.
 */
@Transactional
public class UpdateService {

    @Autowired
    private UpdateDao updateDao;

    static String oldApksPath = "/usr/dev/sources/oldversion";
    static String newApksPath = "/usr/dev/sources/newversion";
    static String patchPath = "/usr/dev/sources/patch";

    static{
        if (CommonUtils.getOsName().contains("linux")){
            oldApksPath = "/usr/dev/sources/oldversion";
            newApksPath = "/usr/dev/sources/newversion";
            patchPath = "/usr/dev/sources/patch";
        }else if (CommonUtils.getOsName().contains("windows")){
            oldApksPath = "E:/sources/oldversion";
            newApksPath = "E:/sources/newversion";
            patchPath = "E:/sources/patch";
        }
    }

    public RestFulBean<UpdateBean> checkUpdate(String md5value, int versioncode, String channelId){
        UpdateBean updateInfo = updateDao.getUpdateInfo(md5value, versioncode, channelId);
        if (updateInfo != null){
            File file = new File(patchPath + "/" + updateInfo.getPatchDownloadPath());
            if (file.exists()){
                return ResetFulUtil.getInstance().getRestFulBean(updateInfo,0,"发现新版本");
            }
            //todo 不存在可以再生产增量包或删除此条记录
            updateDao.deleteUpdateInfo(updateInfo);
            return ResetFulUtil.getInstance().getRestFulBean(null,1,"没有新版本");
        }else{
            try {
                updateInfo = createPath(md5value, versioncode, channelId);
            }catch (Exception e){
                return ResetFulUtil.getInstance().getRestFulBean(null,1,"服务器异常" + e.toString());
            }
            if (updateInfo != null){
                updateDao.saveUpdateInfo(updateInfo);
                UpdateBean updateBean = updateDao.getUpdateInfo(md5value, versioncode, channelId);
                if (updateBean != null){
                    File file = new File(patchPath + "/" + updateBean.getPatchDownloadPath());
                    if (file.exists()){
                        return ResetFulUtil.getInstance().getRestFulBean(updateBean,0,"发现新版本");
                    }
                    updateDao.deleteUpdateInfo(updateBean);
                    return ResetFulUtil.getInstance().getRestFulBean(null,1,"没有新版本");
                }
            }
            return ResetFulUtil.getInstance().getRestFulBean(null,1,"没有新版本");
        }
    }

    private UpdateBean createPath(String md5value, int versioncode, String channelId) throws Exception{
        File md5File = null;
        File newFile = null;
        String patchName = "";
        File patchFile = null;
        String versionname = "";

        UpdateBean updateBean = null;
        int newVersionCode = 1;
        String newVersionName = "";

        File file = new File(oldApksPath);
        if (!file.exists()){
            return null;
        }
        File[] files = file.listFiles();
        if (files == null || file.length() == 0){
            return null;
        }

        for (File f : files) {
            String fileMd5 = CommonUtils.getFileMd5(f);
            if (fileMd5.equals(md5value)){
                System.out.println(f.getName() +" md5: "+ fileMd5);
                String[] flag = f.getName().replace(".apk", "").split("_");
                if (flag != null && flag.length == 4 && flag[3].equals(channelId)){
                    versionname = flag[2];
                    md5File = f;
                    break;
                }
            }
        }

        if (md5File == null){
            return null;
        }

        //根据渠道获取当前最新版本
        File nFile = new File(newApksPath);
        if (!nFile.exists()){
            return null;
        }
        File[] nFiles = nFile.listFiles();
        if (nFiles == null || nFiles.length == 0){
            return null;
        }
        for (File nf : nFiles) {
            String[] flag = nf.getName().replace(".apk", "").split("_");
            if (flag != null && flag.length == 4 && flag[3].equals(channelId)){
                System.out.println("渠道:"+ channelId +" 的当前最新版本"+ nf.getName());
                newFile = nf;
                newVersionCode = Integer.parseInt(flag[1]);
                newVersionName = flag[2];
                patchName = patchPath +"/"+ nf.getName().replace(".apk","") +"_patch_" + versioncode +".patch";
                break;
            }
        }

        if (newFile == null){
            return null;
        }

        System.out.println("oldfile:" + md5File.getAbsolutePath());
        System.out.println("newfile:" + newFile.getAbsolutePath());
        System.out.println("patchfile:" + patchName);
        System.out.println("library path:" + System.getProperty("java.library.path"));


        int result = BsDiffUtil.getInstance().bsDiffFile(md5File.getAbsolutePath(),newFile.getAbsolutePath(),patchName);

        if (result != 0){
            return null;
        }

        patchFile = new File(patchName);
        if (!patchFile.exists()){
            return null;
        }
        updateBean = new UpdateBean();

        updateBean.setMd5value(md5value);
        updateBean.setVersionCode(versioncode);
        updateBean.setVersionName(versionname);
        updateBean.setNewVersionCode(newVersionCode);
        updateBean.setNewVersionName(newVersionName);
        updateBean.setFileSize(md5File.length());
        updateBean.setPatchSize(patchFile.length());
        updateBean.setDownloadPath(newFile.getName());
        updateBean.setPatchDownloadPath(patchFile.getName());
        updateBean.setChannelId(channelId);

        return updateBean;
    }

}
