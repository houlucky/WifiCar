package com.houxy.wificar.ui;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.houxy.wificar.C;
import com.houxy.wificar.R;
import com.houxy.wificar.SocketClient;
import com.houxy.wificar.runn.ReceiveRunnable;
import com.houxy.wificar.utils.NetUtil;
import com.houxy.wificar.utils.SPUtil;
import com.houxy.wificar.view.MJPEGView;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.houxy.wificar.C.CAMERA_VIDEO_URL;
import static com.houxy.wificar.C.COMM_BACKWARD;
import static com.houxy.wificar.C.COMM_FORWARD;
import static com.houxy.wificar.C.COMM_LEFT;
import static com.houxy.wificar.C.COMM_RIGHT;
import static com.houxy.wificar.C.COMM_STOP;
import static com.houxy.wificar.C.MSG_ID_CLEAR_QUIT_FLAG;
import static com.houxy.wificar.C.MSG_ID_CON_SUCCESS;
import static com.houxy.wificar.C.MSG_ID_ERR_CONN;
import static com.houxy.wificar.C.MSG_ID_ERR_INIT_READ;
import static com.houxy.wificar.C.MSG_ID_LOOP_END;
import static com.houxy.wificar.C.MSG_ID_LOOP_START;
import static com.houxy.wificar.C.MSG_ID_START_CHECK;
import static com.houxy.wificar.C.QUIT_BUTTON_PRESS_INTERVAL;
import static com.houxy.wificar.C.ROUTER_CONTROL_PORT;
import static com.houxy.wificar.C.ROUTER_CONTROL_URL;
import static com.houxy.wificar.C.STATUS_CONNECTED;
import static com.houxy.wificar.C.STATUS_INIT;
import static com.houxy.wificar.C.WIFI_STATE_CONNECTED;
import static com.houxy.wificar.C.WIFI_STATE_NOT_CONNECTED;

/**
 * Created by Houxy on 2016/11/17.
 */

public class ControlActivity extends AppCompatActivity{


    @BindView(R.id.mySurfaceView) MJPEGView mSurfaceView;
    @BindView(R.id.btnLeft) ImageButton mBtnLeft;
    @BindView(R.id.btnBackward) ImageButton mBtnBackward;
    @BindView(R.id.btnStop) ImageButton mBtnStop;
    @BindView(R.id.btnRight) ImageButton mBtnRight;
    @BindView(R.id.btnForward) ImageButton mBtnForward;
    @BindView(R.id.btnTakePic) ImageButton mBtnTakePic;
    @BindView(R.id.tvLog) TextView mTvLog;

    private boolean bHeartBreakFlag = false;
    private boolean bReadyToSendCmd = false;
    private boolean mQuitFlag = false;
    private int mWifiStatus = STATUS_INIT;

    private Context mContext;
    private SocketClient mTcpSocket;
    private Thread mThreadClient = null;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_ID_ERR_INIT_READ:
                    mTvLog.setText("打开监听失败！！");
                    break;
                case MSG_ID_CON_SUCCESS:
                    mTvLog.setText("成功连接到路由器!");
                    Message message = new Message();
                    message.what = MSG_ID_START_CHECK;
                    mHandler.sendMessageDelayed(message, 1000);
                case MSG_ID_START_CHECK:
                    mTvLog.setText("可以开始发送数据！！！");
                    bReadyToSendCmd = true;
                    break;
                case MSG_ID_ERR_CONN:
                    mTvLog.setText("连接路由器失败!");
                    break;
                case MSG_ID_CLEAR_QUIT_FLAG:
                    mQuitFlag = false;
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        initSettings();
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐去标题
        //应用的名字必须要写在setContentView之前，否则会有异常）
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置窗体全屏
        setContentView(R.layout.activity_control);
        ButterKnife.bind(this);
        initView();
        initListener();
        connectToRouter();
    }

    private void initListener() {

        View.OnTouchListener onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        sendCommand(v.getId());
                        break;
                    case MotionEvent.ACTION_UP:
                        sendCommand(COMM_STOP);
                        break;
                    default:break;
                }
                return false;
            }
        };

        mBtnBackward.setOnTouchListener(onTouchListener);
        mBtnForward.setOnTouchListener(onTouchListener);
        mBtnLeft.setOnTouchListener(onTouchListener);
        mBtnRight.setOnTouchListener(onTouchListener);
        mBtnStop.setOnTouchListener(onTouchListener);

    }

    private void initView() {

        mTvLog.setBackgroundColor(Color.argb(0, 0, 255, 0));
        mTvLog.setTextColor(Color.argb(90, 0, 255, 0));

    }

    private void sendCommand(int id){

        switch (id){
            case R.id.btnBackward:
                sendCommand(COMM_BACKWARD);
                break;
            case R.id.btnForward:
                sendCommand(COMM_FORWARD);
                break;
            case R.id.btnLeft:
                sendCommand(COMM_LEFT);
                break;
            case R.id.btnRight:
                sendCommand(COMM_RIGHT);
                break;
            case R.id.btnStop:
                sendCommand(COMM_STOP);
                break;
            default:break;
        }
    }

    private void sendCommand(byte[] data) {
        if (mWifiStatus != STATUS_CONNECTED || null == mTcpSocket) {
            mTvLog.setText(R.string.status_abnormal);
            return;
        }

        if (!bReadyToSendCmd) {
            mTvLog.setText(R.string.wait_to_send);
            return;
        }

        try {
            mTcpSocket.sendMsg(data);
        } catch (Exception e) {
            Log.i("Socket", e.getMessage() != null ? e.getMessage() : "sendCommand error!");
        }

    }

    private void connectToRouter() {
        int status = NetUtil.getWifiStatus(this);
        if (WIFI_STATE_CONNECTED == status) {
            initWifiConnection();
//            mThreadClient = new Thread(mRunnable);
            mThreadClient = new Thread(new ReceiveRunnable(mTcpSocket.getInputStream()) {
                @Override
                public void onMessageReceived(byte[] data) {
                    System.out.print(Arrays.toString(data));
                }
            });
            mThreadClient.start();
            String cameraUrl = CAMERA_VIDEO_URL;

            if (null != cameraUrl && cameraUrl.length() > 4) {
                mSurfaceView.setSource(cameraUrl);//��ʼ��Camera
            }
        } else if (WIFI_STATE_NOT_CONNECTED == status) {
            mTvLog.setText(R.string.wifi_state_not_connect);
        } else {
            mTvLog.setText(R.string.wifi_not_open);
        }
    }

    private void initWifiConnection() {
        mWifiStatus = STATUS_INIT;
        Log.i("Socket", "initWifiConnection");
        try {
            if (mTcpSocket != null) {
                mTcpSocket.closeSocket();
            }
            String clientUrl = ROUTER_CONTROL_URL;
            int clientPort = ROUTER_CONTROL_PORT;
            mTcpSocket = new SocketClient(clientUrl, clientPort);
            Log.i("Socket", "Wifi Connect created ip=" + clientUrl + " port=" + clientPort);
            mWifiStatus = STATUS_CONNECTED;
        } catch (Exception e) {
            Log.d("Socket", "initWifiConnection return exception! ");
        }

        Message msg = new Message();
        if (mWifiStatus != STATUS_CONNECTED || null == mTcpSocket) {
            msg.what = MSG_ID_ERR_CONN;
        } else {
            msg.what = MSG_ID_CON_SUCCESS;
        }

        mHandler.sendMessage(msg);
    }

    @Override
    protected void onResume() {
        int status = NetUtil.getWifiStatus(this);
        if (WIFI_STATE_CONNECTED == status ) {
            String cameraUrl = CAMERA_VIDEO_URL;

            if (null != cameraUrl && cameraUrl.length() > 4) {
//                backgroundView.setSource(cameraUrl);
            }
        }
        super.onResume();
    }

    @Override
    public void onDestroy() {
        if (null != mTcpSocket) {
            try {
                mTcpSocket.closeSocket();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mThreadClient.interrupt();
        }

        if (null != mHandler) {
            int i;
            for (i = MSG_ID_LOOP_START + 1; i < MSG_ID_LOOP_END; i++) {
                mHandler.removeMessages(i);
            }
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (mQuitFlag) {
            finish();
        } else {
            mQuitFlag = true;
            Toast.makeText(mContext, "请再次按返回键退出应用", Toast.LENGTH_LONG).show();
            Message msg = new Message();
            msg.what = MSG_ID_CLEAR_QUIT_FLAG;
            mHandler.sendMessageDelayed(msg, QUIT_BUTTON_PRESS_INTERVAL);
        }
    }

    /**
     * 根据用户在{@link SettingActivity}设置的值来初始化设置
     *
     */
    void initSettings() {

        CAMERA_VIDEO_URL = SPUtil.getVideoAddress();

        String RouterUrl = SPUtil.getControlAddress();
        int index = RouterUrl.indexOf(":");
        String routerIP = "";
        int port = 0;
        if (index > 0) {   //将路由器的Ip和端口分开
            routerIP = RouterUrl.substring(0, index);
            String routerPort = RouterUrl.substring(index + 1, RouterUrl.length());
            port = Integer.parseInt(routerPort);
        }
        ROUTER_CONTROL_URL = routerIP;
        ROUTER_CONTROL_PORT = port;

        //初始化控制指令
        initControlComm();
    }

    /**
     * 初始化控制指令
     */
    void initControlComm() {

        C.CommandArray cmd = new C.CommandArray(SPUtil.getForwardComm());
        if (cmd.isValid()) {
            COMM_FORWARD[1] = cmd.mCmd1;
            COMM_FORWARD[2] = cmd.mCmd2;
            COMM_FORWARD[3] = cmd.mCmd3;
        } else {
            Log.i("CONTROL", "error format of command:" + SPUtil.getForwardComm());
        }

        C.CommandArray cmd1 = new C.CommandArray(SPUtil.getBackwardComm());
        if (cmd.isValid()) {
            COMM_BACKWARD[1] = cmd1.mCmd1;
            COMM_BACKWARD[2] = cmd1.mCmd2;
            COMM_BACKWARD[3] = cmd1.mCmd3;
        } else {
            Log.i("CONTROL", "error format of command:" + SPUtil.getBackwardComm());
        }

        C.CommandArray cmd2 = new C.CommandArray(SPUtil.getTurnLeftComm());
        if (cmd.isValid()) {
            COMM_LEFT[1] = cmd2.mCmd1;
            COMM_LEFT[2] = cmd2.mCmd2;
            COMM_LEFT[3] = cmd2.mCmd3;
        } else {
            Log.i("CONTROL", "error format of command:" + SPUtil.getTurnLeftComm());
        }

        C.CommandArray cmd3 = new C.CommandArray(SPUtil.getTurnRightComm());
        if (cmd.isValid()) {
            COMM_RIGHT[1] = cmd3.mCmd1;
            COMM_RIGHT[2] = cmd3.mCmd2;
            COMM_RIGHT[3] = cmd3.mCmd3;
        } else {
            Log.i("CONTROL", "error format of command:" + SPUtil.getTurnRightComm());
        }

        C.CommandArray cmd4 = new C.CommandArray(SPUtil.getStopComm());
        if (cmd.isValid()) {
            COMM_STOP[1] = cmd4.mCmd1;
            COMM_STOP[2] = cmd4.mCmd2;
            COMM_STOP[3] = cmd4.mCmd3;
        } else {
            Log.i("CONTROL", "error format of command:" + SPUtil.getStopComm());
        }
    }
}
