package com.jieluote.aidlserver.activity;

import android.os.Bundle;
import android.view.View;

import com.jieluote.aidlserver.R;
import androidx.appcompat.app.AppCompatActivity;
import static com.jieluote.aidlserver.PublicConstant.TAG;

/**
 * 本Activity没有具体实现,只做说明
 * AIDL需要配合client端使用
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
public class AIDLActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aidl);
        View backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}