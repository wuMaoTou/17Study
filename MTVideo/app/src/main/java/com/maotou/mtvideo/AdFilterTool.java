package com.maotou.mtvideo;

import android.content.Context;
import android.content.res.Resources;

/**
 * Created by wuchundu on 2018/11/21.
 */
public class AdFilterTool {
    public static boolean isAd(Context context, String url) {
        Resources res = context.getResources();
        String[] filterUrls = res.getStringArray(R.array.adUrls);
        for (String adUrl : filterUrls ) {
            if (url.contains(adUrl)) {
                return true;
            }
        }
        return false;
    }
}
