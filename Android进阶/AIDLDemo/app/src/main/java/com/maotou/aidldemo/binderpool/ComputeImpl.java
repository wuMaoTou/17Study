package com.maotou.aidldemo.binderpool;

import android.os.RemoteException;

/**
 * Created by wuchundu on 2018/12/4.
 */
public class ComputeImpl extends ICompute.Stub {
    @Override
    public int add(int a, int b) throws RemoteException {
        return a + b;
    }
}
