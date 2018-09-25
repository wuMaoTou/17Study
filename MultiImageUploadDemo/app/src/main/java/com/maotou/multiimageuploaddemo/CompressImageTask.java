package com.maotou.multiimageuploaddemo;


import com.maotou.multiimageuploaddemo.compress.CompressUtils;

import java.io.File;

/**
 * Created by wuchundu on 2018/7/6.
 */
public class CompressImageTask extends AbsTask<String, File> {
    @Override
    public File execut(String s, OnThreadResultListener listener) throws Exception {
        return CompressUtils.compressByLuban(s);
    }
}
