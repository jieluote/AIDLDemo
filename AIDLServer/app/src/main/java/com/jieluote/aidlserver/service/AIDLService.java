package com.jieluote.aidlserver.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;
import com.jieluote.aidlserver.IAidlInterface;
import com.jieluote.aidlserver.IOEventCallBack;
import static com.jieluote.aidlserver.PublicConstant.TAG;

public class AIDLService extends Service {
    final RemoteCallbackList <IOEventCallBack> mCallbacks = new RemoteCallbackList <IOEventCallBack>();

    public AIDLService() {
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

    private final IAidlInterface.Stub mBinder = new IAidlInterface.Stub() {

        @Override
        public void sendMsg(int number, String content) throws RemoteException {
            Log.d(TAG, "server receive msg from client-" + number + ",content:" + content + " at thread:" + Thread.currentThread().getName());
        }

        @Override
        public String sendReturnMsg(int number, String content) throws RemoteException {
            Log.d(TAG, "server receive return msg from client" + ",content:" + content + " at thread:" + Thread.currentThread().getName());
            return "return msg from server_" + Thread.currentThread().getName();
        }

        @Override
        public void sendCallBackMsg(int number, String content) throws RemoteException {
            Log.d(TAG, "server receive async callBack Msg from client,number:" + number + " at thread:" + Thread.currentThread().getName());
            try {
                //模拟耗时任务
                Log.d(TAG, "handle io event process...");
                Thread.sleep(5000);
                Log.d(TAG, "handle io event finished");
                callback(true);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void registerCallback(IOEventCallBack callBack) throws RemoteException {
            if(callBack != null){
                mCallbacks.register(callBack);
            }
        }

        @Override
        public void unregisterCallback(IOEventCallBack callBack) throws RemoteException {
            if(callBack != null){
                mCallbacks.unregister(callBack);
            }
        }
    };

    void callback(boolean result) {
        final int count = mCallbacks.beginBroadcast();
        for (int i = 0; i < count; i++) {
            try {
                mCallbacks.getBroadcastItem(i).handleIoEvent(result);
            } catch (RemoteException e) {
                // The RemoteCallbackList will take care of removing
                // the dead object for us.
            }
        }
        mCallbacks.finishBroadcast();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"remote service onDestroy");
    }
}