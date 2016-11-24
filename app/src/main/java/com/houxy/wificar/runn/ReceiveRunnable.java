package com.houxy.wificar.runn;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.houxy.wificar.C;

import java.io.BufferedInputStream;
import java.io.InputStream;

import static com.houxy.wificar.C.COMMAND_PERFIX;
import static com.houxy.wificar.C.MSG_ID_CON_READ;

/**
 * Created by Houxy on 2016/11/16.
 */

public abstract class ReceiveRunnable implements Runnable{

    private InputStream mInputStream;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case MSG_ID_CON_READ:
//                    byte[] command = (byte[]) msg.obj;
////                    mTvLog.setText("handle response from router: " + command.toString() );
////                    handleCallback(command);
//                    onMessageReceived(command);
//                    break;
//                default:
//                    break;
//            }
            if( MSG_ID_CON_READ == msg.what){
                byte[] command = (byte[]) msg.obj;
//                    mTvLog.setText("handle response from router: " + command.toString() );
//                    handleCallback(command);
                onMessageReceived(command);
            }
        }
    };


    public ReceiveRunnable(InputStream inputStream){
        mInputStream = inputStream;
    }

    @Override
    public void run() {
        boolean threadFlag = true;
        BufferedInputStream is = null;
        is = new BufferedInputStream(mInputStream);
        byte[] buffer = new byte[256];
        long lastTicket = System.currentTimeMillis();
        byte[] command = {0, 0, 0, 0, 0};
        int commandLength = 0;
        int i = 0;
        while (threadFlag) {
            try {
                //从输入流中读取一定数量的字节，并将其存储在缓冲区数组 buffer 中。以整数形式返回实际读取的字节数
                int ret = is.read(buffer);
                if (ret > 0) {
                    //把接收到的命令打印出来
                    printRecBuffer("receive buffer", buffer, ret);
                    if (ret > 0 && ret <= C.COMMAND_LENGTH) {
                        long newTicket = System.currentTimeMillis();
                        long ticketInterval = newTicket - lastTicket;
                        Log.d("Socket", "time ticket interval =" + ticketInterval);

                        if (ticketInterval < C.MIN_COMMAND_REC_INTERVAL) {
                            if (commandLength > 0) {
                                commandLength = appendBuffer(buffer, ret, command, commandLength);
                            } else {
                                Log.d("Socket", "not recognized command-1");
                            }
                        } else {
                            //命令的字首
                            if (buffer[0] == COMMAND_PERFIX) {
                                for (i = 0; i < ret; i++) {
                                    command[i] = buffer[i];
                                }
                                commandLength = ret;
                            } else {
                                Log.d("Socket", "not recognized command-2");
                                commandLength = 0;
                            }
                        }

                        lastTicket = newTicket;
                        printRecBuffer("print command", command, commandLength);
                        if (commandLength >= C.COMMAND_LENGTH) {
                            Message msg = new Message();
                            msg.what = MSG_ID_CON_READ;
                            msg.obj = command;
                            mHandler.sendMessage(msg);
                            commandLength = 0;
                        }
                    }
                }
            } catch (Exception e) {
                threadFlag = false;
            }
        }
    }

    private int appendBuffer(byte[] buffer, int len, byte[] dstBuffer, int dstLen) {
        int j = 0;
        int i;
        for (i = dstLen; i < C.COMMAND_LENGTH && j < len; i++) {
            dstBuffer[i] = buffer[j];
            j++;
        }
        return i;
    }

    private void printRecBuffer(String tag, byte[] buffer, int len) {
        StringBuilder sb = new StringBuilder();
        sb.append(tag);
        sb.append(" len = ");
        sb.append(len);
        sb.append(" :");
        for (int i = 0; i < len; i++) {
            sb.append(buffer[i]);
            sb.append(", ");
        }
        Log.i("Socket", sb.toString());
    }

    public abstract void onMessageReceived(byte[] data);

}
