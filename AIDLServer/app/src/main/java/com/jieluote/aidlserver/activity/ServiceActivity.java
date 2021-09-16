package com.jieluote.aidlserver.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.jieluote.aidlserver.R;
import com.jieluote.aidlserver.service.TestService;
import androidx.appcompat.app.AppCompatActivity;

/**
 * 本Activity主要演示了使用Service(继承binder的方式)的使用情况:
 * 1.start方式
 * 2.bind方式
 *
 * auth:jieluote
 */

public class ServiceActivity extends AppCompatActivity implements View.OnClickListener{
    public final static String TAG = ServiceActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);
        Button startServiceBtn = findViewById(R.id.start_service_btn);
        Button stopServiceBtn = findViewById(R.id.stop_service_btn);
        Button bindServiceBtn = findViewById(R.id.bind_service_btn);
        Button unbindServiceBtn = findViewById(R.id.unbind_service_btn);
        Button backBtn = findViewById(R.id.back_btn);
        startServiceBtn.setOnClickListener(this);
        stopServiceBtn.setOnClickListener(this);
        bindServiceBtn.setOnClickListener(this);
        unbindServiceBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back_btn:
                finish();
                break;
            case R.id.start_service_btn:
                Intent startIntent = new Intent(ServiceActivity.this, TestService.class);
                startService(startIntent);
                break;
            case R.id.stop_service_btn:
                Intent stopIntent = new Intent(ServiceActivity.this, TestService.class);
                stopService(stopIntent);
                break;
            case R.id.bind_service_btn:
                Intent bindIntent = new Intent(ServiceActivity.this, TestService.class);
                boolean isBind = bindService(bindIntent, mBindConnection, BIND_AUTO_CREATE);
                if(isBind){
                    Log.d(TAG,"bind service success");
                }else{
                    Log.d(TAG,"bind service failed");
                }
                break;
            case R.id.unbind_service_btn:
                unbindService(mBindConnection);
                break;
        }
    }

    private ServiceConnection mBindConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            Log.d(TAG, "onServiceConnected:"+name);
            TestService.TestBinder mBinder = (TestService.TestBinder) binder;
            mBinder.action();
        }
        //unbind时并不会被调用,当service所在进程crash或者被kill意外中断时才会被调用
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected:"+name);
        }
    };
}