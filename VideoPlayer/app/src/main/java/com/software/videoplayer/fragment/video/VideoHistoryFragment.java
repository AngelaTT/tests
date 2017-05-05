package com.software.videoplayer.fragment.video;

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
import com.software.videoplayer.model.VideoInfo;
import com.software.videoplayer.util.VideoHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class VideoHistoryFragment extends Fragment {

    @BindViews({R.id.hs_img1,R.id.hs_img2,R.id.hs_img3})
    List<ImageView> list;
    @BindViews({R.id.hs_txt1, R.id.hs_txt2, R.id.hs_txt3})
    List<TextView> txtList;

    private View root;
    public static List<VideoInfo> history_show=new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_video_history, container, false);
        ButterKnife.bind(this, root);
        if (getArguments() != null) {
            history_show = getArguments().getParcelableArrayList(VideoConstants.HISTORY);
            initView();
        }
        return root;
    }

    private void initView() {
        if (history_show.size() > 0) {
            if (history_show.size() >= 3) {
                for (int i = 0; i < 3; i++) {
                    Glide.with(this).load(history_show.get(i).getPath()).error(R.drawable.default_video).into(list.get(i));
                    txtList.get(i).setText(history_show.get(i).getName());
                }
            } else {
                for (int i = 0; i < history_show.size(); i++) {
                    Glide.with(this).load(history_show.get(i).getPath()).error(R.drawable.default_video).into(list.get(i));
                    txtList.get(i).setText(history_show.get(i).getName());
                }
                for (int j = 2; j >= history_show.size(); j--) {
                    list.get(j).setVisibility(View.INVISIBLE);
                }
            }

        }
    }

    @OnClick({R.id.hs_img1, R.id.hs_img2, R.id.hs_img3})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.hs_img1:
                VideoHelper.getVideoHelper().playerVideo(getActivity(), history_show.get(0));
                break;
            case R.id.hs_img2:
                VideoHelper.getVideoHelper().playerVideo(getActivity(), history_show.get(1));
                break;
            case R.id.hs_img3:
                VideoHelper.getVideoHelper().playerVideo(getActivity(), history_show.get(2));
                break;
        }
    }

}
