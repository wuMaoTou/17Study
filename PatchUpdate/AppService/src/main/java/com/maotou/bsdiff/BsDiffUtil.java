package com.maotou.bsdiff;

/**
 * Created by lichun on 18-4-11.
 */
public class BsDiffUtil {

    static{
        System.loadLibrary("bsdiff");
    }

    private static BsDiffUtil instance;

    private BsDiffUtil(){}

    public static BsDiffUtil getInstance(){
        if (instance == null){
            synchronized (BsDiffUtil.class){
                if (instance == null){
                    instance = new BsDiffUtil();
                }
            }
        }
        return instance;
    }

    public native int bsDiffFile(String oldFile, String newFile, String patchFile);

}
