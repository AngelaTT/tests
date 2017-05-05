package com.software.videoplayer.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.software.videoplayer.data.Stroe;

import java.util.List;

public class BtCacheSerVice extends Service {

    public BtCacheSerVice() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void storeList(Context context,List<String> list, String table) {
        new Thread(() -> Stroe.storeData(context, table, list)).start();
    }
}
