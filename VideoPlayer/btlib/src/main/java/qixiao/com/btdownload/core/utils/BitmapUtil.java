package qixiao.com.btdownload.core.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by Administrator on 2017/3/21.
 */

public class BitmapUtil {

    //通过id拿到bitmap
    public static Bitmap getBitmap(Context context, int id){
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), id);
        return bitmap;
    }
}
