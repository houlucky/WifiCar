package com.houxy.wificar.i;

/**
 * Created by Houxy on 2016/11/23.
 */

public interface OnSendMessageListener {
    void onSuccess(byte[] s);
    void onFailed(Exception e);
}
