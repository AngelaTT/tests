package com.software.videoplayer.util;

import android.content.Context;
import android.os.Environment;

import com.software.videoplayer.R;
import com.software.videoplayer.data.model.Folder;
import com.software.videoplayer.data.model.PlayList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/9/16
 * Time: 10:27 PM
 * Desc: DBUtils
 */
public class DBUtils {

    public static PlayList generateFavoritePlayList(Context context) {
        PlayList favorite = new PlayList();
        favorite.setFavorite(true);
        favorite.setName(context.getString(R.string.mp_play_list_favorite));
        return favorite;
    }

    public static List<Folder> generateDefaultFolders() {
        List<Folder> defaultFolders = new ArrayList<>(3);
        // File sdcardDir = Environment.getExternalStorageDirectory();
        File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File musicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
        // defaultFolders.add(FileUtils.folderFromDir(sdcardDir));
        defaultFolders.add(com.software.videoplayer.util.FileUtils.folderFromDir(downloadDir));
        defaultFolders.add(com.software.videoplayer.util.FileUtils.folderFromDir(musicDir));
        return defaultFolders;
    }
}
