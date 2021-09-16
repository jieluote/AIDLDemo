package com.jieluote.aidlclient.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.jieluote.aidlclient.R;
import com.jieluote.aidlserver.IAidlInterface;
import com.jieluote.aidlserver.IOEventCallBack;
import androidx.appcompat.app.AppCompatActivity;
import static com.jieluote.aidlclient.PublicConstant.TAG;
/**
 * 本Activity主要演示了使用AIDL发送消息的3种情况:
 * 1.client给server端发送普通消息
 * 2.client给server端发送带返回值消息
 * 3.client给server端发送异步回调消息
 * 需要配合server端使用
 *
 * 创建AIDL服务步骤:
 * 1.在server端创建.aidl文件 IAidlInterface.aidl(名字任意),定义好需要实现的方法。
 * 2.编译后IDE自动生成同名的 IAidlInterface.java文件(原理是调用了sdk中的 aidl.exe),位于app/build/generated/aidl_source_output_dir下
 *  这是一个接口,里面包含了静态抽象内部类Stub(继承了Binder同时也实现了IAidlInterface)
 *  主要包含了三个方法asInterface(IBinder obj),asBinder();onTransact(...)和一个静态内部类Proxy
 * 3.新建继承Service的服务类,在里面实例化Stub类,实现好定义的方法(具体逻辑),然后将实例返回给Service的onBind()方法。
 * 4.把.aidl拷贝到client端,包名全路径需要一致(这样才保证Client端无感知调用)
 * 5.client bind服务后, 在serviceConnection onServiceConnected方法回调中执行asInterface(binder)获得接口对象,
 * 可以使用server端的各个方法，至此AIDL流程创建完毕。
 * 注意:IPC调用是同步的 也就是可能会阻塞
 *
 * auth:jieluote
 */
public class AIDLActivity extends AppCompatActivity implements View.OnClickListener{
    private IAidlInterface iRemoteService;
    private boolean isAlreadyBind;
    private boolean isAlreadyBindAsync;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aidl);
        Button backBtn = findViewById(R.id.back_btn);
        Button bindServiceBtn = findViewById(R.id.bind_service_btn);
        Button sendMsgBtn = findViewById(R.id.send_msg_btn);
        Button sendMoreMsgBtn = findViewById(R.id.send_more_msg_btn);
        Button sendReturnMsgBtn = findViewById(R.id.send_return_msg_btn);
        Button sendCallbackMsgBtn = findViewById(R.id.send_callback_msg_btn);
        Button unbindServiceBtn = findViewById(R.id.unbind_service_btn);
        Button testBlockBtn = findViewById(R.id.test_block_btn);

        backBtn.setOnClickListener(this);
        bindServiceBtn.setOnClickListener(this);
        sendMsgBtn.setOnClickListener(this);
        sendMoreMsgBtn.setOnClickListener(this);
        sendReturnMsgBtn.setOnClickListener(this);
        sendCallbackMsgBtn.setOnClickListener(this);
        unbindServiceBtn.setOnClickListener(this);
        testBlockBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back_btn:
                finish();
                break;
            case R.id.bind_service_btn:
                bindService();
                break;
            case R.id.send_msg_btn:
                sendMessage();
                break;
            case R.id.send_more_msg_btn:
                sendMoreMessage();
                break;
            case R.id.send_return_msg_btn:
                sendReturnMessage();
                break;
            case R.id.send_callback_msg_btn:
                sendCallbackMessage();
                break;
            case R.id.unbind_service_btn:
                unbindService();
                break;
            case R.id.test_block_btn:
                Toast.makeText(this, "--test block--", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected:"+name);
            iRemoteService = IAidlInterface.Stub.asInterface(service);
        }
        //unbind时并不会被调用,当service所在进程crash或者被kill意外中断时才会被调用
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected:"+name);
            iRemoteService = null;
        }
    };

    private ServiceConnection mCallBackMsgConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "on service connected:"+name);
            iRemoteService = IAidlInterface.Stub.asInterface(service);
            try {
                //注册回调
                iRemoteService.registerCallback(callBack);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            iRemoteService.sendCallBackMsg(1, "[client send callback msg to server at " + Thread.currentThread().getName()+"]");
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        //unbind时并不会被调用,当service所在进程crash或者被kill意外中断时才会被调用
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected:"+name);
            try {
                //注销回调
                iRemoteService.unregisterCallback(callBack);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            iRemoteService = null;
        }
    };

    private void bindService() {
        Intent bindIntent = new Intent();
        bindIntent.setAction("com.jieluote.aidlserver.service.AIDLService");
        bindIntent.setPackage("com.jieluote.aidlserver");

        boolean isBind = bindService(bindIntent, mServiceConnection, BIND_AUTO_CREATE);
        isAlreadyBind = isBind;
        if (isBind) {
            Log.d(TAG, "bind remote service success");
        } else {
            Log.d(TAG, "bind remote service failed");
        }
    }

    private void bindCallBackService() {
        Intent bindIntent = new Intent();
        bindIntent.setAction("com.jieluote.aidlserver.service.AIDLService");
        bindIntent.setPackage("com.jieluote.aidlserver");
        boolean isBind = bindService(bindIntent, mCallBackMsgConnection, BIND_AUTO_CREATE);
        isAlreadyBindAsync = isBind;
        if (isBind) {
            Log.d(TAG, "bind remote service success");
        } else {
            Log.d(TAG, "bind remote service failed");
        }
    }

    private void unbindService() {
        if (isAlreadyBind) {
            unbindService(mServiceConnection);
            isAlreadyBind = false;
        }
        if (isAlreadyBindAsync) {
            unbindService(mCallBackMsgConnection);
            isAlreadyBindAsync = false;
        }
    }

    /**
     * 发送普通消息
     */
    private void sendMessage() {
        if(!checkRemoteService()){
            return;
        }
        Log.d(TAG, "client send msg to server at " + Thread.currentThread().getName());
        try {
            iRemoteService.sendMsg(0, "[client send msg to server at " + Thread.currentThread().getName() + "]");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送多个消息,可观察Server端Binder线程的接收情况
     */
    private void sendMoreMessage() {
        if(!checkRemoteService()){
            return;
        }
        for (int i = 0; i < 3; i++) {
            int finalI = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Log.d(TAG, "client-" + finalI + " send more msg to server at " + Thread.currentThread().getName());
                        iRemoteService.sendMsg(finalI, "[client send more msg to server at " + Thread.currentThread().getName() + "]");
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }, "client_Thread_" + i).start();
        }
    }

    /**
     * 发送返回值消息
     */
    private void sendReturnMessage() {
        try {
            if(!checkRemoteService()){
                return;
            }
            Log.d(TAG, "client send return msg to server");
            String result = iRemoteService.sendReturnMsg(0, "[client send return msg to server at " + Thread.currentThread().getName()+"]");
            Log.d(TAG, "client return value is :" + result);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送异步回调消息
     */
    private void sendCallbackMessage() {
        if (!isAlreadyBindAsync) {
            bindCallBackService();
        } else {
            //由于IPC是同步阻塞的,如果服务器端执行的是耗时任务,那么在消息返回时请求者会被阻塞
            //所以这里需要在子线程中发送回调消息
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        iRemoteService.sendCallBackMsg(1, "[client send callback msg to server at " + Thread.currentThread().getName() + "]");
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    private IOEventCallBack callBack = new IOEventCallBack.Stub() {
        @Override
        public void handleIoEvent(boolean result) throws RemoteException {
            Log.d(TAG, "client receive msg from server handle result:" + result);
        }
    };

    private boolean checkRemoteService() {
        if (iRemoteService == null) {
            Toast.makeText(this, "please bind service first", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}