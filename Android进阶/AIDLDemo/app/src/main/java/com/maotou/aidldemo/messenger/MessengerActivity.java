package com.maotou.aidldemo.messenger;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.maotou.aidldemo.R;

import java.lang.ref.WeakReference;

import static com.maotou.aidldemo.messenger.MessengerService.MESSAGE_FROM_SERVICE;

/**
 * Created by wuchundu on 2018/11/29.
 */
public class MessengerActivity extends AppCompatActivity {

    private Messenger binder;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            binder = new Messenger(iBinder);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            binder = null;
        }
    };

    private static class MessengerHandler extends Handler {

        private WeakReference<Context> reference;

        MessengerHandler(Context context) {
            reference = new WeakReference<Context>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_FROM_SERVICE:
                    Bundle bundle = (Bundle) msg.obj;
                    String service = bundle.getString("service");
                    Toast.makeText(reference.get(), service, Toast.LENGTH_SHORT).show();
                    Log.d("tag", service);

                    break;
            }
        }
    }

    private Messenger messenger = new Messenger(new MessengerHandler(this));

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        findViewById(R.id.getlist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binder != null) {
                    try {
                        Message message = new Message();
                        message.what = MessengerService.MESSAGE_FROM_CLIENT;
                        Bundle bundle = new Bundle();
                        bundle.putString("client", "来自于客户端的消息");
                        message.obj = bundle;
                        message.replyTo = messenger;
                        binder.send(message);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binder != null) {

                }
            }
        });

        Intent intent = new Intent(this, MessengerService.class);
        bindService(intent,connection,Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        unbindService(connection);
        super.onDestroy();
    }
}
