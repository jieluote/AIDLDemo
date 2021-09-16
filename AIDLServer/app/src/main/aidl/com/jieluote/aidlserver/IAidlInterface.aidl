// IAidlInterface.aidl
package com.jieluote.aidlserver;
import com.jieluote.aidlserver.IOEventCallBack;
// Declare any non-default types here with import statements

interface IAidlInterface {
    /**
     *  1.不能使用private、public、protect修饰方法；
        2.支持传递数据类型有：java基本数据类型（byte、short、int、long、float、double、char、boolean）、String、CharSequence、List（接收方必须是ArrayList）、Map（接收方必须是HashMap）、其他AIDL定义的AIDL接口、实现Parcelable序列化的类；
        3.其他AIDL定义的AIDL接口和实现Parcelable序列化的类必须import，即使在相同包结构下，其余的类型不需要import；
        4.对于非基本数据类型，也不是String和CharSequence类型的，需要有方向指示，包括in、out和inout，in表示由客户端设置，out表示由服务端设置，inout是两者均可设置。
     */
    void sendMsg(int number, String content);

    String sendReturnMsg(int number, String content);

    void sendCallBackMsg(int number, String content);

    void registerCallback(IOEventCallBack callBack);

    void unregisterCallback(IOEventCallBack callBack);

}