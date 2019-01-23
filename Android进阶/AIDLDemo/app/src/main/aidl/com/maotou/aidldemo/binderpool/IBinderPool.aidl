// IBinderPool.aidl
package com.maotou.aidldemo.binderpool;

// Declare any non-default types here with import statements

interface IBinderPool {
    IBinder queryBinder(int binderCode);
}
