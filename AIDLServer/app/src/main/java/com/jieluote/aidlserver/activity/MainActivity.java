package com.jieluote.aidlserver.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.jieluote.aidlserver.R;

/**
 * 本应用是跨进程通信C/S中的Server端
 * 创建了绑定服务三种定义接口方式
 * (也可以理解为访问服务的三种方式):
 * 1.继承Binder类    本应用内
 * 2.使用Messenger   跨进程 - 单线程
 * 3.使用AIDL        跨进程 - 多线程
 * 我们可以了解到Service的生命周期及这三种方式的异同，
 * 但不会去介绍这三个内部的实现细节,更多的是从使用的角度上
 * 理解binder,理解跨进程通讯。
 *
 * 运行结果不反应在页面上,都打印在日志中(TEST_TAG),这样更方便查看整体流程
 * 注意:需要配合client端使用
 *
 * auth:jieluote
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button serviceBtn = findViewById(R.id.service_btn);
        Button messengerBtn = findViewById(R.id.messenger_btn);
        Button aidlBtn = findViewById(R.id.aidl_btn);

        serviceBtn.setOnClickListener(this);
        messengerBtn.setOnClickListener(this);
        aidlBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.service_btn:
                Intent serviceIntent = new Intent(MainActivity.this, ServiceActivity.class);
                startActivity(serviceIntent);
                break;
            case R.id.messenger_btn:
                Intent msgIntent = new Intent(MainActivity.this, MessengerActivity.class);
                startActivity(msgIntent);
                break;
            case R.id.aidl_btn:
                Intent aidlIntent = new Intent(MainActivity.this, AIDLActivity.class);
                startActivity(aidlIntent);
                break;
            default:
                break;
        }
    }
}