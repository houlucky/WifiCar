package com.houxy.wificar;

import android.util.Log;

import com.houxy.wificar.i.OnSendMessageListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

/**
 * Created by Houxy on 2016/11/15.
 */

public class SocketClient {
    private  Socket client = null;

    public SocketClient(final String site, final int port) {
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        client = new Socket(site, port);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(byte[] msg, OnSendMessageListener listener) {
        try {
            OutputStream out = client.getOutputStream();
            out.write(msg);
            out.flush();
            listener.onSuccess(msg);
            Log.d("TAG", "comm : " + Arrays.toString(msg));
        } catch (IOException e) {
            e.printStackTrace();
            listener.onFailed(e);
        }
    }


    public void closeSocket() {
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public InputStream getInputStream() {
        if (client != null) {
            try {
                return client.getInputStream();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public OutputStream getOutputStream() {
        if (client != null) {
            try {
                return client.getOutputStream();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}


