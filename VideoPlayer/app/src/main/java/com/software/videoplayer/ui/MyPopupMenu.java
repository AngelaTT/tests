package com.software.videoplayer.ui;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.software.videoplayer.R;

/**
 * User: Moon
 * Data: 2017/3/20.
 */

public class MyPopupMenu {

    private PopupWindow popupWindow;
    private Activity mContext;
    private View mView;

    public MyPopupMenu(Activity context, View view) {
        mContext = context;
        mView = view;
    }

    public MyPopupMenu show() {

        popupWindow= new PopupWindow(mView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setAnimationStyle(R.style.My_Setting);
        popupWindow.showAtLocation(mContext.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
        return this;
    }

    public MyPopupMenu dismiss() {
        popupWindow.dismiss();
        return this;
    }
}
