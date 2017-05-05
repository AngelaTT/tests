package com.software.videoplayer.activity.video;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.software.videoplayer.R;
import com.software.videoplayer.activity.BaseActivity1;
import com.software.videoplayer.adapter.VideoFileShowAdapter;
import com.software.videoplayer.constants.MyConstants;
import com.software.videoplayer.constants.VideoConstants;
import com.software.videoplayer.data.Stroe;
import com.software.videoplayer.fragment.video.VideoHistoryFragment;
import com.software.videoplayer.model.LockInfo;
import com.software.videoplayer.model.VideoInfo;
import com.software.videoplayer.util.FileEnDecryptManager;
import com.software.videoplayer.util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.software.videoplayer.activity.video.VideoFolderShowActivity.map;

public class ScreenShotActivity extends BaseActivity1 {

    @BindView(R.id.video_show_gv)
    GridView videoShowGv;
    @BindView(R.id.sh_all)
    TextView shAll;
    @BindView(R.id.sh_share)
    TextView shShare;
    @BindView(R.id.sh_delete)
    TextView shDelete;
    @BindView(R.id.sh_encryption)
    TextView shEncryption;
    @BindView(R.id.sh_menu)
    LinearLayout shMenu;
    private File[] files;
    private VideoFileShowAdapter fileShowAdapter;
    private List<VideoInfo> list;
    private boolean changeState = false;
    private Map<Integer, Boolean> map = new HashMap<>();
    private List<VideoInfo> selectedList = new ArrayList<>();
    private List<Uri> shareList = new ArrayList<>();
    private List<LockInfo> lockInfoList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loacal_video_show);
        ButterKnife.bind(this);
        videoShowGv.setNumColumns(3);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.screenshot));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        list = new ArrayList<>();
        /**
         * 解决4.3系统上的崩溃异常： Caused by: java.lang.ClassCastException: java.lang.Object[] cannot be cast to java.io.File[]
         * 将要传过来的File[]，转成ArryList<String>在通过intent传递
         * 接收后在把装有Fielpath的ArryList<String>转成File[]
         */
//        files = (File[]) getIntent().getSerializableExtra(VideoConstants.VIDEO_PATH);
        ArrayList<String> strings = getIntent().getStringArrayListExtra(VideoConstants.VIDEO_PATH);
        files = new File[strings.size()];
        for (int i = 0; i < strings.size(); i++) {
            files[i] = new File(strings.get(i));
        }

        for (int i = 0; i < files.length; i++) {
            VideoInfo videoInfo = new VideoInfo(files[files.length - 1 - i].getName(), null, files[files.length - 1 - i].getPath(), null, null);
            list.add(videoInfo);
        }
        fileShowAdapter = new VideoFileShowAdapter(this, list, false);
        videoShowGv.setAdapter(fileShowAdapter);
        videoShowGv.setOnItemClickListener((parent, view, position, id) -> {
            if (changeState) {
                VideoFileShowAdapter.ViewHolder viewHolder = (VideoFileShowAdapter.ViewHolder) view.getTag();
                viewHolder.selected.toggle();
                map.put(position, viewHolder.selected.isChecked());
            } else {
                Intent intent = new Intent(this, ImageShowActivity.class);
                intent.putExtra(VideoConstants.SCREENSHOT,list.get(position));
                startActivityForResult(intent,0);
            }});
        videoShowGv.setOnItemLongClickListener((parent, view, position, id) -> {
            if (!changeState) {
                changeState = true;
                fileShowAdapter.updateView(true);
                shMenu.setVisibility(View.VISIBLE);
            }
            return true;
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.sh_all, R.id.sh_share, R.id.sh_delete,R.id.sh_encryption})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.sh_all:
                if (shAll.getText().toString().equals("全选")) {
                    shAll.setText("取消全选");
                    fileShowAdapter.updateViewAllSelect(true);
                    for (int i=0;i<list.size();i++) {
                        map.put(i, true);
                    }
                } else {
                    shAll.setText("全选");
                    fileShowAdapter.updateViewAllSelect(false);
                    for (int i=0;i<list.size();i++) {
                        map.put(i, false);
                    }
                }
                break;
            case R.id.sh_share:
                for (int i=0;i<list.size();i++) {
                    if (map.get(i) != null) {
                        if (map.get(i)) {
                            shareList.add(Uri.fromFile(new File(list.get(i).getPath())));
                        }
                    }
                }
                if (shareList.size() > 9) {
                    Toast.makeText(this,"分享最多只能选择九张图片哦！",Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND_MULTIPLE);
                    intent.setType("image/*");
                    intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, (ArrayList<? extends Parcelable>) shareList);
                    startActivity(Intent.createChooser(intent,"图片分享"));
                }
                shMenu.setVisibility(View.GONE);
                changeState = false;
                fileShowAdapter.updateViewToNormal(false, false);
                shareList.clear();
                break;
            case R.id.sh_delete:
                ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("正在删除图片...");
                progressDialog.show();
                new Thread(() -> {
                    initSelectData();
                    for (int j=0;j<selectedList.size();j++) {
                        FileUtils.delete(selectedList.get(j).getPath());
                        list.remove(selectedList.get(j));
                    }
                    runOnUiThread(() -> {
                        progressDialog.dismiss();
                        shMenu.setVisibility(View.GONE);
                        changeState = false;
                        fileShowAdapter.updateViewToNormal(false, false);
                        selectedList.clear();
                        sendBroadcast(new Intent(MyConstants.UPDATE_SCREENSHOT));
                    });
                }).start();
                break;

            case R.id.sh_encryption:
                ProgressDialog pdl = new ProgressDialog(this);
                pdl.setMessage("加密中，请稍等...");
                pdl.show();
                new Thread(() -> {
                    initSelectData();
                    if (Stroe.getData(this, MyConstants.LOCK_VIDEO, LockInfo.class) != null) {
                        lockInfoList = (List<LockInfo>) Stroe.getData(this, MyConstants.LOCK_VIDEO, LockInfo.class);
                    }else {
                        lockInfoList = new ArrayList<LockInfo>();
                    }
                    for (int j=0;j<selectedList.size();j++) {
                        String oldPath=VideoConstants.PRIVATE_PATH + System.currentTimeMillis();
                        String newPath=oldPath+".fuck";
                        FileUtils.cutFile(selectedList.get(j).getPath(),newPath);
                        FileEnDecryptManager.getInstance().InitEncrypt(newPath);
                        LockInfo lockInfo = new LockInfo(selectedList.get(j).getName(), selectedList.get(j).getPath(), newPath);
                        lockInfoList.add(lockInfo);
                        list.remove(selectedList.get(j));
                        VideoHistoryFragment.history_show.remove(selectedList.get(j));
                    }
                    Stroe.storeData(this, MyConstants.LOCK_VIDEO, lockInfoList);
                    updateView(pdl);
                }).start();
                break;
        }
    }

    private void initSelectData() {
        for (int i=0;i<list.size();i++) {
            if (map.get(i) != null) {
                if (map.get(i)) {
                    selectedList.add(list.get(i));
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (changeState) {
            shMenu.setVisibility(View.GONE);
            changeState = false;
            fileShowAdapter.updateViewToNormal(false, false);
            selectedList.clear();
            shareList.clear();
            shAll.setText("全选");
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == 1 && data != null) {
            VideoInfo videoInfo = data.getParcelableExtra(VideoConstants.SCREENSHOT);
            list.remove(videoInfo);
            fileShowAdapter.notifyDataSetChanged();
        }
    }

    private void updateView(ProgressDialog pdl) {
        runOnUiThread(() -> {
            pdl.dismiss();
            shMenu.setVisibility(View.GONE);
            fileShowAdapter.notifyDataSetChanged();
            fileShowAdapter.updateView(false);
            changeState = false;
            sendBroadcast(new Intent(MyConstants.UPDATE_SCREENSHOT));
//            map.put(folder_name, list);
            Intent intent = new Intent();
            intent.putParcelableArrayListExtra(VideoConstants.VIDEO_DELETE, (ArrayList<? extends Parcelable>) selectedList);
          /*  if (list.size()<=0) {
                intent.putExtra(MyConstants.DETE_ALL, folder_name);
            } else {
                intent.putExtra(MyConstants.DETE_ALL, "");
            }*/
            this.setResult(1,intent);
            sendBroadcast(new Intent(MyConstants.UPDATE_PRIVATE));
            selectedList.clear();
        });
    }
}
