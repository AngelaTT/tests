package com.software.videoplayer.activity;

import android.support.v7.app.AppCompatActivity;

import com.umeng.analytics.MobclickAgent;

public class BaseActivity1 extends AppCompatActivity {

    @Override
    protected  void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
