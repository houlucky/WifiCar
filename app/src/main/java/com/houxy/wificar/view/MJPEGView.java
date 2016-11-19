package com.houxy.wificar.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Houxy on 2016/11/19.
 */

public class MJPEGView extends SurfaceView implements SurfaceHolder.Callback, Runnable{

    private SurfaceHolder mSurfaceHolder;
    private Canvas mCanvas;
    private boolean mIsDrawing;


    public MJPEGView(Context context) {
        super(context);
        initView();
    }



    public MJPEGView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public MJPEGView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        //获取焦点
        setFocusable(true);
        setFocusableInTouchMode(true);
        //设置屏幕常亮
        setKeepScreenOn(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mIsDrawing = true;
        new Thread(this).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mIsDrawing = false;
    }

    @Override
    public void run() {

        while (mIsDrawing){

        }
    }
}
