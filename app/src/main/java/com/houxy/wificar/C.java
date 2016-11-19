package com.houxy.wificar;

import android.util.Log;

/**
 * Created by Houxy on 2016/11/15.
 */

public class C {
    public static final String SP_DATA = "sp_data";

    public static final byte[] COMM_FORWARD = {(byte) 0xFF, (byte)0x00, (byte)0x01, (byte)0x00, (byte) 0xFF};
    public static final byte[] COMM_BACKWARD = {(byte) 0xFF, 0x00, 0x02, 0x00, (byte) 0xFF};
    public static final byte[] COMM_STOP = {(byte) 0xFF, 0x00, 0x00, 0x00, (byte) 0xFF};
    public static final byte[] COMM_LEFT = {(byte) 0xFF, 0x00, 0x03, 0x00, (byte) 0xFF};
    public static final byte[] COMM_RIGHT = {(byte) 0xFF, 0x00, 0x04, 0x00, (byte) 0xFF};
    public static final byte[] COMM_LEN_ON = {(byte) 0xFF, 0x04, 0x03, 0x00, (byte) 0xFF};
    public static final byte[] COMM_LEN_OFF = {(byte) 0xFF, 0x04, 0x02, 0x00, (byte) 0xFF};
    public static final byte[] COMM_GEAR_CONTROL = {(byte) 0xFF, 0x01, 0x01, 0x00, (byte) 0xFF};

    public static final byte[] COMM_SELF_CHECK = {(byte) 0xFF, (byte)0xEE, (byte)0xEE, 0x00, (byte) 0xFF};
    public static final byte[] COMM_SELF_CHECK_ALL = {(byte) 0xFF, (byte)0xEE, (byte)0xE0, 0x00, (byte) 0xFF};
    public static final byte[] COMM_HEART_BREAK = {(byte) 0xFF, (byte)0xEE, (byte)0xE1, 0x00, (byte) 0xFF};


    public static  String CAMERA_VIDEO_URL = "http://192.168.2.1:8080/?action=stream";
    public static  String CAMERA_VIDEO_URL_TEST = "";
    public static  String ROUTER_CONTROL_URL = "192.168.2.1";
    public static  String ROUTER_CONTROL_URL_TEST = "192.168.1.1";
    public static  int ROUTER_CONTROL_PORT = 2001;
    public static  int ROUTER_CONTROL_PORT_TEST = 2001;
    public static final String WIFI_SSID_PERFIX = "robot";


    public static final int MSG_ID_ERR_CONN = 1001;
    //private final int MSG_ID_ERR_SEND = 1002;
    public static final int MSG_ID_ERR_RECEIVE = 1003;
    public static final int MSG_ID_CON_READ = 1004;
    public static final int MSG_ID_CON_SUCCESS = 1005;
    public static final int MSG_ID_START_CHECK = 1006;
    public static final int MSG_ID_ERR_INIT_READ = 1007;
    public static final int MSG_ID_CLEAR_QUIT_FLAG = 1008;

    public static final int MSG_ID_LOOP_START = 1010;
    public static final int MSG_ID_HEART_BREAK_RECEIVE = 1011;
    public static final int MSG_ID_HEART_BREAK_SEND = 1012;
    public static final int MSG_ID_LOOP_END = 1013;

    public static final int STATUS_INIT = 0x2001;
    //private final int STATUS_CONNECTING = 0x2002;
    public static final int STATUS_CONNECTED = 0x2003;
    public static final int WARNING_ICON_OFF_DURATION_MSEC = 600;
    public static final int WARNING_ICON_ON_DURATION_MSEC = 800;

    public static final int WIFI_STATE_UNKNOW = 0x3000;
    public static final int WIFI_STATE_DISABLED = 0x3001;
    public static final int WIFI_STATE_NOT_CONNECTED = 0x3002;
    public static final int WIFI_STATE_CONNECTED = 0x3003;

    public static final int MIN_GEAR_STEP = 5;
    public static final int MAX_GEAR_VALUE = 180;
    public static final int INIT_GEAR_VALUE = 50;

    public static final byte COMMAND_PERFIX = -1;
    public static final int HEART_BREAK_CHECK_INTERVAL = 8000;//ms
    public static final int QUIT_BUTTON_PRESS_INTERVAL = 2500;//ms
    public static final int HEART_BREAK_SEND_INTERVAL = 2500;//ms

    public static final int COMMAND_LENGTH = 5;
    public static final int COMMAND_RADIOX = 16;
    public static final int MIN_COMMAND_REC_INTERVAL = 1000;//ms

    public static final String ACTION_TAKE_PICTURE_DONE = "hanry.take_picture_done";
    public static final String EXTRA_RES = "res";
    public static final String EXTRA_PATH = "path";

    public final static int CAM_RES_OK = 6;
    public final static int CAM_RES_FAIL_FILE_WRITE_ERROR = 7;
    public final static int CAM_RES_FAIL_FILE_NAME_ERROR = 8;
    public final static int CAM_RES_FAIL_NO_SPACE_LEFT = 9;
    public final static int CAM_RES_FAIL_BITMAP_ERROR = 10;
    public final static int CAM_RES_FAIL_UNKNOW = 20;

    public static class CommandArray {

        public byte mCmd1 = 0;
        public byte mCmd2 = 0;
        public byte mCmd3 = 0;
        public CommandArray (int cmd1, int cmd2, int cmd3) {
            mCmd1 = (byte)cmd1;
            mCmd2 = (byte)cmd2;
            mCmd3 = (byte)cmd3;
        }

        public CommandArray (String cmdLine) {
            int icmd1 = -1;
            int icmd2 = -1;
            int icmd3 = -1;

            if (cmdLine != null
                    && (cmdLine.startsWith("FF") || cmdLine.startsWith("ff"))
                    && (cmdLine.endsWith("FF") || cmdLine.endsWith("ff"))
                    && cmdLine.length() == COMMAND_LENGTH*2 ) {
                String cmd1 = cmdLine.substring(2, 4);
                String cmd2 = cmdLine.substring(4, 6);
                String cmd3 = cmdLine.substring(6, 8);

                try {
                    icmd1 = Integer.parseInt(cmd1, COMMAND_RADIOX);
                    icmd2 = Integer.parseInt(cmd2, COMMAND_RADIOX);
                    icmd3 = Integer.parseInt(cmd3, COMMAND_RADIOX);
                } catch (Exception e) {
                    icmd1 = icmd2 = icmd3 = -1;
                }

                if (icmd1 >= 0 && icmd2 >= 0 && icmd3 >= 0) {
                    mCmd1 = (byte)icmd1;
                    mCmd2 = (byte)icmd2;
                    mCmd3 = (byte)icmd3;

                } else {
                    Log.i("Constant", "uncorrect command:" + cmdLine
                            + " cmd1=" + icmd1
                            + " cmd2=" + icmd2
                            + " cmd3=" + icmd3);
                }
            } else {
                Log.i("Constant", "error format command:" + cmdLine
                        + " cmd1=" + icmd1
                        + " cmd2=" + icmd2
                        + " cmd3=" + icmd3);
            }
        }

        public boolean isValid() {
            if (mCmd1 != 0 || mCmd2 != 0 || mCmd3 != 0) {
                return true;
            } else {
                return false;
            }
        }
    }

}
