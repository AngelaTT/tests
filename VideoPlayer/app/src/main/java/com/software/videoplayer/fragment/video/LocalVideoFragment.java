package com.software.videoplayer.fragment.video;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.software.videoplayer.R;
import com.software.videoplayer.constants.VideoConstants;
import com.software.videoplayer.data.Stroe;
import com.software.videoplayer.model.VideoInfo;
import com.software.videoplayer.util.SPHelper;
import com.software.videoplayer.util.VideoHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LocalVideoFragment extends Fragment {

    @BindViews({R.id.lv_img1, R.id.lv_img2, R.id.lv_img3})
    List<ImageView> imgList;

    @BindViews({R.id.lv_txt1, R.id.lv_txt2, R.id.lv_txt3})
    List<TextView> txtList;

    @BindViews({R.id.new1, R.id.new2, R.id.new3})
    List<TextView> txtNewList;

    BroadcastReceiver broadcastReceiver;
    @BindView(R.id.lf_new)
    TextView lfNew;

    private List<VideoInfo> videoList = new ArrayList<>();
    private boolean clean;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_local_video, container, false);
        ButterKnife.bind(this, root);
        if (getArguments() != null) {
            videoList = getArguments().getParcelableArrayList(VideoConstants.VIDEO);
            initView();
            updateView();
            boolean isShow = SPHelper.getBooleanMessage(getActivity(), VideoConstants.FIND_NEW_VIDEO);
            if (!isShow) {
                lfNew.setVisibility(View.GONE);
            } else {
                lfNew.setVisibility(View.VISIBLE);
            }
        }
        return root;
    }

    private void updateView() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(VideoConstants.FIND_NEW_VIDEO);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                boolean findNew = intent.getBooleanExtra(VideoConstants.FIND_NEW_VIDEO, false);
                if (findNew) {
                    lfNew.setVisibility(View.VISIBLE);
                } else {
                    lfNew.setVisibility(View.GONE);
                }
            }
        };
        getActivity().registerReceiver(broadcastReceiver, intentFilter);
    }

    private void initView() {
        if (videoList.size() > 0) {
            if (videoList.size() >= 3) {
                for (int i = 0; i < 3; i++) {
                    Glide.with(this).load(videoList.get(i).getPath()).into(imgList.get(i));
                    txtList.get(i).setText(videoList.get(i).getName());
                    if (videoList.get(i).getNew()) {
                        txtNewList.get(i).setVisibility(View.VISIBLE);
                    } else {
                        txtNewList.get(i).setVisibility(View.GONE);
                    }

                }
            } else {
                for (int i = 0; i < videoList.size(); i++) {
                    Glide.with(this).load(videoList.get(i).getPath()).into(imgList.get(i));
                    txtList.get(i).setText(videoList.get(i).getName());
                    if (videoList.get(i).getNew()) {
                        txtNewList.get(i).setVisibility(View.VISIBLE);
                    } else {
                        txtNewList.get(i).setVisibility(View.GONE);
                    }
                }
                for (int j = 2; j >= videoList.size(); j--) {
                    imgList.get(j).setVisibility(View.INVISIBLE);
                    txtList.get(j).setVisibility(View.INVISIBLE);
                }
            }

        }
    }

    @Override

    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(broadcastReceiver);
    }

    @OnClick({R.id.lv_img1, R.id.lv_img2, R.id.lv_img3})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lv_img1:
                VideoHelper.getVideoHelper().playerVideo(getActivity(), videoList.get(0));
                if (videoList.get(0).getNew()) {
                    videoList.get(0).setNew(false);
                    txtNewList.get(0).setVisibility(View.GONE);
                }
                break;
            case R.id.lv_img2:
                VideoHelper.getVideoHelper().playerVideo(getActivity(), videoList.get(1));
                if (videoList.get(1).getNew()) {
                    videoList.get(1).setNew(false);
                    txtNewList.get(1).setVisibility(View.GONE);
                }
                break;
            case R.id.lv_img3:
                VideoHelper.getVideoHelper().playerVideo(getActivity(), videoList.get(2));
                if (videoList.get(2).getNew()) {
                    videoList.get(2).setNew(false);
                    txtNewList.get(2).setVisibility(View.GONE);
                }
                break;
        }

        new Thread(() -> {
            clean = true;
            for (int i = 0; i < videoList.size(); i++) {
                if (videoList.get(i).getNew()) {
                    clean = false;
                    break;
                }
            }
            Stroe.storeData(getContext(), VideoConstants.VIDEO, videoList);
            if (clean) {
                SPHelper.putBooleanMessage(getActivity(), VideoConstants.FIND_NEW_VIDEO, false);
                getActivity().runOnUiThread(() -> lfNew.setVisibility(View.GONE));
            }
        }).start();
    }
}
