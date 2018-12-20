package com.maotou.aidldemo.binderpool;

import android.os.IBinder;
import android.os.RemoteException;

/**
 * Created by wuchundu on 2018/12/4.
 * 根据binderCode获取对应业务的Binder
 */
public class BinderPoolImpl extends IBinderPool.Stub {

    public static final int BINDER_NONE = -1;
    public static final int BINDER_COMPUTE = 0;
    public static final int BINDER_SECURITY_CENTER = 1;

    @Override
    public IBinder queryBinder(int binderCode) throws RemoteException {

        IBinder binder = null;

        switch (binderCode) {
            case BINDER_COMPUTE:
                binder = new ComputeImpl();
                break;
            case BINDER_SECURITY_CENTER:
                binder = new SecurityCenterImpl();
                break;
            default:
                break;
        }

        return binder;
    }
}
