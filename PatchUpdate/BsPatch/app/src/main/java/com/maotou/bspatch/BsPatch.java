package com.maotou.bspatch;

/**
 * Created by wuchundu on 18-4-11.
 */
public class BsPatch {

    static{
        System.loadLibrary("bspatch");
    }

    public static native int applyPatch(String oldApk, String newApk, String patch);
}
