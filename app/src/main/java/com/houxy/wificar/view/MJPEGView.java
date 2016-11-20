package com.houxy.wificar.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.houxy.wificar.utils.Utils;

/**
 * Created by Houxy on 2016/11/19.
 */

public class MJPEGView extends SurfaceView implements SurfaceHolder.Callback, Runnable{

    public final static int POSITION_UPPER_LEFT = 9;
    public final static int POSITION_UPPER_RIGHT = 3;
    public final static int POSITION_LOWER_LEFT = 12;
    public final static int POSITION_LOWER_RIGHT = 6;

    public final static int SIZE_STANDARD = 1;
    public final static int SIZE_BEST_FIT = 4;
    public final static int SIZE_FULLSCREEN = 8;

    private SurfaceHolder mSurfaceHolder;
    private Canvas mCanvas;
    private MJPEGInputStream mInputStream;
    private boolean mIsDrawing;
    private boolean mShowFps = true;
    private boolean mTakePic = false;
    private Bitmap mFpsBitmap;
    private PorterDuffXfermode mode;
    private Paint p;
    private Paint fpsPaint;
    private String mInputStreamUrl;
    private long start;

    private int fpsPos;
    private int frameCounter;
    private int fpsTextColor;
    private int fpsBgColor;
    private int displayMode;
    private int mScreenWidth;
    private int mScreenHeight;

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

        fpsPaint = new Paint();
        fpsPaint.setTextAlign(Paint.Align.LEFT);
        fpsPaint.setTextSize(12);
        fpsPaint.setTypeface(Typeface.DEFAULT);
        fpsTextColor = Color.WHITE;
        fpsBgColor = Color.TRANSPARENT;
        fpsPos = MJPEGView.POSITION_UPPER_RIGHT;
        displayMode = MJPEGView.SIZE_STANDARD;

        DisplayMetrics dm = getResources().getDisplayMetrics();
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;

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

        start = System.currentTimeMillis();
        mode = new PorterDuffXfermode(PorterDuff.Mode.DST_OVER);
        p = new Paint();
        Log.i("MjpegView", "playback thread started! time:" + start);

        while (mIsDrawing){
            draw();
        }
    }

    private void draw() {

        int width;
        int height;
        Bitmap mBitmap;
        Rect destRect;

        try {
            mCanvas = mSurfaceHolder.lockCanvas();

            if (mInputStream == null && mInputStreamUrl != null) {
                mInputStream = MJPEGInputStream.read(mInputStreamUrl);
            }

            mBitmap = mInputStream.readMjpegFrame();

            if (mTakePic) {
                String fName = Utils.generateFileName();
                Log.i("MJPEGView", "mTakePic  " + fName);
                int res = Utils.saveBitmapToFile(mBitmap, fName);
//                BroadCastResult(res, fName);
                mTakePic = false;
            }
            destRect = destRect(mScreenWidth, mScreenHeight);
            mCanvas.drawColor(Color.BLACK);
            mCanvas.drawBitmap(mBitmap, null, destRect, p);

            //记录FPS
            if (mShowFps) {
                p.setXfermode(mode);
                if (mFpsBitmap != null) {
                    height = ((fpsPos & 1) == 1) ? destRect.top
                            : destRect.bottom - mFpsBitmap.getHeight();
                    width = ((fpsPos & 8) == 8) ? destRect.left
                            : destRect.right - mFpsBitmap.getWidth();
                    mCanvas.drawBitmap(mFpsBitmap, width, height, null);
                }
                p.setXfermode(null);
                frameCounter++;
                if ((System.currentTimeMillis() - start) >= 1000) {
                    String fps = String.valueOf(frameCounter) + "fps";
                    start = System.currentTimeMillis();
                    mFpsBitmap = makeFpsOverlay(fpsPaint, fps);
                    frameCounter = 0;
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if( null != mCanvas){
                mSurfaceHolder.unlockCanvasAndPost(mCanvas);
            }
        }
    }

    public void setSource(String url) {
        mInputStreamUrl = url;
        mInputStream = null;
    }

    private Rect destRect(int bmw, int bmh) {
        int tempx;
        int tempy;
        if (displayMode == MJPEGView.SIZE_STANDARD) {
            tempx = (getWidth() / 2) - (bmw / 2);
            tempy = (getHeight() / 2) - (bmh / 2);
            return new Rect(tempx, tempy, bmw + tempx, bmh + tempy);
        }
        if (displayMode == MJPEGView.SIZE_BEST_FIT) {
            float bmasp = (float) bmw / (float) bmh;
            bmw = getWidth();
            bmh = (int) (getWidth() / bmasp);
            if (bmh > getHeight()) {
                bmh = getHeight();
                bmw = (int) (getHeight() * bmasp);
            }
            tempx = (getWidth() / 2) - (bmw / 2);
            tempy = (getHeight() / 2) - (bmh / 2);
            return new Rect(tempx, tempy, bmw + tempx, bmh + tempy);
        }
        if (displayMode == MJPEGView.SIZE_FULLSCREEN)
            return new Rect(0, 0, getWidth(), getHeight());
        return null;
    }

    private Bitmap makeFpsOverlay(Paint p, String text) {
        Rect b = new Rect();
        p.getTextBounds(text, 0, text.length(), b);
        int bWidth = b.width() + 5;
        int bHeight = b.height() + 5;
        Bitmap bm = Bitmap.createBitmap(bWidth, bHeight, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        p.setColor(fpsBgColor);
        c.drawRect(0, 0, bWidth, bHeight, p);
        p.setColor(fpsTextColor);
        c.drawText(text, -b.left, (bHeight / 2) - ((p.ascent() + p.descent()) / 2) + 1, p);
        return bm;
    }



}
