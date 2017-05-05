package com.software.videoplayer.activity.music;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.software.videoplayer.R;
import com.software.videoplayer.activity.BaseActivity1;
import com.software.videoplayer.fragment.music.MusicPlayerFragment;

public class MusicPlayerActivity extends BaseActivity1 {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        MusicPlayerFragment musicPlayerFragment = new MusicPlayerFragment();
        if (musicPlayerFragment.isAdded()) {
            transaction.show(musicPlayerFragment);
        } else {
            transaction.add(R.id.music_player, new MusicPlayerFragment());
        }
        transaction.commit();
    }
}
