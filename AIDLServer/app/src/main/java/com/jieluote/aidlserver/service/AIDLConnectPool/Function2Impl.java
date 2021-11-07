package com.jieluote.aidlserver.service.AIDLConnectPool;

import android.os.RemoteException;
import com.jieluote.aidlserver.arithmetic.IFunction2;

public class Function2Impl extends IFunction2.Stub {

    @Override
    public int subtractNum(int one, int two) throws RemoteException {
        return one - two;
    }
}
