package com.software.videoplayer.ui;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.PopupWindow;

import com.shuyu.gsyvideoplayer.utils.CommonUtil;
import com.software.videoplayer.R;


/**
 * User: Moon
 * Data: 2017/3/29.
 */

public class MySettingDialog {

    private PopupWindow popupWindow;
    private Context mContext;
    private View mView;

    public MySettingDialog(Context context, View view) {
        mContext = context;
        mView = view;
    }

    public MySettingDialog show() {

        popupWindow= new PopupWindow(mView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setAnimationStyle(R.style.My_Setting);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        ViewGroup viewGroup= (ViewGroup) (CommonUtil.scanForActivity(mContext)).findViewById(Window.ID_ANDROID_CONTENT);
        popupWindow.showAtLocation(viewGroup, Gravity.RIGHT, 0, 0);
        return this;
    }

    public MySettingDialog dismiss() {
        popupWindow.dismiss();
        return this;
    }

    public boolean isShowing() {
        if (popupWindow != null) {
            return popupWindow.isShowing();
        }
        return false;
    }

}
