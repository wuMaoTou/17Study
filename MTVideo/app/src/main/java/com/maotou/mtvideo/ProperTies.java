package com.maotou.mtvideo;

import android.content.Context;

import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Administrator on 2017/7/31.
 */

public class ProperTies {

    public static Properties getProperties(Context c) {
        Properties urlProps;
        Properties props = new Properties();
        try {
            InputStream in = c.getAssets().open("appConfig");
            props.load(in);
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        urlProps = props;
        return urlProps;
    }
}