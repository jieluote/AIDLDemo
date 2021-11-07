package com.jieluote.aidlserver.service.AIDLConnectPool;

import android.os.RemoteException;
import com.jieluote.aidlserver.arithmetic.IFunction1;

public class Function1Impl extends IFunction1.Stub {
    @Override
    public int addNum(int one, int two) throws RemoteException {
        return one + two;
    }
}
