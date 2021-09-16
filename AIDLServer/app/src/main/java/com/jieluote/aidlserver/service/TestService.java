package com.jieluote.aidlserver.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import static com.jieluote.aidlserver.PublicConstant.TAG;

public class TestService extends Service {
    private TestBinder mBinder;

    public TestService() {
    }

    public class TestBinder extends Binder {
        public void action() {
            Log.d(TAG, "remote service TestBinder action");
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mBinder = new TestBinder();
        Log.d(TAG,"remote service onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"remote service onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "remote service onBind:" + mBinder);
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        boolean onUnbind = super.onUnbind(intent);
        Log.d(TAG,"remote service onUnbind:"+onUnbind);
        return onUnbind;
    }

    /**
     * onRebind调用时机:
     * （1）服务中onUnBind方法返回值为true
     * （2）服务对象被解绑后没有被销毁，之后再次被绑定
     */
    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.d(TAG,"remote service onRebind");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"remote service onDestroy");
    }
}