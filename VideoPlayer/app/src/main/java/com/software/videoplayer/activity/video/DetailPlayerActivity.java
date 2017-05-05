package com.software.videoplayer.activity.video;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.shuyu.gsyvideoplayer.GSYPreViewManager;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.GSYVideoPlayer;
import com.shuyu.gsyvideoplayer.GSYWindowManager;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.software.videoplayer.R;
import com.software.videoplayer.activity.BaseActivity1;
import com.software.videoplayer.constants.MyConstants;
import com.software.videoplayer.constants.VideoConstants;
import com.software.videoplayer.data.Stroe;
import com.software.videoplayer.interfaces.SampleListener;
import com.software.videoplayer.model.VideoInfo;
import com.software.videoplayer.util.LandLayoutVideo;
import com.software.videoplayer.util.VideoHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailPlayerActivity extends BaseActivity1 {

    @BindView(R.id.detail_player)
    LandLayoutVideo detailPlayer;

    @BindView(R.id.activity_detail_player)
    RelativeLayout activityDetailPlayer;

    private OrientationUtils orientationUtils;

    private VideoInfo videoInfo;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_player);
        ButterKnife.bind(this);
        videoInfo = getIntent().getParcelableExtra(VideoConstants.VIDEO_INFO);
        if (videoInfo != null) {
            url = videoInfo.getPath();
        } else {
            if (getIntent().getData() != null) {
                    url = VideoHelper.getPath(this, getIntent().getData());
                    videoInfo = new VideoInfo();
                    videoInfo.setPath(url);
                    videoInfo.setName(new File(url).getName());
            }
        }

        if (url.endsWith(".mkv")) {
            GSYVideoManager.instance().setVideoType(this, 1);
        } else if (url.endsWith(".avi")) {
            GSYVideoManager.instance().setVideoType(this, 1);
        } else {
            GSYVideoManager.instance().setVideoType(this, 0);
        }

        if (getIntent().getBooleanExtra(VideoConstants.VIDEO_CASH, false)) {
            detailPlayer.setUp(url, false, null, videoInfo.getName());
        } else {
            detailPlayer.setUp(url, true, null, videoInfo.getName());
        }

        playerVideo();
        detailPlayer.getBackButton().setOnClickListener(v -> this.finish());
        orientationUtils.resolveByClick();
        detailPlayer.startPlayLogic();
    }
    @SuppressWarnings("unchecked")
    private void playerVideo() {

//        外部辅助的旋转，帮助全屏
        orientationUtils = new OrientationUtils(this, detailPlayer);
        //初始化不打开外部的旋转
        orientationUtils.setEnable(false);

        detailPlayer.setIsTouchWiget(true);
        //关闭自动旋转
        detailPlayer.setRotateViewAuto(false);
        detailPlayer.setLockLand(false);
        detailPlayer.setShowFullAnimation(false);
        detailPlayer.setNeedLockFull(true);
        detailPlayer.setOpenPreView(true);
        detailPlayer.getFullscreenButton().setOnClickListener(v -> {
            //直接横屏
            orientationUtils.resolveByClick();

            //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
            detailPlayer.startWindowFullscreen(DetailPlayerActivity.this, true, true);
        });

        detailPlayer.setStandardVideoAllCallBack(new SampleListener() {
            @Override
            public void onPrepared(String url, Object... objects) {
                super.onPrepared(url, objects);
                //开始播放了才能旋转和全屏
                orientationUtils.setEnable(true);
                genHistory();
            }

            @Override
            public void onAutoComplete(String url, Object... objects) {
                super.onAutoComplete(url, objects);
            }

            @Override
            public void onClickStartError(String url, Object... objects) {
                super.onClickStartError(url, objects);
            }

            @Override
            public void onQuitFullscreen(String url, Object... objects) {
                super.onQuitFullscreen(url, objects);
                if (orientationUtils != null) {
                    orientationUtils.backToProtVideo();
                }
            }

            @Override
            public void onPlayError(String url, Object... objects) {
                super.onPlayError(url, objects);
                Toast.makeText(DetailPlayerActivity.this,"播放链接失效！",Toast.LENGTH_SHORT).show();
                new Thread(() -> {

                    List<VideoInfo> history_show= (List<VideoInfo>) Stroe.getData(DetailPlayerActivity.this, VideoConstants.HISTORY, VideoInfo.class);
                    if (history_show != null) {
                        history_show.remove(videoInfo);
                    }

                    Stroe.storeData(DetailPlayerActivity.this, VideoConstants.HISTORY,history_show);
                    Intent intent = new Intent(MyConstants.UPDATE_HISTORY);
                    intent.putExtra(MyConstants.UPDATE_HISTORY, videoInfo);
                    sendBroadcast(intent);

                }).start();
            }
        });

        detailPlayer.setLockClickListener((view, lock) -> {
            if (orientationUtils != null) {
                //配合下方的onConfigurationChanged
                orientationUtils.setEnable(!lock);
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void genHistory() {
        new Thread(() -> {

            List<VideoInfo> history_show= (List<VideoInfo>) Stroe.getData(DetailPlayerActivity.this, VideoConstants.HISTORY, VideoInfo.class);
            if (history_show != null) {
                if (!history_show.contains(videoInfo)) {
                    history_show.add(0, videoInfo);
                } else {
                    for (int i = 0; i < history_show.size(); i++) {
                        if (history_show.get(i).getPath().equals(videoInfo.getPath())) {
                            history_show.remove(videoInfo);
                        }
                    }
                    history_show.add(0, videoInfo);
                }
            } else {
                history_show = new ArrayList<>();
                history_show.add(videoInfo);
            }

            Stroe.storeData(DetailPlayerActivity.this, VideoConstants.HISTORY,history_show);
            sendBroadcast(new Intent(MyConstants.UPDATE_HISTORY));

        }).start();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (GSYWindowManager.instance().getView() != null) {
            LandLayoutVideo.showsamll = false;
            detailPlayer.hideSmallVideo();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GSYVideoPlayer.releaseAllVideos();
        GSYPreViewManager.instance().releaseMediaPlayer();
        if (orientationUtils != null)
            orientationUtils.releaseListener();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }
        super.onConfigurationChanged(newConfig);

    }

    @Override
    public void onBackPressed() {
        if (detailPlayer.getPopupWindow() != null) {
            if (detailPlayer.getPopupWindow().isShowing()) {
                detailPlayer.getPopupWindow().dismiss();
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }
}
