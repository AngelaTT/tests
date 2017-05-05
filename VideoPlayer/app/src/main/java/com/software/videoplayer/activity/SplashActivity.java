package com.software.videoplayer.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;

import com.software.videoplayer.R;
import com.software.videoplayer.constants.MyConstants;
import com.software.videoplayer.util.PermissionHelper;

public class SplashActivity extends BaseActivity1 {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        acquirePermission();
    }

    private void jumpToMain() {
        new Handler().postDelayed(() -> {startActivity(new Intent(SplashActivity.this,MainActivity.class));this.finish();},1000);
    }

    private void acquirePermission() {

        boolean readStorage = PermissionHelper.getPermissionHelper().checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        boolean readPhone = PermissionHelper.getPermissionHelper().checkPermission(this, Manifest.permission.READ_PHONE_STATE);

        if (readStorage && readPhone) {
            jumpToMain();
        } else {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_PHONE_STATE}, MyConstants.READ_STORAGE);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MyConstants.READ_STORAGE:
                if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    jumpToMain();
                } else {
                    new AlertDialog.Builder(this)
                            .setTitle("权限请求")
                            .setMessage("我们需要读取SD卡的权限来扫描您手机上的视频音乐文件")
                            .setNegativeButton("取消", (dialogInterface, i) -> jumpToMain())
                            .setPositiveButton("确定", (dialogInterface, i) -> ActivityCompat.requestPermissions(SplashActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},MyConstants.READ_STORAGE))
                            .create().show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;

        }
    }
}
