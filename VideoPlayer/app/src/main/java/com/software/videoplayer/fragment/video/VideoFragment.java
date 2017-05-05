package com.software.videoplayer.fragment.video;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.software.videoplayer.R;
import com.software.videoplayer.activity.video.HistoryActivity;
import com.software.videoplayer.activity.video.LockActivity;
import com.software.videoplayer.activity.video.RegisterActivity;
import com.software.videoplayer.activity.video.ScreenShotActivity;
import com.software.videoplayer.activity.video.VideoFolderShowActivity;
import com.software.videoplayer.constants.MyConstants;
import com.software.videoplayer.constants.VideoConstants;
import com.software.videoplayer.data.Stroe;
import com.software.videoplayer.fragment.file.FileFragment;
import com.software.videoplayer.model.VideoInfo;
import com.software.videoplayer.util.FileUtils;
import com.software.videoplayer.util.SPHelper;
import com.software.videoplayer.util.Utils;
import com.software.videoplayer.util.VideoHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.software.videoplayer.util.Utils.updateFragment;

public class VideoFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    @BindViews({R.id.rl_local_video, R.id.rl_history, R.id.rl_screenshot, R.id.rl_private})
    List<RelativeLayout> rlLList;
    @BindView(R.id.rl_refresh)
    SwipeRefreshLayout rlRefresh;

    private List<VideoInfo> videoList = new ArrayList<>();
    private List<VideoInfo> history_show = new ArrayList<>();
    private File fileScreenshot;
    private File[] filesScreenshot;
    private File filePrivate;
    private File[] filesPrivate;

    private List<Integer> idList = new ArrayList<>();
    private List<Fragment> fragmentList = new ArrayList<>();

    private LocalVideoFragment localVideoFragment;
    private VideoHistoryFragment videoHistoryFragment;
    private VideoScreenshotFragment videoScreenshotFragment;
    private VideoPrivateFragment videoPrivateFragment;
    private int size;

    /**
     * 单例对象实例
     */
    private static VideoFragment instance = null;

    public static VideoFragment getInstance() {
        if (instance == null) {
            instance = new VideoFragment();
        }
        return instance;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_video, container, false);
        ButterKnife.bind(this, root);
        setHasOptionsMenu(true);
        getData();
        refreshView();
        handleBR();
        return root;
    }

    @SuppressWarnings("unchecked")
    private void getData() {

        idList.add(R.id.rl_local_video);
        idList.add(R.id.rl_history);
        idList.add(R.id.rl_screenshot);
        idList.add(R.id.rl_private);

        new Thread(() -> {

            //本地视频Fragment
            if (Stroe.getData(getActivity(), VideoConstants.VIDEO, VideoInfo.class) != null) {
                videoList = (List<VideoInfo>) Stroe.getData(getActivity(), VideoConstants.VIDEO, VideoInfo.class);
            } else {
                videoList = VideoHelper.getVideoHelper().queryVideo(getActivity());
            }
            localVideoFragment = new LocalVideoFragment();
            if (videoList != null && videoList.size() > 0) {
                MyConstants.VIDEOLOCAL_SHOW = true;
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList(VideoConstants.VIDEO, (ArrayList<? extends Parcelable>) videoList);
                localVideoFragment.setArguments(bundle);
            } else {
                MyConstants.VIDEOLOCAL_SHOW = false;
            }
            fragmentList.add(localVideoFragment);

            //播放历史fragment
            videoHistoryFragment = new VideoHistoryFragment();
            if (Stroe.getData(getActivity(), VideoConstants.HISTORY, VideoInfo.class) != null) {

                history_show = (List<VideoInfo>) Stroe.getData(getActivity(), VideoConstants.HISTORY, VideoInfo.class);
                assert history_show != null;
                if (!history_show.isEmpty()) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList(VideoConstants.HISTORY, (ArrayList<? extends Parcelable>) history_show);
                    videoHistoryFragment.setArguments(bundle);
                    MyConstants.HISTORY_SHOW = true;
                } else {
                    MyConstants.HISTORY_SHOW = false;
                }
            } else {
                MyConstants.HISTORY_SHOW = false;
            }
            fragmentList.add(videoHistoryFragment);

            //视频截屏Fragment
            fileScreenshot =  Utils.ensureCreated(new File(VideoConstants.SCREENSHOT_PATH));
            filesScreenshot = fileScreenshot.listFiles();
            videoScreenshotFragment = new VideoScreenshotFragment();
            if (filesScreenshot != null && filesScreenshot.length > 0) {
                MyConstants.SCREENSHOT_SHOW = true;
                Bundle bundle = new Bundle();
                bundle.putSerializable(VideoConstants.SCREENSHOT, filesScreenshot);
                videoScreenshotFragment.setArguments(bundle);
            } else {
                MyConstants.SCREENSHOT_SHOW = false;
            }
            fragmentList.add(videoScreenshotFragment);

            //私密文件夹Fragment
            filePrivate=Utils.ensureCreated(new File(VideoConstants.PRIVATE_PATH));
            filesPrivate = filePrivate.listFiles();
            videoPrivateFragment = new VideoPrivateFragment();
            if (filesPrivate != null && filesPrivate.length > 0) {
                MyConstants.PRIVATE_SHOW = true;
            } else {
                MyConstants.PRIVATE_SHOW = false;
            }
            fragmentList.add(videoPrivateFragment);

            getActivity().runOnUiThread(this::initView);


        }).start();
    }

    private void refreshView() {
        rlRefresh.setOnRefreshListener(this);
    }

    private void handleBR() {
        IntentFilter in = new IntentFilter();
        in.addAction(MyConstants.UPDATE_VIDEO);
        in.addAction(MyConstants.UPDATE_HISTORY);
        in.addAction(MyConstants.UPDATE_SCREENSHOT);
        in.addAction(MyConstants.UPDATE_PRIVATE);
        getContext().registerReceiver(mBR, in);
    }

    public void initView() {
        if (fragmentList.size() > 0) {
            for (int j = 0; j < rlLList.size(); j++) {
                updateFragment(getActivity(), idList.get(j), fragmentList.get(j));
            }
            if (MyConstants.VIDEOLOCAL_SHOW) {
                rlLList.get(0).setVisibility(View.VISIBLE);
            } else {
                rlLList.get(0).setVisibility(View.GONE);
            }
            if (MyConstants.HISTORY_SHOW) {
                rlLList.get(1).setVisibility(View.VISIBLE);
            } else {
                rlLList.get(1).setVisibility(View.GONE);
            }
            if (MyConstants.SCREENSHOT_SHOW) {
                rlLList.get(2).setVisibility(View.VISIBLE);
            } else {
                rlLList.get(2).setVisibility(View.GONE);
            }
            if (MyConstants.PRIVATE_SHOW) {
                rlLList.get(3).setVisibility(View.VISIBLE);
            } else {
                rlLList.get(3).setVisibility(View.GONE);
            }

        }
    }

    @OnClick({R.id.rl_local_video, R.id.rl_history, R.id.rl_screenshot, R.id.rl_private})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_local_video:
                videoToShow();
                break;
            case R.id.rl_history:
                historyToShow();
                break;
            case R.id.rl_screenshot:
                screenshotToShow();
                break;
            case R.id.rl_private:
                privateToShow();
                break;
        }
    }

    private void privateToShow() {
        if (!SPHelper.getBooleanMessage(getActivity(), MyConstants.IS_FIRST)) {
            startActivity(new Intent(getActivity(),RegisterActivity.class));
        } else {
            Intent intent = new Intent(getActivity(), LockActivity.class);
            intent.putExtra(VideoConstants.PRIVATE, filesPrivate);
            startActivity(intent);
        }

    }

    private void screenshotToShow() {
        ArrayList<String> filePath = new ArrayList<>();
        for (int i = 0; i < filesScreenshot.length; i++) {
            filePath.add(filesScreenshot[i].getPath());
        }
        Intent intent = new Intent(getActivity(), ScreenShotActivity.class);
        intent.putStringArrayListExtra(VideoConstants.VIDEO_PATH, filePath);
        startActivity(intent);
    }

    @SuppressWarnings("unchecked")
    private void videoToShow() {
        Intent intent = new Intent(getContext(), VideoFolderShowActivity.class);
        intent.putParcelableArrayListExtra(VideoConstants.VIDEO, (ArrayList<? extends Parcelable>) videoList);
        startActivityForResult(intent,0);
        size = videoList.size();
    }

    private void historyToShow() {
        Intent intent = new Intent(getActivity(), HistoryActivity.class);
        intent.putParcelableArrayListExtra(VideoConstants.HISTORY, (ArrayList<? extends Parcelable>) history_show);
        startActivity(intent);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.scanner_all:
                findNewVideo();
                break;
          /*  case R.id.refresh_video:
                refreshVideo();
                break;*/
            default:
                break;
        }
        return true;
    }

    private void refreshVideo() {

        rlRefresh.setRefreshing(true);
        refreshLocalVideo();

    }

    private void findNewVideo() {
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("扫描中...");
        progressDialog.show();

        new Thread(() -> {

            List<VideoInfo> newList = VideoHelper.getVideoHelper().getVideoInfoList();
            for (int i = 0; i < newList.size(); i++) {
                if (!videoList.contains(newList.get(i))) {
                    newList.get(i).setNew(true);
                    videoList.add(0,newList.get(i));
                }
            }
            checkLocalVideo(progressDialog, null);
        }).start();
    }

    private void checkLocalVideo(ProgressDialog progressDialog,SwipeRefreshLayout rlRefresh) {
        List<VideoInfo> dlVideo = new ArrayList<>();
        if (0 == videoList.size()) {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            return;
        }
        for (int j = 0; j < videoList.size(); j++) {

            if (!FileUtils.isFileExist(videoList.get(j).getPath())) {
                dlVideo.add(videoList.get(j));
            }
        }
        if (!dlVideo.isEmpty()) {
            for (int j = 0; j < dlVideo.size(); j++) {
                videoList.remove(dlVideo.get(j));
            }
            VideoConstants.deleteVideo = true;
        }
        for (int i=0;i<videoList.size();i++) {
            if (videoList.get(i).getNew()) {
                VideoConstants.newLocalVideo = true;
                break;
            }
        }


        if (VideoConstants.newLocalVideo || VideoConstants.deleteVideo) {
            Stroe.storeData(getActivity(), VideoConstants.VIDEO, videoList);
            Intent intent = new Intent();
            intent.setAction(MyConstants.UPDATE_VIDEO);
            getActivity().sendBroadcast(intent);
        }

        getActivity().runOnUiThread(() -> {

            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            if (rlRefresh != null) {
                rlRefresh.setRefreshing(false);
            }

            if (VideoConstants.newLocalVideo) {
                findNewVideo(true);
                SPHelper.putBooleanMessage(getActivity(), VideoConstants.FIND_NEW_VIDEO, true);
            }
            if (VideoConstants.deleteVideo) {
                VideoConstants.deleteVideo = false;
            }
        });
    }

    @Override
    public void onRefresh() {
//        更新视频
        refreshLocalVideo();
    }

    private void refreshLocalVideo() {
        new Thread(() -> {
            getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE));

            List<VideoInfo> newList = VideoHelper.getVideoHelper().queryVideo(getActivity());
            for (int i = 0; i < newList.size(); i++) {
                if (!videoList.contains(newList.get(i))) {
                    newList.get(i).setNew(true);
                    videoList.add(0,newList.get(i));
                }
           }
            getActivity().sendBroadcast(new Intent(MyConstants.UPDATE_HISTORY));
            getActivity().sendBroadcast(new Intent(MyConstants.UPDATE_SCREENSHOT));
            getActivity().sendBroadcast(new Intent(MyConstants.UPDATE_PRIVATE));
            checkLocalVideo(null,rlRefresh);
        }).start();
    }

    @SuppressWarnings("unchecked")
    BroadcastReceiver mBR = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case MyConstants.UPDATE_VIDEO:

                        if (videoList.isEmpty()) {
                            rlLList.get(0).setVisibility(View.GONE);
                            SPHelper.putBooleanMessage(getActivity(), VideoConstants.FIND_NEW_VIDEO, false);
                        } else {
                            rlLList.get(0).setVisibility(View.VISIBLE);
                            LocalVideoFragment newLVF = new LocalVideoFragment();
                            Bundle bundle = new Bundle();
                            bundle.putParcelableArrayList(VideoConstants.VIDEO, (ArrayList<? extends Parcelable>) videoList);
                            newLVF.setArguments(bundle);
                            updateFragment(getActivity(), idList.get(0), newLVF);
                        }
                    break;
                case MyConstants.UPDATE_HISTORY:

                    history_show = (List<VideoInfo>) Stroe.getData(getActivity(), VideoConstants.HISTORY, VideoInfo.class);

                    if (history_show != null) {
                        if (history_show.size() <= 0) {
                            rlLList.get(1).setVisibility(View.GONE);
                        } else {
                            rlLList.get(1).setVisibility(View.VISIBLE);
                            VideoHistoryFragment newHF = new VideoHistoryFragment();
                            Bundle bundle = new Bundle();
                            bundle.putParcelableArrayList(VideoConstants.HISTORY, (ArrayList<? extends Parcelable>) history_show);
                            newHF.setArguments(bundle);
                            updateFragment(getActivity(), idList.get(1), newHF);
                        }
                    } else {
                        rlLList.get(1).setVisibility(View.GONE);
                    }

                    break;
                case MyConstants.UPDATE_SCREENSHOT:
                    if (fileScreenshot != null && fileScreenshot.exists()) {
                        filesScreenshot = fileScreenshot.listFiles();
                        if (filesScreenshot.length<=0) {
                            rlLList.get(2).setVisibility(View.GONE);
                        } else {
                            rlLList.get(2).setVisibility(View.VISIBLE);
                            VideoScreenshotFragment newVSF = new VideoScreenshotFragment();
                            Bundle bundleVSF = new Bundle();
                            bundleVSF.putSerializable(VideoConstants.SCREENSHOT, filesScreenshot);
                            newVSF.setArguments(bundleVSF);
                            updateFragment(getActivity(), idList.get(2), newVSF);
                        }
                    }
                    break;
                case MyConstants.UPDATE_PRIVATE:

                    if (filesPrivate != null && filePrivate.exists()) {
                        filesPrivate = filePrivate.listFiles();
                        if (filesPrivate.length<=0) {
                            rlLList.get(3).setVisibility(View.GONE);
                        } else {
                            rlLList.get(3).setVisibility(View.VISIBLE);
                            VideoPrivateFragment newVSF = new VideoPrivateFragment();
                            Bundle bundleVSF = new Bundle();
                            bundleVSF.putSerializable(VideoConstants.SCREENSHOT, filesScreenshot);
                            newVSF.setArguments(bundleVSF);
                            updateFragment(getActivity(), idList.get(3), newVSF);
                        }
                    }

                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        getContext().unregisterReceiver(mBR);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        VideoConstants.newLocalVideo = false;
        if (requestCode == 0 && resultCode==1) {
            if (data != null) {
                videoList = data.getParcelableArrayListExtra(VideoConstants.VIDEO);
                new Thread(() -> {
                    if (!videoList.isEmpty()) {
                        for (int i = 0; i < videoList.size(); i++) {
                            if (videoList.get(i).getNew()) {
                                VideoConstants.newLocalVideo = true;
                                break;
                            }
                        }

                        if (!VideoConstants.newLocalVideo) {
                            findNewVideo(false);
                            SPHelper.putBooleanMessage(getActivity(), VideoConstants.FIND_NEW_VIDEO, false);
                        }

                        getActivity().sendBroadcast(new Intent(MyConstants.UPDATE_VIDEO));

                    } else {
                        getActivity().sendBroadcast(new Intent(MyConstants.UPDATE_VIDEO));
                    }
                    Stroe.storeData(getActivity(), VideoConstants.VIDEO, videoList);
                }).start();
            }
        }
    }

    private void findNewVideo(boolean isNew) {
        Intent intent = new Intent();
        intent.setAction(VideoConstants.FIND_NEW_VIDEO);
        intent.putExtra(VideoConstants.FIND_NEW_VIDEO, isNew);
        getActivity().sendBroadcast(intent);
        VideoConstants.newLocalVideo = false;
    }
}
