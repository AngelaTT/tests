package com.software.videoplayer.data.source.db;

import com.litesuits.orm.LiteOrm;
import com.software.videoplayer.BuildConfig;
import com.software.videoplayer.util.Injection;


/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/10/16
 * Time: 4:00 PM
 * Desc: LiteOrmHelper
 */
public class LiteOrmHelper {

    private static final String DB_NAME = "music-player.db";

    private static volatile LiteOrm sInstance;

    private LiteOrmHelper() {
        // Avoid direct instantiate
    }

    public static LiteOrm getInstance() {
        if (sInstance == null) {
            synchronized (LiteOrmHelper.class) {
                if (sInstance == null) {
                    sInstance = LiteOrm.newCascadeInstance(Injection.provideContext(), DB_NAME);
                    sInstance.setDebugged(BuildConfig.DEBUG);
                }
            }
        }
        return sInstance;
    }
}
