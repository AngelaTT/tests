package com.software.videoplayer.activity.video;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.TextView;

import com.software.videoplayer.R;
import com.software.videoplayer.activity.BaseActivity1;
import com.software.videoplayer.adapter.VideoFileShowAdapter;
import com.software.videoplayer.constants.MyConstants;
import com.software.videoplayer.constants.VideoConstants;
import com.software.videoplayer.data.Stroe;
import com.software.videoplayer.fragment.video.VideoHistoryFragment;
import com.software.videoplayer.model.LockInfo;
import com.software.videoplayer.model.VideoInfo;
import com.software.videoplayer.ui.MyPopupMenu;
import com.software.videoplayer.util.FileEnDecryptManager;
import com.software.videoplayer.util.FileUtils;
import com.software.videoplayer.util.VideoHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.software.videoplayer.activity.video.VideoFolderShowActivity.map;

public class VideoFileShowActivity extends BaseActivity1 implements View.OnClickListener {

    private List<VideoInfo> list=new ArrayList<>();
    private ActionBar actionBar;
    public static boolean cleanNew = true;
    private boolean changeState = false;
    private HashMap<Integer,Boolean> selectMap;
    MyPopupMenu popupWindow;
    VideoFileShowAdapter videoFileShowAdapter;
    private CheckBox ppSa;
    private TextView ppAp,ppHv,ppDl;
    private View menu;
    private String folder_name;
    private List<VideoInfo> dlList;
    private List<LockInfo> lockInfoList = new ArrayList<>();

    @SuppressLint({"UseSparseArrays", "InflateParams"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_file_show);
        selectMap = new HashMap<>();
        if (getSupportActionBar() != null) {
            actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        GridView gridView = (GridView) findViewById(R.id.video_show_file_gv);
         folder_name= getIntent().getStringExtra(VideoConstants.TYPE);
        if (getSupportActionBar() != null) {
            actionBar.setTitle(folder_name);
        }
        if (map != null) {
            list = map.get(folder_name);
        }
         videoFileShowAdapter= new VideoFileShowAdapter(this,list,true);
        gridView.setAdapter(videoFileShowAdapter);
        menu = LayoutInflater.from(this).inflate(R.layout.popup, null);
        ppSa = (CheckBox) menu.findViewById(R.id.pp_sa);

        ppSa.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                videoFileShowAdapter.updateViewAllSelect(true);
                for (int i = 0; i < list.size(); i++) {
                    selectMap.put(i, true);
                }
            } else {
                videoFileShowAdapter.updateViewAllSelect(false);
                for (int i = 0; i < list.size(); i++) {
                    selectMap.put(i, false);
                }
            }
        });

        ppAp = (TextView) menu.findViewById(R.id.pp_ap);
        ppHv = (TextView) menu.findViewById(R.id.pp_hv);
        ppDl = (TextView) menu.findViewById(R.id.pp_dl);
        ppAp.setOnClickListener(this);
        ppHv.setOnClickListener(this);
        ppDl.setOnClickListener(this);

        gridView.setOnItemClickListener((adapterView, view, i, l) -> {
            if (changeState) {
                VideoFileShowAdapter.ViewHolder viewHolder = (VideoFileShowAdapter.ViewHolder) view.getTag();
                viewHolder.selected.toggle();
                selectMap.put(i, viewHolder.selected.isChecked());
            } else {
                VideoHelper.getVideoHelper().playerVideo(this, list.get(i));
                if (list.get(i).getNew()) {
                    list.get(i).setNew(false);
                    videoFileShowAdapter.notifyDataSetChanged();
                }
            }

        });
        gridView.setOnItemLongClickListener((parent, view, position, id) -> {
            if (!changeState) {
                videoFileShowAdapter.updateView(true);
                changeState = true;
                popupWindow=new MyPopupMenu(this, menu).show();
            }
            return true;
        });
    }

    @Override
    public void onBackPressed() {

        if (changeState) {
            if (popupWindow != null) {
                popupWindow.dismiss();
            }

            if (videoFileShowAdapter != null) {
                videoFileShowAdapter.updateView(false);
                videoFileShowAdapter.updateViewAllSelect(false);
            }
            changeState = false;

        } else {
            new Thread(() -> {
                for (int i=0;i<list.size();i++) {
                    if (list.get(i).getNew()) {
                        cleanNew = false;
                    }
                }
            }).start();
            super.onBackPressed();
        }


    }







    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("unchecked")
    public void onClick(View view) {

        dlList= new ArrayList<>();
        for (int i=0;i<list.size();i++) {
            if (selectMap.get(i) != null) {
                if (selectMap.get(i)) {
                    dlList.add(list.get(i));
                }
            }
        }


        switch (view.getId()) {

            case R.id.pp_ap:
                ProgressDialog pdl = new ProgressDialog(this);
                pdl.setMessage("加密中，请稍等...");
                pdl.show();
                new Thread(() -> {
                    if (Stroe.getData(this, MyConstants.LOCK_VIDEO, LockInfo.class) != null) {
                        lockInfoList = (List<LockInfo>) Stroe.getData(this, MyConstants.LOCK_VIDEO, LockInfo.class);
                    }
                    for (int j=0;j<dlList.size();j++) {
                        String oldPath=VideoConstants.PRIVATE_PATH + System.currentTimeMillis();
                        String newPath=oldPath+".fuck";
                        FileUtils.cutFile(dlList.get(j).getPath(),newPath);
                        FileEnDecryptManager.getInstance().InitEncrypt(newPath);
                        LockInfo lockInfo = new LockInfo(dlList.get(j).getName(), dlList.get(j).getPath(), newPath);
                        lockInfoList.add(lockInfo);
                        list.remove(dlList.get(j));
                        VideoHistoryFragment.history_show.remove(dlList.get(j));
                    }
                    Stroe.storeData(this, MyConstants.LOCK_VIDEO, lockInfoList);
                    //以下两行代码作用为：去掉加密文件的播放记录
                    Stroe.storeData(VideoFileShowActivity.this, VideoConstants.HISTORY, VideoHistoryFragment.history_show);
                    sendBroadcast(new Intent(MyConstants.UPDATE_HISTORY));
                    updateView(pdl);
                }).start();
                break;
            case R.id.pp_dl:
                    ProgressDialog progressDialog = new ProgressDialog(this);
                    progressDialog.setMessage("删除中...");
                    progressDialog.show();

                    new Thread(() -> {

                        for (int j=0;j<dlList.size();j++) {
                            FileUtils.delete(dlList.get(j).getPath());
                            list.remove(dlList.get(j));
                        }
                        updateView(progressDialog);

                    }).start();
                break;
            default:
                break;
        }
    }

    private void updateView(ProgressDialog dialog) {
        runOnUiThread(() -> {
            dialog.dismiss();
            popupWindow.dismiss();
            videoFileShowAdapter.notifyDataSetChanged();
            videoFileShowAdapter.updateView(false);
            changeState = false;
            map.put(folder_name, list);
            Intent intent = new Intent();
            intent.putParcelableArrayListExtra(VideoConstants.VIDEO_DELETE, (ArrayList<? extends Parcelable>) dlList);
            if (list.size()<=0) {
                intent.putExtra(MyConstants.DETE_ALL, folder_name);
            } else {
                intent.putExtra(MyConstants.DETE_ALL, "");
            }
            this.setResult(1,intent);
            sendBroadcast(new Intent(MyConstants.UPDATE_PRIVATE));
        });
    }
}
