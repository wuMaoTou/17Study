package com.maotou.bspatch

/**
 * Created by wuchundu on 18-4-11.
 */
object BsPatch {

    init {
        System.loadLibrary("bspatch")
    }

    external fun applyPatch(oldApk: String, newApk: String, patch: String): Int
}
