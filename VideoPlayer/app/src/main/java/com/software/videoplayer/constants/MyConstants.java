package com.software.videoplayer.constants;

import android.os.Environment;

import com.software.videoplayer.data.model.Song;

import java.util.ArrayList;

/**
 * Created by moon on 2017/2/16.
 */

public class MyConstants {

    public static final String VIDEO_SHOW = "video.show.message";
    public static final String UPDATE_VIDEO = "update.video";
    public static final String UPDATE_HISTORY = "update.history";
    public static final String UPDATE_SCREENSHOT = "update.screenshot";
    public static final String UPDATE_PRIVATE = "update.private";
    public static final String DETE_ALL = "delete.add";
    public static final String LOCK = "unlock";
    public static final String LOCKSTRING = "unlock.string";
    public static final String LOCK_FUCKSTRING = "unlock.fuck.string";
    public static final String IS_FIRST = "is.first";

    public static final String WEB_URL = "web.url";
    public static final String WEB_NAME = "web.name";
    public static final int READ_STORAGE = 1;

    public static boolean VIDEOLOCAL_SHOW = false;
    public static boolean HISTORY_SHOW = false;
    public static boolean SCREENSHOT_SHOW = false;
    public static boolean PRIVATE_SHOW = false;

    public static String CLOUD = "cloud";
    public static String LOCK_VIDEO = "lock.video";
    public static String LOCK_IMAGE = "lock.image";
    public static String LOCK_PASSWORD = "lock.password";
    public static String LOCK_ANSWER = "lock.answer";
    public static String NEW_BT_TASK = "new.bt.task";
    public static String DOWNLOADED_BT= "download.bt";
    public static String DOWNLOADED_PATH= "download.path";
    public static String BT_DOWNLOADING = "bt_downloading";
    public static final String BTDOWN_PATH= Environment.getExternalStorageDirectory() + "/FastVideoPlayer/.dowm/";

//    MusicPlayerFragment
    public static ArrayList<Song> songs = new ArrayList<>();//收藏的音乐列表
    public static ArrayList<Song> songsList = new ArrayList<>();//本地的音乐列表
    public static boolean[] checkedFlag  = new boolean[100]; //记录本地音乐的选择状态
    public static boolean[] checkedFlagFile  = new boolean[100]; //记录本地文件的选择状态
    public static int localeMP3Number = 0;
    public static int localeMP4Number = 0;
}
