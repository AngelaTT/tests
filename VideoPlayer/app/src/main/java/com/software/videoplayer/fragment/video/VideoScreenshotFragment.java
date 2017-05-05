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
import com.software.videoplayer.util.VideoHelper;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class VideoScreenshotFragment extends Fragment {


    @BindViews({R.id.hs_img1, R.id.hs_img2, R.id.hs_img3})
    List<ImageView> imgList;
    @BindViews({R.id.hs_txt1, R.id.hs_txt2, R.id.hs_txt3})
    List<TextView> txtList;
    @BindView(R.id.record_img)
    ImageView recordImg;
    @BindView(R.id.record_name)
    TextView recordName;
    private File[] path;
    private View root;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_video_history, container, false);
        ButterKnife.bind(this, root);
        if (getArguments() != null) {
            path = (File[]) getArguments().getSerializable(VideoConstants.SCREENSHOT);
            recordImg.setImageResource(R.drawable.screen_img);
            recordName.setText(R.string.screenshot);
            initView();
        }
        return root;
    }

    private void initView() {
        if (path.length > 0) {
            if (path.length >= 3) {
                for (int i = 0; i < 3; i++) {
                    Glide.with(this).load(path[path.length-1-i].getPath()).into(imgList.get(i));
                    txtList.get(i).setText(path[path.length-1-i].getName());
                }
            } else {
                for (int i = 0; i < path.length; i++) {
                    Glide.with(this).load(path[path.length-1-i].getPath()).into(imgList.get(i));
                    txtList.get(i).setText(path[path.length-1-i].getName());
                }
                for (int j = 2; j >= path.length; j--) {
                    imgList.get(j).setVisibility(View.INVISIBLE);
                }
            }

        }
    }
    @OnClick({R.id.hs_img1, R.id.hs_img2, R.id.hs_img3})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.hs_img1:
                VideoHelper.getVideoHelper().showImage(getActivity(),path[path.length-1].getPath());
                break;
            case R.id.hs_img2:
                VideoHelper.getVideoHelper().showImage(getActivity(),path[path.length-2].getPath());
                break;
            case R.id.hs_img3:
                VideoHelper.getVideoHelper().showImage(getActivity(),path[path.length-3].getPath());
                break;
        }
    }
}
