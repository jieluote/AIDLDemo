package com.jieluote.aidlclient.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;
import com.jieluote.aidlclient.R;
import com.jieluote.aidlclient.service.TestService;
import androidx.appcompat.app.AppCompatActivity;
import static com.jieluote.aidlclient.PublicConstant.TAG;
/**
 * 本Activity主要演示了使用Service(继承binder的方式)的使用情况:
 * 1.start方式
 * 2.bind方式
 * 这两种方式都支持启动/绑定  本地/远程service,
 * 只不过不能调用远程service的方法
 *
 * auth:jieluote
 */

public class ServiceActivity extends AppCompatActivity implements View.OnClickListener{
    private boolean isInterProcess;  //是否是跨进程，是跨进程时需要server端配合

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);
        Button backBtn = findViewById(R.id.back_btn);
        Button startServiceBtn = findViewById(R.id.start_service_btn);
        Button stopServiceBtn = findViewById(R.id.stop_service_btn);
        Button bindServiceBtn = findViewById(R.id.bind_service_btn);
        Button unbindServiceBtn = findViewById(R.id.unbind_service_btn);
        Switch interProcessSwitch = findViewById(R.id.inter_process_switch);
        interProcessSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isInterProcess = b;
            }
        });

        backBtn.setOnClickListener(this);
        startServiceBtn.setOnClickListener(this);
        stopServiceBtn.setOnClickListener(this);
        bindServiceBtn.setOnClickListener(this);
        unbindServiceBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.start_service_btn:
                Intent startIntent = getServiceIntent();
                try {
                    //注意,如果远程服务依赖的应用未启动,直接startService 会报 SecurityException, process is bad
                    startService(startIntent);
                } catch (Exception e) {
                    Log.d(TAG, "start service failed:" + e);
                }
                break;
            case R.id.stop_service_btn:
                Intent stopIntent = getServiceIntent();
                stopService(stopIntent);
                break;
            case R.id.bind_service_btn:
                Intent bindIntent = getServiceIntent();
                boolean isBind = bindService(bindIntent, mBindConnection, BIND_AUTO_CREATE);
                if (isBind) {
                    Log.d(TAG, "bind service success");
                } else {
                    Log.d(TAG, "bind service failed");
                }
                break;
            case R.id.unbind_service_btn:
                unbindService(mBindConnection);
                break;
        }
    }

    private Intent getServiceIntent(){
        Intent intent = new Intent();
        if(isInterProcess){
            intent.setAction("com.jieluote.aidlserver.service.TestService");
            intent.setPackage("com.jieluote.aidlserver");
        }else{
            intent.setClass(ServiceActivity.this, TestService.class);
        }
        return intent;
    }

    private ServiceConnection mBindConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            Log.d(TAG, "onServiceConnected:" + name);
            if (binder instanceof TestService.TestBinder) {
                TestService.TestBinder testBinder = (TestService.TestBinder) binder;
                testBinder.action();
            } else {
                String msg = "unsupport binder type,binder:" + binder.toString();
                Toast.makeText(ServiceActivity.this, msg, Toast.LENGTH_SHORT).show();
                Log.d(TAG, msg);
            }
        }

        //unbind时并不会被调用,当service所在进程crash或者被kill意外中断时才会被调用
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected:" + name);
        }
    };
}