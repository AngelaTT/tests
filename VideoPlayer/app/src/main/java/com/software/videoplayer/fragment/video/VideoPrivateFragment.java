package com.software.videoplayer.fragment.video;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.software.videoplayer.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class VideoPrivateFragment extends Fragment {


    private View root;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root=inflater.inflate(R.layout.fragment_video_private, container, false);
        return root;
    }

}
