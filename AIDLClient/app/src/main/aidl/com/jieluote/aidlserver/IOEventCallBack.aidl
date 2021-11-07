// IOEventCallBack.aidl
package com.jieluote.aidlserver;

// 用于执行回调结果的aidl

interface IOEventCallBack {
    void handleIoEvent(boolean result);
}