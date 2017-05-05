package com.software.videoplayer.util;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;

/**
 * Created by moon on 2017/2/16.
 */

public class PermissionHelper {
    private static PermissionHelper videoHelper = new PermissionHelper();

    private PermissionHelper() {}

    public static PermissionHelper getPermissionHelper() {
        return videoHelper;
    }

    public boolean checkPermission(Activity activity, String permission) {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(activity.getApplicationContext(), permission);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                return false;
            } else {
                return true;
            }
        }
              return true;

    }
}
