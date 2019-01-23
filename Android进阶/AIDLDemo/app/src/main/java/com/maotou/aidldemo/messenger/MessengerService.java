package com.maotou.aidldemo.messenger;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.lang.ref.WeakReference;

/**
 * Created by wuchundu on 2018/11/29.
 */
public class MessengerService extends Service {

    public static final int MESSAGE_FROM_CLIENT = 101;
    public static final int MESSAGE_FROM_SERVICE = 102;

    private static class MessengerHandler extends Handler {

        private WeakReference<Context> reference;

        MessengerHandler(Context context) {
            reference = new WeakReference<Context>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_FROM_CLIENT:
                    Bundle bundle = (Bundle) msg.obj;
                    String client = bundle.getString("client");
                    Toast.makeText(reference.get(), client, Toast.LENGTH_SHORT).show();
                    Log.d("tag", client);

                    try {
                        Messenger messenger = msg.replyTo;
                        Message message = new Message();
                        message.what = MessengerService.MESSAGE_FROM_SERVICE;
                        Bundle rmsg = new Bundle();
                        rmsg.putString("service", "收到了待会回复你");
                        message.obj = rmsg;
                        messenger.send(message);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    private Messenger messenger = new Messenger(new MessengerHandler(this));

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }
}
