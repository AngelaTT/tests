package com.software.videoplayer.activity.video;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.TextView;

import com.software.videoplayer.R;
import com.software.videoplayer.adapter.LockFileShowAdapter;
import com.software.videoplayer.constants.MyConstants;
import com.software.videoplayer.constants.VideoConstants;
import com.software.videoplayer.data.Stroe;
import com.software.videoplayer.fragment.video.VideoHistoryFragment;
import com.software.videoplayer.model.LockInfo;
import com.software.videoplayer.model.VideoInfo;
import com.software.videoplayer.ui.MyPopupMenu;
import com.software.videoplayer.util.FileEnDecryptManager;
import com.software.videoplayer.util.FileUtils;
import com.software.videoplayer.util.SPHelper;
import com.software.videoplayer.util.VideoHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PrivateActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.private_gv)
    GridView privateGv;

    private List<LockInfo> video = new ArrayList<>();
    private List<LockInfo> image = new ArrayList<>();
    private LockFileShowAdapter videoShowAdapter;
    private LockFileShowAdapter imageShowAdapter;
    private boolean videoChangeState = false;
    private boolean imageChangeState = false;
    private boolean isVideo = true;
    @SuppressLint("UseSparseArrays")
    private Map<Integer, Boolean> videoMap=new HashMap<>();
    @SuppressLint("UseSparseArrays")
    private Map<Integer, Boolean> iamgeMap = new HashMap<>();
    private MyPopupMenu myPopupMenu;
    private View menu;
    private CheckBox selectAll;
    private TextView unLock,delete;
    List<LockInfo> unlockList = new ArrayList<>();

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private);
        ButterKnife.bind(this);
        if (getIntent() != null) {
            video = getIntent().getParcelableArrayListExtra(MyConstants.LOCK_VIDEO);
            image = getIntent().getParcelableArrayListExtra(MyConstants.LOCK_IMAGE);
            menu = LayoutInflater.from(this).inflate(R.layout.lock, null);
            unLock = (TextView) menu.findViewById(R.id.un_lock);
            delete = (TextView) menu.findViewById(R.id.lock_delete);
            selectAll  = (CheckBox) menu.findViewById(R.id.lock_cb);
            unLock.setOnClickListener(this);
            delete.setOnClickListener(this);
        }
        showView();
    }

    private void showView() {
//        if (isVideo) {
            videoShowAdapter = new LockFileShowAdapter(this, video, true);
            privateGv.setAdapter(videoShowAdapter);
            privateGv.setOnItemClickListener((parent, view, position, id) -> {
                if (!videoChangeState) {
                    LockInfo lockInfo = video.get(position);
                    FileEnDecryptManager.getInstance().Initdecrypt(lockInfo.getNewPath());
                    String oldName = lockInfo.getNewPath();
                    String newName = oldName.replace(new File(oldName).getName(), new File(lockInfo.getOldPath()).getName());
                    FileUtils.reNamePath(oldName, newName);
                    VideoInfo videoInfo1 = new VideoInfo();
                    videoInfo1.setName(lockInfo.getName());
                    videoInfo1.setPath(newName);
                    /**
                     * 根据文件名的后缀判断是否为图片，在对应操作打开文件的方式
                     */
                    String str = getStrLast3(videoInfo1);
                    if (str.equals("png")){
                        Intent intent = new Intent(this, ImageShowActivity.class);
                        intent.putExtra(VideoConstants.SCREENSHOT,videoInfo1);
                        startActivityForResult(intent,0);
                    }else {
                        VideoHelper.getVideoHelper().playerVideo(PrivateActivity.this, videoInfo1);
                    }

                    SPHelper.putBooleanMessage(this, MyConstants.LOCK, true);
                    SPHelper.putStringMessage(this, MyConstants.LOCKSTRING, newName);
                    SPHelper.putStringMessage(this, MyConstants.LOCK_FUCKSTRING, oldName);
                } else {
                    LockFileShowAdapter.ViewHolder viewHolder = (LockFileShowAdapter.ViewHolder) view.getTag();
                    viewHolder.selected.toggle();
                    videoMap.put(position, viewHolder.selected.isChecked());
                }

            });
            privateGv.setOnItemLongClickListener((parent, view, position, id) -> {
                if (!videoChangeState) {
                    videoChangeState = true;
                    videoShowAdapter.updateView(true);
                    myPopupMenu = new MyPopupMenu(PrivateActivity.this,menu).show();
                }
                return true;
            });
            selectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    videoShowAdapter.updateViewAllSelect(true);
                    for (int i=0;i<video.size();i++) {
                        videoMap.put(i, true);
                    }
                } else {
                    videoShowAdapter.updateViewAllSelect(false);
                    for (int i=0;i<video.size();i++) {
                        videoMap.put(i, false);
                    }
                }
            });
       /* } else {
            *//*imageShowAdapter = new LockFileShowAdapter(this, video, false);
            privateGv.setAdapter(imageShowAdapter);
            privateGv.setOnItemClickListener((parent, view, position, id) -> {
                LockInfo lockInfo = video.get(position);
                FileEnDecryptManager.getInstance().Initdecrypt(lockInfo.getNewPath());
                String oldName = lockInfo.getNewPath();
                String newName = oldName+new File(oldName).getName();
                FileUtils.reNamePath(oldName, newName);
                VideoInfo videoInfo1 = new VideoInfo();
                videoInfo1.setName(lockInfo.getName());
                videoInfo1.setPath(newName);
                VideoHelper.getVideoHelper().playerVideo(PrivateActivity.this, videoInfo1);

            });*//*
        }*/
    }

    private String getStrLast3(VideoInfo videoInfo1) {
        String name = videoInfo1.getName();
        String str = name.substring(name.length() - 3, name.length());
        return str;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (SPHelper.getBooleanMessage(this, MyConstants.LOCK)) {
            String newName = SPHelper.getStringnMessage(this, MyConstants.LOCKSTRING);
            String oldName = SPHelper.getStringnMessage(this, MyConstants.LOCK_FUCKSTRING);
            FileUtils.reNamePath(newName, oldName);
            FileEnDecryptManager.getInstance().InitEncrypt(oldName);
            SPHelper.putBooleanMessage(this, MyConstants.LOCK, false);
            Log.e("fuckA", "oldName" + oldName + "@@newName" + newName);
        }
    }

    @Override
    public void onBackPressed() {
        if (videoChangeState) {
            if (myPopupMenu != null) {
                myPopupMenu.dismiss();
            }
            if (videoShowAdapter != null) {
                videoShowAdapter.updateView(false);
                videoShowAdapter.updateViewAllSelect(false);
            }
            videoChangeState = false;

        } else if (imageChangeState) {

        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.un_lock:
                if (isVideo) {
                    ProgressDialog dialog = new ProgressDialog(this);
                    dialog.setMessage("解密恢复中...");
                    dialog.show();
                    new Thread(() -> {
                        for (int i=0;i<video.size();i++) {
                            if (videoMap.get(i) != null) {
                                if (videoMap.get(i)) {
                                    unlockList.add(video.get(i));
                                }
                            }
                        }
                        for (int j=0;j<unlockList.size();j++) {
                            FileEnDecryptManager.getInstance().Initdecrypt(unlockList.get(j).getNewPath());
                            FileUtils.cutFile(unlockList.get(j).getNewPath(), unlockList.get(j).getOldPath());
                            Log.e("fuck", unlockList.get(j).getOldPath());
                            video.remove(unlockList.get(j));
                        }
                        Stroe.storeData(this, MyConstants.LOCK_VIDEO, video);
                        runOnUiThread(() -> {
                            dialog.dismiss();
                            videoChangeState = false;
                            videoShowAdapter.updateView(false);
                            myPopupMenu.dismiss();
                            videoShowAdapter.updateViewAllSelect(false);
                            sendBroadcast(new Intent(MyConstants.UPDATE_PRIVATE));
                            unlockList.clear();
                        });
                    }).start();
                }

                break;
            case R.id.lock_delete:
                if (isVideo) {
                    new AlertDialog.Builder(this)
                            .setTitle("提示")
                            .setMessage("私有文件删除后无法回复,确认删除吗？")
                            .setPositiveButton("确定", (dialog, which) -> {
                                ProgressDialog myPd = new ProgressDialog(this);
                                myPd.setMessage("正在删除小秘密哦，请稍等...");
                                myPd.show();
                                new Thread(() -> {
                                    List<LockInfo> unlockList = new ArrayList<>();

                                    for (int i = 0; i < video.size(); i++) {
                                        if (videoMap.get(i) != null) {
                                            if (videoMap.get(i)) {
                                                unlockList.add(video.get(i));
                                            }
                                        }
                                    }
                                    for (int j = 0; j < unlockList.size(); j++) {
                                        FileUtils.delete(unlockList.get(j).getNewPath());
                                        video.remove(unlockList.get(j));
                                    }
                                    Stroe.storeData(this, MyConstants.LOCK_VIDEO, video);
                                    runOnUiThread(() -> {
                                        myPd.dismiss();
                                        videoChangeState = false;
                                        videoShowAdapter.updateView(false);
                                        videoShowAdapter.updateViewAllSelect(false);
                                        myPopupMenu.dismiss();
                                        sendBroadcast(new Intent(MyConstants.UPDATE_PRIVATE));
                                    });
                                }).start();
                            })
                            .setNegativeButton("取消", (dialog, which) -> {
                                videoChangeState = false;
                                videoShowAdapter.updateView(false);
                                videoShowAdapter.updateViewAllSelect(false);
                                myPopupMenu.dismiss();
                            })
                            .create().show();
                } else {
                    //TODO IMAGE_IMPL
                }

                break;
            default:
                break;
        }
    }

}
