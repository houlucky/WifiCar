package com.houxy.wificar.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import static com.houxy.wificar.C.WIFI_SSID_PERFIX;
import static com.houxy.wificar.C.WIFI_STATE_CONNECTED;
import static com.houxy.wificar.C.WIFI_STATE_DISABLED;
import static com.houxy.wificar.C.WIFI_STATE_NOT_CONNECTED;
import static com.houxy.wificar.C.WIFI_STATE_UNKNOW;

/**
 * Created by Houxy on 2016/11/17.
 */

public class NetUtil {


    public static int getWifiStatus(Context context) {
        int status = WIFI_STATE_UNKNOW;
        WifiManager mWifiMng = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        switch (mWifiMng.getWifiState()) {
            case WifiManager.WIFI_STATE_DISABLED:
            case WifiManager.WIFI_STATE_DISABLING:
            case WifiManager.WIFI_STATE_ENABLING:
            case WifiManager.WIFI_STATE_UNKNOWN:
                status = WIFI_STATE_DISABLED;
                break;
            case WifiManager.WIFI_STATE_ENABLED:
                status = WIFI_STATE_NOT_CONNECTED;
                ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo.State wifiState = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
//                NetworkInfo.State wifiState = conMan.getAllNetworks();
                if (NetworkInfo.State.CONNECTED == wifiState) {
                    WifiInfo info = mWifiMng.getConnectionInfo();
                    if (null != info) {
                        String bSSID = info.getBSSID();
                        String SSID = info.getSSID();
                        Log.i("Socket", "getWifiStatus bssid=" + bSSID + " ssid=" + SSID);
                        if (null != SSID && SSID.length() > 0) {
                            if (SSID.toLowerCase().contains(WIFI_SSID_PERFIX)) {
                                status = WIFI_STATE_CONNECTED;
                            }
                        }
                    }
                }
                break;
            default:
                break;
        }
        return status;
    }

}
