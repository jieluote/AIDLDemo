package com.jieluote.aidlclient.activity;

import android.os.Bundle;
import android.os.ConditionVariable;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jieluote.aidlclient.AIDLPool;
import com.jieluote.aidlclient.R;
import com.jieluote.aidlserver.arithmetic.IFunction1;
import com.jieluote.aidlserver.arithmetic.IFunction2;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static com.jieluote.aidlclient.PublicConstant.TAG;

/**
 * 本Activity演示AIDL连接池的使用,这是一个自定义的功能,类似于中转站(门面模式),
 * 只通过一个service获取想要的binder对象,避免server端过多的service实现
 */
public class AIDLConnectPoolActivity extends AppCompatActivity implements View.OnClickListener{
    
    private EditText mNum1Tv;
    private EditText mNum2Tv;
    private TextView mResultTv;

    private IFunction1 mAddFunction;
    private IFunction2 mSubtractFunction;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aidl_connect_pool);
        initAIDL();
        Button backBtn = findViewById(R.id.back_btn);
        Button addBtn = findViewById(R.id.add_btn);
        Button subtractBtn = findViewById(R.id.subtract_btn);
        mNum1Tv = findViewById(R.id.num1_tv);
        mNum2Tv = findViewById(R.id.num2_tv);
        mResultTv = findViewById(R.id.result_tv);

        backBtn.setOnClickListener(this);
        addBtn.setOnClickListener(this);
        subtractBtn.setOnClickListener(this);
    }

    private void initAIDL() {
        Log.d(TAG, "start initAIDL");
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        AIDLPool aidlPool = AIDLPool.getInstance();
                        if (aidlPool.connectAIDLPoolService(AIDLConnectPoolActivity.this)) {
                            Log.d(TAG, "connect AIDLPoolService success");
                            IBinder addBinder = aidlPool.queryBinder(AIDLPool.ARITHMETIC_ADD_CODE);
                            mAddFunction = IFunction1.Stub.asInterface(addBinder);
                            IBinder subtractBinder = aidlPool.queryBinder(AIDLPool.ARITHMETIC_SUBTRACT_CODE);
                            mSubtractFunction = IFunction2.Stub.asInterface(subtractBinder);
                        }
                    }
                }
        ).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_btn:
                finish();
                break;
            case R.id.add_btn:
                String num1Str = mNum1Tv.getText().toString();
                String num2Str = mNum2Tv.getText().toString();
                try {
                    int result = mAddFunction.addNum(Integer.valueOf(num1Str),Integer.valueOf(num2Str));
                    mResultTv.setText(String.valueOf(result));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.subtract_btn:
                String num1 = mNum1Tv.getText().toString();
                String num2 = mNum2Tv.getText().toString();
                try {
                    int result = mSubtractFunction.subtractNum(Integer.valueOf(num1),Integer.valueOf(num2));
                    mResultTv.setText(String.valueOf(result));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }
}
