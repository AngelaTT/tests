package com.software.videoplayer.activity.video;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.TextView;

import com.software.videoplayer.R;
import com.software.videoplayer.adapter.VideoFileShowAdapter;
import com.software.videoplayer.adapter.VideoFolderShowAdapter;
import com.software.videoplayer.constants.MyConstants;
import com.software.videoplayer.constants.VideoConstants;
import com.software.videoplayer.data.Stroe;
import com.software.videoplayer.model.LockInfo;
import com.software.videoplayer.model.VideoInfo;
import com.software.videoplayer.ui.MyPopupMenu;
import com.software.videoplayer.util.FileEnDecryptManager;
import com.software.videoplayer.util.FileUtils;
import com.software.videoplayer.util.VideoHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoFolderShowActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.video_show_gv)
    GridView mGridLayout;
    public static boolean file_show = false;
    public static Map<String, List<VideoInfo>> map;
    VideoFileShowAdapter videoFileShowAdapter;
    private List<VideoInfo> videoList = new ArrayList<>();
    VideoFolderShowAdapter videoFolderShowAdapter;
    private boolean changeStateFile = false;
    private boolean changeStateFolder = false;
    private HashMap<Integer, Boolean> selectMapFile;
    private HashMap<Integer, Boolean> selectMapFolder;
    private MyPopupMenu myPopupMenu;
    private View menu;
    private List<String> maxList;



    @SuppressLint("UseSparseArrays")
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loacal_video_show);

        ButterKnife.bind(this);
        selectMapFile = new HashMap<>();
        selectMapFolder = new HashMap<>();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.local_video));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }
        videoList = getIntent().getParcelableArrayListExtra(VideoConstants.VIDEO);
        for (int i=0;i<videoList.size();i++) {
            Log.e("fuck", videoList.get(i).getPath());
        }
        showVideo();
    }

    @SuppressLint("InflateParams")
    private void showVideo() {
        menu = LayoutInflater.from(this).inflate(R.layout.popup, null);
        CheckBox ppSa = (CheckBox) menu.findViewById(R.id.pp_sa);

        ppSa.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (file_show) {
                    videoFileShowAdapter.updateViewAllSelect(true);
                    for (int i=0;i<videoList.size(); i++) {
                        selectMapFile.put(i, true);
                    }
                } else {
                    videoFolderShowAdapter.updateViewAllSelect(true);
                    for (int j=0;j<maxList.size();j++) {
                        selectMapFolder.put(j, true);
                    }
                }
            } else {
                if (file_show) {
                    videoFileShowAdapter.updateViewAllSelect(false);
                    for (int i=0;i<videoList.size(); i++) {
                        selectMapFile.put(i, false);
                    }
                } else {
                    videoFolderShowAdapter.updateViewAllSelect(false);
                    for (int j=0;j<maxList.size();j++) {
                        selectMapFolder.put(j, false);
                    }
                }
            }

        });


        TextView ppAp = (TextView) menu.findViewById(R.id.pp_ap);
        TextView ppHv = (TextView) menu.findViewById(R.id.pp_hv);
        TextView ppDl = (TextView) menu.findViewById(R.id.pp_dl);
        ppAp.setOnClickListener(this);
        ppHv.setOnClickListener(this);
        ppDl.setOnClickListener(this);

        if (file_show) {
            videoFileShowAdapter = new VideoFileShowAdapter(this, videoList, true);
            mGridLayout.setAdapter(videoFileShowAdapter);
            mGridLayout.setOnItemClickListener((adapterView, view, i, l) -> {
                if (changeStateFile) {
                    VideoFileShowAdapter.ViewHolder viewHolder = (VideoFileShowAdapter.ViewHolder) view.getTag();
                    viewHolder.selected.toggle();
                    selectMapFile.put(i, viewHolder.selected.isChecked());
                } else {
                    VideoHelper.getVideoHelper().playerVideo(this, videoList.get(i));
                    if (videoList.get(i).getNew()) {
                        videoList.get(i).setNew(false);
                        videoFileShowAdapter.notifyDataSetChanged();
                    }
                }


            });

            mGridLayout.setOnItemLongClickListener((parent, view, position, id) -> {
                if (!changeStateFile) {
                    changeStateFile = true;
                    videoFileShowAdapter.updateView(true);
                    myPopupMenu = new MyPopupMenu(this, menu).show();
                }
                return true;
            });


        } else {
            map = new HashMap<>();
            maxList= new ArrayList<>();

            for (ListIterator<VideoInfo> it = videoList.listIterator(); it
                    .hasNext(); ) {
                VideoInfo mGridItem = it.next();
                String bucket = mGridItem.getBucket();
                if (!map.containsKey(bucket)) {
                    List<VideoInfo> minList= new ArrayList<>();
                    minList.add(mGridItem);
                    map.put(bucket, minList);
                    maxList.add(bucket);
                } else {
                    if (!map.get(bucket).contains(mGridItem)) {
                        map.get(bucket).add(mGridItem);
                    }
                }
            }
            videoFolderShowAdapter = new VideoFolderShowAdapter(this, maxList, map);
            mGridLayout.setAdapter(videoFolderShowAdapter);
            mGridLayout.setOnItemClickListener((adapterView, view, i, l) -> {
                if (changeStateFolder) {
                    VideoFolderShowAdapter.ViewHolderFolder viewHolderFolder = (VideoFolderShowAdapter.ViewHolderFolder) view.getTag();
                    viewHolderFolder.checked.toggle();
                    selectMapFolder.put(i, viewHolderFolder.checked.isChecked());
                } else {
                    Intent intent = new Intent(this, VideoFileShowActivity.class);
                    intent.putExtra(VideoConstants.TYPE, maxList.get(i));
                    startActivityForResult(intent,0);
                }

            });

            mGridLayout.setOnItemLongClickListener((parent, view, position, id) -> {
                if (!changeStateFolder) {
                    changeStateFolder = true;
                    videoFolderShowAdapter.updateView(true);
                    myPopupMenu = new MyPopupMenu(this, menu).show();
                }
                return true;
            });


        }


        backData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.video_show, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.fuck_view_change:
                if (item.getTitle().equals("文件浏览")) {
                    item.setTitle("文件夹浏览");
                } else {
                    item.setTitle("文件浏览");
                }
                file_show = !file_show;
                showVideo();
                if (myPopupMenu != null) {
                    myPopupMenu.dismiss();
                }
                if (changeStateFolder) {
                    changeStateFolder = false;
                }
                if (changeStateFile) {
                    changeStateFile = false;
                }
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!file_show && VideoFileShowActivity.cleanNew) {
            videoFolderShowAdapter.notifyDataSetChanged();
        }
    }

    private void backData() {
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(VideoConstants.VIDEO, (ArrayList<? extends Parcelable>) videoList);
        VideoFolderShowActivity.this.setResult(1, intent);
    }

    @Override
    public void onBackPressed() {
        if (changeStateFile) {
            if (myPopupMenu != null) {
                myPopupMenu.dismiss();
            }
            videoFileShowAdapter.updateView(false);
            videoFolderShowAdapter.updateViewAllSelect(false);
            changeStateFile = false;
        } else if (changeStateFolder) {
            if (myPopupMenu != null) {
                myPopupMenu.dismiss();
            }
            videoFolderShowAdapter.updateView(false);
            videoFolderShowAdapter.updateViewAllSelect(false);
            changeStateFolder = false;
        } else {
            super.onBackPressed();
        }

    }
    @SuppressWarnings("unchecked")
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pp_ap:
                if (file_show) {
                    ProgressDialog pld = new ProgressDialog(this);
                    pld.setMessage("文件加密中，请稍等...");
                    pld.show();
                    new Thread(() -> {
                        List<VideoInfo> dlList = new ArrayList<>();
                        List<LockInfo> lockInfoList = new ArrayList<>();
                        if (Stroe.getData(this, MyConstants.LOCK_VIDEO, LockInfo.class) != null) {
                            lockInfoList = (List<LockInfo>) Stroe.getData(this, MyConstants.LOCK_VIDEO, LockInfo.class);
                        }
                        for (int i=0;i<videoList.size();i++) {
                            if (selectMapFile.get(i) != null) {
                                if (selectMapFile.get(i)) {
                                    dlList.add(videoList.get(i));
                                }
                            }
                        }
                        for (int j=0;j<dlList.size();j++) {
                            String oldPath=VideoConstants.PRIVATE_PATH + System.currentTimeMillis();
                            String newName=oldPath+".fuck";
                            FileUtils.cutFile(dlList.get(j).getPath(),newName);
                            FileEnDecryptManager.getInstance().InitEncrypt(newName);
                            LockInfo lockInfo = new LockInfo(dlList.get(j).getName(), dlList.get(j).getPath(), newName);
                            assert lockInfoList != null;
                            lockInfoList.add(lockInfo);
                            videoList.remove(dlList.get(j));
                        }
                        Stroe.storeData(this, MyConstants.LOCK_VIDEO, lockInfoList);
                        runOnUiThread(() -> {
                            pld.dismiss();
                            myPopupMenu.dismiss();
                            videoFileShowAdapter.notifyDataSetChanged();
                            videoFolderShowAdapter.updateView(false);
                            changeStateFile = false;
                        });
                    }).start();

                } else {
                    ProgressDialog pld = new ProgressDialog(this);
                    pld.setMessage("文件加密中,请稍等...");
                    pld.show();
                    new Thread(() -> {
                        List<VideoInfo> dlList = new ArrayList<>();
                        List<LockInfo> lockInfoList = new ArrayList<>();
                        List<String> maxDlList = new ArrayList<>();
                        if (Stroe.getData(this, MyConstants.LOCK_VIDEO, LockInfo.class) != null) {
                            lockInfoList = (List<LockInfo>) Stroe.getData(this, MyConstants.LOCK_VIDEO, LockInfo.class);
                        }
                        for (int i=0;i<maxList.size();i++) {
                            if (selectMapFolder.get(i) != null) {
                                if (selectMapFolder.get(i)) {
                                    List<VideoInfo> minList=map.get(maxList.get(i));
                                    for (int j=0;j<minList.size();j++) {
                                        dlList.add(minList.get(j));
                                    }
                                    maxDlList.add(maxList.get(i));
                                }
                            }
                        }
                        for (int k=0;k<maxDlList.size();k++) {
                            maxList.remove(maxDlList.get(k));
                        }
                        for (int j=0;j<dlList.size();j++) {
                            String oldPath=VideoConstants.PRIVATE_PATH + System.currentTimeMillis();
                            String newName=oldPath+".fuck";
                            FileUtils.cutFile(dlList.get(j).getPath(),newName);
                            FileEnDecryptManager.getInstance().InitEncrypt(newName);

                            LockInfo lockInfo = new LockInfo(dlList.get(j).getName(), dlList.get(j).getPath(), newName);
                            assert lockInfoList != null;
                            lockInfoList.add(lockInfo);
                            videoList.remove(dlList.get(j));
                        }
                        Stroe.storeData(this, MyConstants.LOCK_VIDEO, lockInfoList);
                        runOnUiThread(() -> {
                            pld.dismiss();
                            myPopupMenu.dismiss();
                            videoFolderShowAdapter.notifyDataSetChanged();
                            videoFolderShowAdapter.updateView(false);
                            changeStateFolder = false;
                        });


                    }).start();

                }
                sendBroadcast(new Intent(MyConstants.UPDATE_PRIVATE));
                break;
            case R.id.pp_dl:
                if (file_show) {

                    ProgressDialog progressDialog = new ProgressDialog(this);
                    progressDialog.setMessage("删除中...");
                    progressDialog.show();

                   new Thread(() -> {
                       List<VideoInfo> dlList = new ArrayList<>();
                       for (int i=0;i<videoList.size();i++) {
                           if (selectMapFile.get(i) != null) {
                               if (selectMapFile.get(i)) {
                                   dlList.add(videoList.get(i));
                               }
                           }
                       }
                       for (int j=0;j<dlList.size();j++) {
                           FileUtils.delete(dlList.get(j).getPath());
                           videoList.remove(dlList.get(j));
                       }

                       Stroe.storeData(this, VideoConstants.VIDEO, videoList);

                       runOnUiThread(() -> {
                           progressDialog.dismiss();
                           myPopupMenu.dismiss();
                           videoFileShowAdapter.notifyDataSetChanged();
                           videoFolderShowAdapter.updateView(false);
                           changeStateFile = false;
                       });
                   }).start();
                } else {
                    ProgressDialog progressDialog = new ProgressDialog(this);
                    progressDialog.setMessage("删除中...");
                    progressDialog.show();

                    new Thread(() -> {
                        List<VideoInfo> dlList = new ArrayList<>();
                        List<String> maxDlList = new ArrayList<>();
                        for (int i=0;i<maxList.size();i++) {
                            if (selectMapFolder.get(i) != null) {
                                if (selectMapFolder.get(i)) {
                                    List<VideoInfo> minList=map.get(maxList.get(i));
                                    for (int j=0;j<minList.size();j++) {
                                        dlList.add(minList.get(j));
                                    }
                                    maxDlList.add(maxList.get(i));
                                }
                            }
                        }
                        for (int k=0;k<maxDlList.size();k++) {
                            maxList.remove(maxDlList.get(k));
                        }
                        for (int j=0;j<dlList.size();j++) {
                            FileUtils.delete(dlList.get(j).getPath());
                            videoList.remove(dlList.get(j));
                        }

                        Stroe.storeData(this, VideoConstants.VIDEO, videoList);

                        runOnUiThread(() -> {
                            progressDialog.dismiss();
                            myPopupMenu.dismiss();
                            videoFolderShowAdapter.notifyDataSetChanged();
                            videoFolderShowAdapter.updateView(false);
                            changeStateFolder = false;
                        });
                    }).start();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == 1) {
            if (data != null) {

                if (!data.getStringExtra(MyConstants.DETE_ALL).equals("")) {
                    maxList.remove(data.getStringExtra(MyConstants.DETE_ALL));
                }

                videoFolderShowAdapter.notifyDataSetChanged();

                List<VideoInfo> list = data.getParcelableArrayListExtra(VideoConstants.VIDEO_DELETE);
                if (list != null) {
                    for (int i=0;i<list.size();i++) {
                        videoList.remove(list.get(i));
                    }
                }

            }
        }
    }
}