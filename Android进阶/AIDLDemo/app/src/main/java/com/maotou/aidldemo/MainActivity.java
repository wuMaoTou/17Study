package com.maotou.aidldemo;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private IBookManager binder;
    private Random random;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            binder = IBookManager.Stub.asInterface(iBinder);
            if (binder != null) {
                try {
                    binder.addNewBookListaner(listener);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            binder = null;
        }
    };

    private IOnNewBookListener.Stub listener = new IOnNewBookListener.Stub() {
        @Override
        public void newBook(Book book) throws RemoteException {
            Toast.makeText(MainActivity.this, "添加了新书:  " + book.toString(), Toast.LENGTH_LONG).show();
        }
    };

    private IBinder.DeathRecipient recipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            if (binder != null) {
                binder.asBinder().unlinkToDeath(recipient, 0);
                binder = null;
                Intent intent = new Intent(MainActivity.this, BookService.class);
                bindService(intent, connection, BIND_AUTO_CREATE);
            }
        }
    };
    private List<Book> bookList;
    private List<Book> books;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.getlist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binder != null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                bookList = binder.getBookList();
                                MainActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(MainActivity.this, bookList.toString(), Toast.LENGTH_LONG).show();
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            }
        });

        random = new Random();
        findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binder != null) {
                    try {
                        int i = random.nextInt();
                        books = binder.addBook(new Book(i, "新添加的书" + i));
                        binder.asBinder().linkToDeath(recipient, 0);
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, books.toString(), Toast.LENGTH_LONG).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        Intent intent = new Intent(this, BookService.class);
        bindService(intent, connection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        if (binder != null && binder.asBinder().isBinderAlive()){
            try {
                binder.removeNewBookListener(listener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        unbindService(connection);
        super.onDestroy();
    }
}
