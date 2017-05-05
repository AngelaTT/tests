package com.software.videoplayer.constants;

import android.os.Environment;

/**
 * Created by moon on 2017/2/15.
 */

public class VideoConstants {

    public static final String VIDEO = "video";
    public static final String VIDEO_CASH = "cash";
    public static final String HISTORY = "history";
    public static final String SCREENSHOT = "screenshot";
    public static final String PRIVATE = "private";
    public static final String TYPE = "type";
    public static final String VIDEO_DELETE = "video.delete.list";
    public static final String VIDEO_PATH = "video.type";
    public static final String VIDEO_ID = "video.id";
    public static final String VIDEO_INFO = "video.info";
    public static final String FIND_NEW_VIDEO= "find.new.video";
    public static final String PRIVATE_STORE= "private.store";
    public static final String SCREENSHOT_PATH= Environment.getExternalStorageDirectory() + "/FastVideoPlayer/.screenshot/";
    public static final String PRIVATE_PATH= Environment.getExternalStorageDirectory() + "/FastVideoPlayer/.private/";

    public static boolean newScreenShot= false;
    public static boolean newHistory = false;
    public static boolean newLocalVideo = false;

    public static boolean deleteVideo = false;

}
