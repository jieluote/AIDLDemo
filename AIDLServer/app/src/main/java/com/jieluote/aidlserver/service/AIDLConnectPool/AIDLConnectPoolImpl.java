package com.jieluote.aidlserver.service.AIDLConnectPool;

import android.os.IBinder;
import android.os.RemoteException;

import com.jieluote.aidlserver.IAIDLConnectPool;
import com.jieluote.aidlserver.arithmetic.IFunction1;

public class AIDLConnectPoolImpl extends IAIDLConnectPool.Stub {

    public static final int ARITHMETIC_ADD_CODE = 100;
    public static final int ARITHMETIC_SUBTRACT_CODE = 200;

    @Override
    public IBinder queryBinder(int binderCode) throws RemoteException {
        IBinder binder = null;
        switch (binderCode){
            case ARITHMETIC_ADD_CODE:
                binder = new Function1Impl();
                break;
            case ARITHMETIC_SUBTRACT_CODE:
                binder = new Function2Impl();
                break;
            default:
                break;
        }
        return binder;
    }
}
