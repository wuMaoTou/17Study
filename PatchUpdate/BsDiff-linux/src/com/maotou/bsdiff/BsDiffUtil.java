package com.maotou.bsdiff;

/**
 * Created by wuchundou on 18-4-11.
 */
public class BsDiffUtil {

    private static BsDiffUtil instance = new BsDiffUtil();

    private BsDiffUtil(){}

    public static BsDiffUtil getInstance(){
        return instance;
    }

    static{
        System.loadLibrary("bsdiff");
    }

    public native int bsDiffFile(String oldFile, String newFile, String patchFile);

}
