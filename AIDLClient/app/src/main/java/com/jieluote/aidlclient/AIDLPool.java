package com.jieluote.aidlclient;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.ConditionVariable;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import com.jieluote.aidlserver.IAIDLConnectPool;
import static com.jieluote.aidlclient.PublicConstant.TAG;

public class AIDLPool {
    public static AIDLPool sAIDLPool;
    public IAIDLConnectPool mAIDLConnectPool;
    public static final int ARITHMETIC_ADD_CODE = 100;
    public static final int ARITHMETIC_SUBTRACT_CODE = 200;
    private ConditionVariable mConditionVariable = new ConditionVariable();

    private AIDLPool(){}

    public static AIDLPool getInstance(){
        if(sAIDLPool == null){
            synchronized (AIDLPool.class){
                if(sAIDLPool == null){
                    sAIDLPool = new AIDLPool();
                }
            }
        }
        return sAIDLPool;
    }

    public boolean connectAIDLPoolService(Context context){
        Intent bindIntent = new Intent();
        bindIntent.setAction("com.jieluote.aidlserver.service.AIDLConnectPool.AIDLConnectPoolService");
        bindIntent.setPackage("com.jieluote.aidlserver");
        boolean isBind = context.bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        Log.d(TAG, "AIDLConnectPoolService isBind:"+isBind);
        //onServiceConnected 过程会有延迟,所以需要等待
        mConditionVariable.block();
        mConditionVariable.close();
        return isBind;
    }

    public IBinder queryBinder(int binderCode){
        IBinder binder = null;
        try {
            if(mAIDLConnectPool != null){
                binder = mAIDLConnectPool.queryBinder(binderCode);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return binder;
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected:"+name);
            mAIDLConnectPool = IAIDLConnectPool.Stub.asInterface(service);
            //onServiceConnected 连接成功,解除等待
            mConditionVariable.open();
        }
        //unbind时并不会被调用,当service所在进程crash或者被kill意外中断时才会被调用
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected:"+name);
            mAIDLConnectPool = null;
        }
    };
}
