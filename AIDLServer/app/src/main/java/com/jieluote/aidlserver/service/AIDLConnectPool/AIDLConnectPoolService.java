package com.jieluote.aidlserver.service.AIDLConnectPool;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import com.jieluote.aidlserver.IAidlInterface;
import com.jieluote.aidlserver.IOEventCallBack;

import static com.jieluote.aidlserver.PublicConstant.TAG;

public class AIDLConnectPoolService extends Service {
    private Binder mBinder = null;

    public AIDLConnectPoolService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
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
        if (mBinder == null) {
            mBinder = new AIDLConnectPoolImpl();
        }
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