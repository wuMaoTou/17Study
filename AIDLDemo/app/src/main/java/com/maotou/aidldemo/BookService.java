package com.maotou.aidldemo;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by wuchundu on 2018/11/29.
 */
public class BookService extends Service {

    private CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<>();
    private RemoteCallbackList<IOnNewBookListener> mListeners = new RemoteCallbackList<>();

    private IBookManager.Stub binder = new IBookManager.Stub() {

        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            int check = checkCallingOrSelfPermission("com.maotou.aidldemo.permission.ACCESS_BOOK_SERVICE");
            if (check == PackageManager.PERMISSION_DENIED){
                return false;
            }

            String packageName = null;
            String[] packages = getPackageManager().getPackagesForUid(getCallingUid());
            if (packages != null && packages.length > 0){
                packageName = packages[0];
            }
            if (!packageName.startsWith("com.maotou")){
                return false;
            }

            return super.onTransact(code, data, reply, flags);
        }

        @Override
        public List<Book> addBook(Book book) throws RemoteException {
            mBookList.add(book);
            notifyListener(book);
            return mBookList;
        }

        @Override
        public List<Book> getBookList() throws RemoteException {
            return mBookList;
        }

        @Override
        public void addNewBookListaner(IOnNewBookListener listener) throws RemoteException {
            mListeners.register(listener);
            Log.d("tag","addNewBookListaner Listener size -- " + mListeners.beginBroadcast());
            mListeners.finishBroadcast();
        }

        @Override
        public void removeNewBookListener(IOnNewBookListener listener) throws RemoteException {
            mListeners.unregister(listener);
            Log.d("tag","removeNewBookListener Listener size -- " + mListeners.beginBroadcast());
            mListeners.finishBroadcast();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mBookList.add(new Book(0, "如何阅读一本书"));
        mBookList.add(new Book(1, "安卓开发艺术探索"));
        mBookList.add(new Book(2, "安卓进阶之光"));
        mBookList.add(new Book(3, "安卓设计模式"));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
//        int check = checkCallingOrSelfPermission("com.maotou.aidldemo.permission.ACCESS_BOOK_SERVICE");
//        if (check == PackageManager.PERMISSION_DENIED){
//            return null;
//        }
        return binder;
    }

    private void notifyListener(Book book) throws RemoteException {
        final int l = mListeners.beginBroadcast();
        for (int i = 0; i < l; i++) {
            IOnNewBookListener broadcastItem = mListeners.getBroadcastItem(i);
            if (broadcastItem != null) {
                broadcastItem.newBook(book);
            }
        }
        mListeners.finishBroadcast();
    }
}
