package com.shuyu.gsyvideoplayer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

/**
 * User: Moon
 * Data: 2017/4/7.
 */

public class GSYWindowManager {

    @SuppressLint("StaticFieldLeak")
    private static GSYWindowManager gsyPreViewManager;
    private View mView;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mParams;
    private int position=0;


    public static synchronized GSYWindowManager instance() {
        if (gsyPreViewManager == null) {
           gsyPreViewManager = new GSYWindowManager();
        }
        return gsyPreViewManager;
    }

    public void addView(Context context, final View view, WindowManager.LayoutParams mParams) {
        WindowManager windowManager = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        mWindowManager = windowManager;
        mView = view;
        this.mParams = mParams;
        windowManager.addView(view,mParams);

    }

    public void updateView(WindowManager.LayoutParams params) {
        if (mView != null) {
            mWindowManager.updateViewLayout(mView, params);
        }

    }

    public void remove() {
        if (mWindowManager != null) {
            try {
                mWindowManager.removeViewImmediate(mView);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }

        }
    }

    public WindowManager.LayoutParams getParams() {
        if (mParams == null) {
            mParams = new WindowManager.LayoutParams();
            mParams.gravity = Gravity.TOP | Gravity.LEFT;
            mParams.x = 0;
            mParams.y = 20;
            //总是出现在应用程序窗口之上
            mParams.type = WindowManager.LayoutParams.TYPE_TOAST;
            //设置图片格式，效果为背景透明
            mParams.format = PixelFormat.RGBA_8888;
            mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR |
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
            mParams.width =  400;
            mParams.height =300;
        }
        return mParams;
    }

    public void putPosition(int mPosition) {
        position = mPosition;
    }
    public int getPosition() {
        if (position != 0) {
            return position;
        }
        return position;
    }

    public View getView() {
        if (mView != null) {
            return mView;
        }
        return mView;
    }
}
