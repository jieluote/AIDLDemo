package com.jieluote.aidlserver.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.jieluote.aidlserver.R;
import com.jieluote.aidlserver.service.MessengerService;
import androidx.appcompat.app.AppCompatActivity;
import static com.jieluote.aidlserver.service.MessengerService.MSG_SEND_FROM_SERVER;
import static com.jieluote.aidlserver.PublicConstant.TAG;

/**
 * 本Activity主要演示了使用messenger发送消息的情况:
 * 1.本应用内发送消息
 * 2.跨进程发送消息(需要client端配合)
 *
 * messenger适用于跨进程单线程的通信，其实现也是基于AIDL的
 * 需要配合handler使用,消息的传递都是封装在message里。
 *
 * auth:jieluote
 */
public class MessengerActivity extends AppCompatActivity implements View.OnClickListener{
    private Handler mServerHandler;
    private Messenger mServerMessenger;
    private Messenger mClientMessenger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);
        Button bindServiceBtn = findViewById(R.id.bind_service_btn);
        Button sendMsgBtn = findViewById(R.id.send_msg_btn);
        Button unbindServiceBtn = findViewById(R.id.unbind_service_btn);
        Button backBtn = findViewById(R.id.back_btn);

        bindServiceBtn.setOnClickListener(this);
        sendMsgBtn.setOnClickListener(this);
        unbindServiceBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);
        initServerHandler();
    }

    private void initServerHandler() {
        mServerHandler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_SEND_FROM_SERVER:
                        Log.d(TAG, "client receive server msg:" + msg.getData().getString("messenger"));
                        break;
                    default:
                        break;
                }
            }
        };
        mClientMessenger = new Messenger(mServerHandler);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.bind_service_btn:
                Intent bindIntent = new Intent(MessengerActivity.this, MessengerService.class);
                boolean isBind = bindService(bindIntent, mMessengerConnection, BIND_AUTO_CREATE);
                if (isBind) {
                    Log.d(TAG, "bind service success");
                } else {
                    Log.d(TAG, "bind service failed");
                }
                break;
            case R.id.send_msg_btn:
                sendMessage();
                break;
            case R.id.unbind_service_btn:
                unbindService(mMessengerConnection);
                break;
            default:
                break;
        }
    }

    private ServiceConnection mMessengerConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected:" + name);
            mServerMessenger = new Messenger(service);
        }

        //unbind时并不会被调用,当service所在进程crash或者被kill意外中断时才会被调用
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected:" + name);
            mServerMessenger = null;
        }
    };

    public void sendMessage() {
        Log.d(TAG, "client start sendMessage");
        Message message = Message.obtain(null, MessengerService.MSG_SEND_FROM_CLIENT, 0, 0);
        Bundle data = new Bundle();
        data.putString("messenger", "message from the client. id:" + this.hashCode());
        message.setData(data);
        message.replyTo = mClientMessenger;
        try {
            mServerMessenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}