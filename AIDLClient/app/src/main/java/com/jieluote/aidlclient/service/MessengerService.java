package com.jieluote.aidlclient.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import static com.jieluote.aidlclient.PublicConstant.TAG;

public class MessengerService extends Service {
    public static final int MSG_SEND_FROM_CLIENT = 100;
    public static final int MSG_SEND_FROM_SERVER = 200;

    private Messenger mMessenger;

    public MessengerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG,"onBind");
        mMessenger = new Messenger(new serverHandler(this));
        return mMessenger.getBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG,"onUnbind");
        return super.onUnbind(intent);
    }

    static class serverHandler extends Handler {
        private Context applicationContext;

        serverHandler(Context context) {
            applicationContext = context.getApplicationContext();
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //这里只能单线程处理client的消息
                case MSG_SEND_FROM_CLIENT:
                    Messenger clientMessenger = msg.replyTo;
                    Log.d(TAG, "local service receive msg from client:" + msg.getData().getString("messenger"));
                    Message serverMessage = Message.obtain(this, MSG_SEND_FROM_SERVER);
                    Bundle data = new Bundle();
                    data.putString("messenger", "local service send message to client .thread:"+Thread.currentThread().getName());
                    serverMessage.setData(data);
                    try {
                        clientMessenger.send(serverMessage);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy");
    }
}