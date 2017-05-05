package com.shuyu.gsyvideoplayer;

import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.shuyu.gsyvideoplayer.video.GSYBaseVideoPlayer;

public class SmallVideoTouch implements View.OnTouchListener {

    private int mDownX, mDownY;
    private int mMarginLeft, mMarginTop;
    private int _xDelta, _yDelta;
    private GSYBaseVideoPlayer mGsyBaseVideoPlayer;
    private int mTouchStartX = 0;
    private int mTouchStartY = 0;


    public SmallVideoTouch(GSYBaseVideoPlayer gsyBaseVideoPlayer, int marginLeft,  int marginTop) {
        super();
        mMarginLeft = marginLeft;
        mMarginTop = marginTop;
        mGsyBaseVideoPlayer = gsyBaseVideoPlayer;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {

        int action = event.getAction();

        WindowManager.LayoutParams wmParams = GSYWindowManager.instance().getParams();
        switch(action){
            case MotionEvent.ACTION_DOWN:
                mTouchStartX = (int)event.getRawX();
                mTouchStartY = (int)event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int mTouchCurrentX = (int) event.getRawX();
                int mTouchCurrentY = (int) event.getRawY();
                wmParams.x += mTouchCurrentX - mTouchStartX;
                wmParams.y += mTouchCurrentY - mTouchStartY;
                GSYWindowManager.instance().updateView(wmParams);

                mTouchStartX = mTouchCurrentX;
                mTouchStartY = mTouchCurrentY;
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return false;
    }

}