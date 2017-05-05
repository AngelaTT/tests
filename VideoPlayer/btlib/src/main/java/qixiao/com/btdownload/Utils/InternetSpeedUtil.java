package qixiao.com.btdownload.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.widget.TextView;

import java.text.DecimalFormat;

/**
 * Created by admin on 2017/4/7.
 */

public class InternetSpeedUtil {
    private final Context context;
    private final TextView tvRx;
    private final TextView tvTx;

    private long rxtxTotal = 0;
    private long mobileRecvSum = 0;
    private long mobileSendSum = 0;
    private long wlanRecvSum = 0;
    private long wlanSendSum = 0;
    public double TIME_SPAN = 2000d;
    private DecimalFormat showFloatFormat = new DecimalFormat("0.00");


    public InternetSpeedUtil(Context context, TextView tv1, TextView tv2){
        this.tvRx = tv1;
        this.tvTx = tv2;
        this.context = context;
    }
    public void initData() {
        mobileRecvSum = TrafficStats.getMobileRxBytes();
        mobileSendSum = TrafficStats.getMobileTxBytes();
        wlanRecvSum = TrafficStats.getTotalRxBytes() - mobileRecvSum;
        wlanSendSum = TrafficStats.getTotalTxBytes() - mobileSendSum;
        rxtxTotal = TrafficStats.getTotalRxBytes()
                + TrafficStats.getTotalTxBytes();
    }
    public void updateViewData(Context context) {

        long tempSum = TrafficStats.getTotalRxBytes()
                + TrafficStats.getTotalTxBytes();
        long rxtxLast = tempSum - rxtxTotal;
        double totalSpeed = rxtxLast * 1000 / TIME_SPAN;
        rxtxTotal = tempSum;
        long tempMobileRx = TrafficStats.getMobileRxBytes();
        long tempMobileTx = TrafficStats.getMobileTxBytes();
        long tempWlanRx = TrafficStats.getTotalRxBytes() - tempMobileRx;
        long tempWlanTx = TrafficStats.getTotalTxBytes() - tempMobileTx;
        long mobileLastRecv = tempMobileRx - mobileRecvSum;
        long mobileLastSend = tempMobileTx - mobileSendSum;
        long wlanLastRecv = tempWlanRx - wlanRecvSum;
        long wlanLastSend = tempWlanTx - wlanSendSum;
        double mobileRecvSpeed = mobileLastRecv * 1000 / TIME_SPAN;
        double mobileSendSpeed = mobileLastSend * 1000 / TIME_SPAN;
        double wlanRecvSpeed = wlanLastRecv * 1000 / TIME_SPAN;
        double wlanSendSpeed = wlanLastSend * 1000 / TIME_SPAN;
        mobileRecvSum = tempMobileRx;
        mobileSendSum = tempMobileTx;
        wlanRecvSum = tempWlanRx;
        wlanSendSum = tempWlanTx;
        if (isWifi()){
            if (wlanRecvSpeed >= 0d) {
                tvRx.setText("↑: "+showSpeed(wlanRecvSpeed));
            }
            if (wlanSendSpeed >= 0d) {
                tvTx.setText("↓: "+showSpeed(wlanSendSpeed));
            }
        }else {
            if (mobileRecvSpeed >= 0d) {
                tvRx.setText("↑: "+showSpeed(mobileRecvSpeed));
            }
            if (mobileSendSpeed >= 0d) {
                tvTx.setText("↓: "+showSpeed(mobileSendSpeed));
            }
        }
    }

    private String showSpeed(double speed) {
        String speedString;
        if (speed >= 1048576d) {
            speedString = showFloatFormat.format(speed / 1048576d) + "MB/s";
        } else {
            speedString = showFloatFormat.format(speed / 1024d) + "KB/s";
        }
        return speedString;
    }
    public boolean isWifi() {
          boolean isWifi = false;
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
            if (activeNetworkInfo != null && activeNetworkInfo.isAvailable() && activeNetworkInfo.getState() == NetworkInfo.State.CONNECTED) {
                if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    isWifi = false;
                } else if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                        isWifi = true;
                }
            }
        return isWifi;
    }
}
