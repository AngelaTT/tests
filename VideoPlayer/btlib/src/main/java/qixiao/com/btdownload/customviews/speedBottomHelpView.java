package qixiao.com.btdownload.customviews;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import qixiao.com.btdownload.R;
import qixiao.com.btdownload.Utils.InternetSpeedUtil;

/**
 * Created by admin on 2017/4/7.
 */

public class speedBottomHelpView {

    private final View view;
    private final Context context;
    private TextView tvRx;
    private TextView tvTx;
    private TextView tvSpace;
    private InternetSpeedUtil s;
    private Timer timer;
    private Handler handler = new Handler();

    public speedBottomHelpView(Context context, View view){
        this.view = view;
        this.context = context;
        init(view);
    }

    private void init(View view) {
        tvRx = ((TextView) view.findViewById(R.id.tv_up));
        tvTx = ((TextView) view.findViewById(R.id.tv_down));
        tvSpace = ((TextView) view.findViewById(R.id.tv_space));
        tvSpace.setText("可用空间："+ getSdcardSpaceSvailable());

        s= new InternetSpeedUtil(context,tvRx, tvTx);
        if (timer == null) {
            timer = new Timer();
            timer.scheduleAtFixedRate(new RefreshTask(), 0L, (long) s.TIME_SPAN);
        }
        s.initData();
        s.updateViewData(context);
    }

    public  class RefreshTask extends TimerTask {

        @Override
        public void run() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    s.updateViewData(context);
                    tvSpace.setText("可用空间："+ getSdcardSpaceSvailable());
                }
            });
        }

    }

    private String getSdcardSpaceSvailable(){
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize;
        long totalBlocks;
        long availableBlocks;
// 由于API18（Android4.3）以后getBlockSize过时并且改为了getBlockSizeLong
// 因此这里需要根据版本号来使用那一套API
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
        {
            blockSize = stat.getBlockSizeLong();
            totalBlocks = stat.getBlockCountLong();
            availableBlocks = stat.getAvailableBlocksLong();
        }
        else
        {
            blockSize = stat.getBlockSize();
            totalBlocks = stat.getBlockCount();//总空间大小
            availableBlocks = stat.getAvailableBlocks();////可用空间大小
        }

       return Formatter.formatFileSize(context,((blockSize * availableBlocks)));
    }
}
