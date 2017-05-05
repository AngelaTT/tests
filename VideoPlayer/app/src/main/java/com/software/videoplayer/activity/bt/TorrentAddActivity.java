package com.software.videoplayer.activity.bt;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.software.videoplayer.R;


public class TorrentAddActivity extends AppCompatActivity {

    private String magnet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_torrent_add);
        if (getIntent() != null) {

        }
    }
}
