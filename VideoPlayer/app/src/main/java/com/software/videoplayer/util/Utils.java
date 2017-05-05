package com.software.videoplayer.util;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import java.io.File;
import java.io.IOException;

import static com.litesuits.orm.LiteOrm.TAG;

public class Utils {
	
	/**
	 * @param cxt
	 * @return 屏幕宽
	 */
	public static int getScreenWidth(Activity cxt) {
		WindowManager m = cxt.getWindowManager();
		Display d = m.getDefaultDisplay();
		return d.getWidth();
	}
	public static File ensureCreated(File folder) {
		if (!folder.exists() && !folder.mkdirs()) {
			Log.w(TAG, "Unable to create the directory: %s." + folder.getPath());
		}
		return folder;
	}
	public static boolean ensureCreatedFile(File file) {
		if (!file.exists()) {
			try {
				if (!file.createNewFile()) {
                    return false;
                } else {
                    return true;
                }
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		} else {
			return true;
		}
	}
	public static void updateFragment(FragmentActivity fragmentActivity, int resId, Fragment fragment) {

		FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();

		if (!fragment.isAdded()) {
			transaction.add(resId, fragment);
		} else {
			transaction.show(fragment);
		}

		transaction.commitAllowingStateLoss();

	}

	public static void replaceFragment(FragmentActivity fragmentActivity, int resId, Fragment fragment) {
		FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		transaction.replace(resId, fragment);
		transaction.commitAllowingStateLoss();
	}


}
