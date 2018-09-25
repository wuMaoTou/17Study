package com.maotou.multiimageuploaddemo;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSFederationCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSFederationToken;

import java.util.HashMap;

/**
 * Created by wuchundu on 2018/7/2.
 */

public class OSSClientUtil {

    private static OSSClientUtil ossClientUtil;
    public static final String endpoint = "oss-cn-hangzhou.aliyuncs.com";
    private OSSClient oss;

    private OSSClientUtil(){
        initOSS();
    }

    public static OSSClientUtil getInstance(){
        if (ossClientUtil == null){
            synchronized (Object.class){
                if (ossClientUtil == null){
                    ossClientUtil = new OSSClientUtil();
                }
            }
        }
        return ossClientUtil;
    }

    private void initOSS() {
        OSSCredentialProvider credetialProvider = getToken();
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(30 * 1000);
        conf.setSocketTimeout(30 * 1000);
        conf.setMaxConcurrentRequest(5);
        conf.setMaxErrorRetry(2);

        OSSLog.enableLog();
        oss = new OSSClient(App.getApp().getApplicationContext(), endpoint, credetialProvider);
    }

    public OSSClient getOSSClient(){
        if (oss == null){
            initOSS();
        }
        oss.updateCredentialProvider(getToken());
        return oss;
    }

    /**
     * 检查权限是否更新
     */
    private OSSCredentialProvider getToken() {
        return new OSSFederationCredentialProvider() {
            @Override
            public OSSFederationToken getFederationToken() {
                HashMap<String, String> hashUrl = new HashMap<>();
                hashUrl.put("token", "e8y5J7z9eeX7Nc02ZeWb02i2Odi7JchbbmRyb2lkIiwidmVyc2lvbiI6IjQuOS4wIiwibW9kZWwiOiJSZWRtaSAzUyIsInRpbWUiOjE1MzA0OTcxMjcsImlkIjoiMTAwOTU2IiwidG9rZW4iOiIyNjQzODAyN2Q5YzVmODZjZjg0NWU0ZTA3ZWUyODk5MiJ9");
                String response = Util.sendPost("https://dev.chezhency.com/Api490/Public/getsts", hashUrl);
                String data = JsonUtils.getData(response);
                OssMode mode = JsonUtils.getJson(data, OssMode.class);
                return new OSSFederationToken(mode.getAccessKeyId(), mode.getAccessKeySecret(), mode.getSecurityToken(), mode.getExpiration());
            }
        };
    }
}
