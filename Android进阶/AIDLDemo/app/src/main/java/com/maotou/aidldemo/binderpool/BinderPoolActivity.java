package com.maotou.aidldemo.binderpool;

import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.maotou.aidldemo.R;

/**
 * Created by wuchundu on 2018/12/4.
 */
public class BinderPoolActivity extends AppCompatActivity {

    private static final String TAG = "BinderPoolActivity";
    private BinderPool binderPool;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.getlist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        doWork();
                    }
                }).start();
            }
        });
    }

    private void doWork(){
        binderPool = BinderPool.getInstance(this);

        IBinder computBinder = binderPool.queryBinder(BinderPoolImpl.BINDER_COMPUTE);
        ICompute compute = ICompute.Stub.asInterface(computBinder);
        try {
            int sum = compute.add(1, 1);
            Log.d(TAG, "sum : " + sum);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        IBinder sBinder = binderPool.queryBinder(BinderPoolImpl.BINDER_SECURITY_CENTER);
        ISecurityCenter securityCenter = ISecurityCenter.Stub.asInterface(sBinder);
        try {
            String encrypt = securityCenter.encrypt("123456");
            Log.d(TAG, "encrypt : " + encrypt);
            String decrypt = securityCenter.decrypt(encrypt);
            Log.d(TAG, "decrypt : " + decrypt);

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
