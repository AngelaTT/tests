package com.software.videoplayer.activity.video;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.software.videoplayer.R;
import com.software.videoplayer.adapter.VideoHistoryShowAdapter;
import com.software.videoplayer.constants.MyConstants;
import com.software.videoplayer.constants.VideoConstants;
import com.software.videoplayer.data.Stroe;
import com.software.videoplayer.model.LockInfo;
import com.software.videoplayer.model.VideoInfo;
import com.software.videoplayer.ui.DefaultDividerDecoration;
import com.software.videoplayer.util.Constants;
import com.software.videoplayer.util.FileEnDecryptManager;
import com.software.videoplayer.util.FileUtils;
import com.software.videoplayer.util.VideoHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.software.videoplayer.activity.video.VideoFolderShowActivity.map;

public class HistoryActivity extends AppCompatActivity {

    @BindView(R.id.video_show_lv)
    RecyclerView videoShowLv;
    VideoHistoryShowAdapter videoHistoryShowAdapter;
    @BindView(R.id.ht_select_all)
    TextView htSelectAll;
    @BindView(R.id.ht_delete)
    TextView htDelete;
    @BindView(R.id.ht_encryption)
    TextView htEncryption;
    @BindView(R.id.ht_menu)
    LinearLayout htMenu;
    private List<VideoInfo> history_show = new ArrayList<>();
    private boolean changeState = false;
    private Map<Integer, Boolean> selectedMap = new HashMap<>();
    private List<LockInfo> lockInfoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        ButterKnife.bind(this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.play_history));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        videoShowLv.addItemDecoration(new DefaultDividerDecoration());
        history_show = getIntent().getParcelableArrayListExtra(VideoConstants.HISTORY);
        videoHistoryShowAdapter = new VideoHistoryShowAdapter(this, history_show);
        videoShowLv.setAdapter(videoHistoryShowAdapter);
        videoHistoryShowAdapter.setOnItemClickListener(position -> {
                    if (changeState) {
                        int firVisPos = ((LinearLayoutManager) videoShowLv.getLayoutManager()).findFirstVisibleItemPosition();
                        VideoHistoryShowAdapter.ViewHolder viewHolder = (VideoHistoryShowAdapter.ViewHolder) videoShowLv.getChildViewHolder(videoShowLv.getChildAt(position - firVisPos));
                        viewHolder.checkBox.toggle();
                        selectedMap.put(position, viewHolder.checkBox.isChecked());
                    } else {
                        VideoHelper.getVideoHelper().playerVideo(this, history_show.get(position));
                    }
                }
        );
        videoHistoryShowAdapter.setOnItemLongClickListener(position -> {
            videoHistoryShowAdapter.updateView(true);
            changeState = true;
            htMenu.setVisibility(View.VISIBLE);
        });
        IntentFilter in = new IntentFilter();
        in.addAction(MyConstants.UPDATE_HISTORY);
        registerReceiver(mBR, in);
    }

    @SuppressWarnings("unchecked")
    BroadcastReceiver mBR = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MyConstants.UPDATE_HISTORY)) {
                VideoInfo videoInfo = intent.getParcelableExtra(MyConstants.UPDATE_HISTORY);
                history_show.remove(videoInfo);
                videoHistoryShowAdapter.notifyDataSetChanged();
            }
        }
    };

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBR);
    }

    @OnClick({R.id.ht_select_all, R.id.ht_delete,R.id.ht_encryption})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ht_select_all:
                if (htSelectAll.getText().toString().equals("全选")) {
                    videoHistoryShowAdapter.updateViewAllSelect(true);
                    htSelectAll.setText("取消全选");
                    for (int i=0;i<history_show.size();i++) {
                        selectedMap.put(i, true);
                    }
                } else {
                    videoHistoryShowAdapter.updateViewAllSelect(false);
                    htSelectAll.setText("全选");
                    for (int i=0;i<history_show.size();i++) {
                        selectedMap.put(i,false);
                    }
                }
                break;
            case R.id.ht_delete:
                List<VideoInfo> dlList = getSelectList();//初始化选择的集合
                for (int j=0;j<dlList.size();j++) {
                    history_show.remove(dlList.get(j));
                }
                videoHistoryShowAdapter.updateToNormal(false, false);
                changeState = false;
                htMenu.setVisibility(View.GONE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Stroe.storeData(HistoryActivity.this, VideoConstants.HISTORY, history_show);
                        sendBroadcast(new Intent(MyConstants.UPDATE_HISTORY));
                    }
                }).start();
                break;

            case R.id.ht_encryption://加密
                List<VideoInfo> selectList = getSelectList();//初始化选择的集合
                if (0 == selectList.size()){
                    Toast.makeText(this,"没有加密选项",Toast.LENGTH_SHORT).show();
                }else {
                    ProgressDialog pdl = new ProgressDialog(this);
                    pdl.setMessage("加密中，请稍等...");
                    pdl.show();
                    new Thread(() -> {
                        if (Stroe.getData(this, MyConstants.LOCK_VIDEO, LockInfo.class) != null) {
                            lockInfoList = (List<LockInfo>) Stroe.getData(this, MyConstants.LOCK_VIDEO, LockInfo.class);
                        }
                        for (int j=0;j<selectList.size();j++) {
                            String oldPath=VideoConstants.PRIVATE_PATH + System.currentTimeMillis();
                            String newPath=oldPath+".fuck";
                            FileUtils.cutFile(selectList.get(j).getPath(),newPath);
                            FileEnDecryptManager.getInstance().InitEncrypt(newPath);
                            LockInfo lockInfo = new LockInfo(selectList.get(j).getName(), selectList.get(j).getPath(), newPath);
                            lockInfoList.add(lockInfo);
//                            list.remove(dlList.get(j));
                        }
                        Stroe.storeData(this, MyConstants.LOCK_VIDEO, lockInfoList);
                        for (int i = 0; i < selectList.size(); i++) {
                            history_show.remove(selectList.get(i));
                        }
                        updateView(pdl,selectList);
                        Stroe.storeData(HistoryActivity.this, VideoConstants.HISTORY, history_show);
                        sendBroadcast(new Intent(MyConstants.UPDATE_HISTORY));
                    }).start();
                }
                break;
        }
    }

    private List<VideoInfo> getSelectList() {
        List<VideoInfo> dlList = new ArrayList<>();
        for (int i=0;i<history_show.size();i++) {
            if (selectedMap.get(i) != null) {
                if (selectedMap.get(i)) {
                    dlList.add(history_show.get(i));
                }
            }
        }
        return dlList;
    }

    @Override
    public void onBackPressed() {
        if (changeState) {
            videoHistoryShowAdapter.updateToNormal(false, false);
            changeState = false;
            htMenu.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }

    }

    private void updateView(ProgressDialog dialog,List<VideoInfo> dlList) {
        runOnUiThread(() -> {
            dialog.dismiss();
            htMenu.setVisibility(View.GONE);
            videoHistoryShowAdapter.notifyDataSetChanged();
            videoHistoryShowAdapter.updateView(false);
            changeState = false;
//            map.put(folder_name, list);
            Intent intent = new Intent();
            intent.putParcelableArrayListExtra(VideoConstants.VIDEO_DELETE, (ArrayList<? extends Parcelable>) dlList);
           /* if (list.size()<=0) {
                intent.putExtra(MyConstants.DETE_ALL, folder_name);
            } else {
                intent.putExtra(MyConstants.DETE_ALL, "");
            }*/
            this.setResult(1,intent);
            sendBroadcast(new Intent(MyConstants.UPDATE_PRIVATE));
        });
    }
}
