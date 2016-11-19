package com.houxy.wificar.utils;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.annotation.StringRes;

import com.houxy.wificar.C;
import com.houxy.wificar.R;
import com.houxy.wificar.WifiCarApplication;

/**
 * Created by Houxy on 2016/11/17.
 */

public class SPUtil {

    public SPUtil() {
        throw new AssertionError("no instance");
    }

    private static class Holder {
        private static SharedPreferences sp = WifiCarApplication.getContext().getSharedPreferences(C.SP_DATA, Activity.MODE_PRIVATE);
    }

    private static SharedPreferences getSP() {
        return Holder.sp;
    }

    public static void setForwardComm(String comm){
        getSP().edit().putString(getString(R.string.turn_forward), comm).apply();
    }

    public static String getForwardComm(){
        return getSP().getString(getString(R.string.turn_forward), getString(R.string.command_turn_forward));
    }

    public static void setBackwardComm(String comm){
        getSP().edit().putString(getString(R.string.turn_backward), comm).apply();
    }

    public static String getBackwardComm(){
        return getSP().getString(getString(R.string.turn_backward), getString(R.string.command_turn_backward));
    }

    public static void setTurnLeftComm(String comm){
        getSP().edit().putString(getString(R.string.turn_left), comm).apply();
    }

    public static String getTurnLeftComm(){
        return getSP().getString(getString(R.string.turn_left), getString(R.string.command_turn_left));
    }

    public static void setTurnRightComm(String comm){
        getSP().edit().putString(getString(R.string.turn_right), comm).apply();
    }

    public static String getTurnRightComm(){
        return getSP().getString(getString(R.string.turn_right), getString(R.string.command_turn_right));
    }

    public static void setStopComm(String comm){
        getSP().edit().putString(getString(R.string.stop), comm).apply();
    }

    public static String getStopComm(){
        return getSP().getString(getString(R.string.stop), getString(R.string.command_stop));
    }

    public static void setControlPort(String comm){
        getSP().edit().putString(getString(R.string.port), comm).apply();
    }

    public static String getControlPort(){
        return getSP().getString(getString(R.string.port), getString(R.string.command_port));
    }

    public static void setControlAddress(String comm){
        getSP().edit().putString(getString(R.string.control_address), comm).apply();
    }

    public static String getControlAddress(){
        return getSP().getString(getString(R.string.control_address), getString(R.string.url_control_address));
    }

    public static void setVideoAddress(String comm){
        getSP().edit().putString(getString(R.string.video_address), comm).apply();
    }

    public static String getVideoAddress(){
        return getSP().getString(getString(R.string.video_address), getString(R.string.url_video_address));
    }

    private static String getString(@StringRes int id){
        return WifiCarApplication.getContext().getString(id);
    }
}
