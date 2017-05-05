package com.software.videoplayer.fragment.bt;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.software.videoplayer.R;
import com.software.videoplayer.adapter.DownloadedAdapter;
import com.software.videoplayer.constants.MyConstants;
import com.software.videoplayer.data.Stroe;
import com.software.videoplayer.model.VideoInfo;
import com.software.videoplayer.service.BtCacheSerVice;
import com.software.videoplayer.ui.DefaultDividerDecoration;
import com.software.videoplayer.util.FileUtils;
import com.software.videoplayer.util.VideoHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class DownloadedBtFragment extends Fragment {

    @BindView(R.id.downloaded_bt)
    RecyclerView downloadedBt;
    Unbinder unbinder;
    @BindView(R.id.bt_select_all)
    TextView htSelectAll;
    @BindView(R.id.ht_menu)
    LinearLayout htMenu;
    @BindView(R.id.bt_delete)
    TextView btDelete;
    private BroadcastReceiver downloadedReceiver;
    private List<String> down = new ArrayList<>();
    private DownloadedAdapter downloadedAdapter;
    private Map<Integer, Boolean> map = new HashMap<>();
    private BtCacheSerVice btCacheSerVice = new BtCacheSerVice();
    public boolean changeStateDown = false;

    @Override
    @SuppressWarnings("unchecked")
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_downloaded_bt, container, false);
        unbinder = ButterKnife.bind(this, view);
        downloadedBt.addItemDecoration(new DefaultDividerDecoration());
        if (Stroe.getData(getContext(), MyConstants.DOWNLOADED_BT, String.class) != null) {
            down = (List<String>) Stroe.getData(getContext(), MyConstants.DOWNLOADED_BT, String.class);
        }
        downloadedAdapter = new DownloadedAdapter(getContext(), down);
        downloadedBt.setAdapter(downloadedAdapter);
        downloadedAdapter.setOnItemClickListener(position -> {
            if (changeStateDown) {
                DownloadedAdapter.DownloadedViewHolder viewHolder = (DownloadedAdapter.DownloadedViewHolder) downloadedBt.getChildViewHolder(downloadedBt.getChildAt(position));
                viewHolder.checkBox.toggle();
                map.put(position, viewHolder.checkBox.isChecked());
            } else {
//                Intent intent4 = new Intent(getActivity(), LocaleFileBrowser.class);
//                intent4.putExtra("title", new File(down.get(position)).getName());
//                intent4.putExtra("startPath", down.get(position));
//                startActivity(intent4);
                VideoInfo videoInfo = new VideoInfo();
                videoInfo.setPath(down.get(position));
                videoInfo.setName(new File(down.get(position)).getName());
                VideoHelper.getVideoHelper().playerVideo(getContext(), videoInfo);
            }
        });
        downloadedAdapter.setOnItemLongClickListener(position -> {
            changeStateDown = true;
            htMenu.setVisibility(View.VISIBLE);
            downloadedAdapter.updateView(true);
        });

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MyConstants.DOWNLOADED_BT);
        downloadedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (!intent.getStringExtra(MyConstants.DOWNLOADED_BT).equals("")) {
                    down.add(intent.getStringExtra(MyConstants.DOWNLOADED_BT));
                    downloadedAdapter.notifyDataSetChanged();
                    btCacheSerVice.storeList(getContext(), down, MyConstants.DOWNLOADED_BT);
                }
            }
        };
        getContext().registerReceiver(downloadedReceiver, intentFilter);
        htSelectAll.setOnClickListener(v -> {
            if (htSelectAll.getText().equals("全选")) {
                downloadedAdapter.updateViewSelectedAll(true);
                for (int i = 0; i < down.size(); i++) {
                    map.put(i, true);
                }
                htSelectAll.setText("取消全选");
            } else {
                downloadedAdapter.updateViewSelectedAll(false);
                for (int i = 0; i < down.size(); i++) {
                    map.put(i, false);
                }
                htSelectAll.setText("全选");
            }
        });
        btDelete.setOnClickListener(v -> new Thread(() -> {
            List<String> dlList = new ArrayList<>();
            for (int i = 0; i < down.size(); i++) {
                if (map.get(i) != null) {
                    if (map.get(i)) {
                        dlList.add(down.get(i));
                    }
                }
            }
            for (int j = 0; j < dlList.size(); j++) {
                down.remove(dlList.get(j));
                FileUtils.delete(dlList.get(j));
            }

            getActivity().runOnUiThread(() -> {
                changeStateDown = false;
                downloadedAdapter.updateView(false);
                downloadedAdapter.updateViewSelectedAll(false);
                htMenu.setVisibility(View.GONE);
                htSelectAll.setText("全选");
                btCacheSerVice.storeList(getContext(), down, MyConstants.DOWNLOADED_BT);
            });

        }).start());

        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        getContext().unregisterReceiver(downloadedReceiver);
    }
    public void updateViewToNormal() {
        if (changeStateDown) {
            changeStateDown = false;
            htMenu.setVisibility(View.GONE);
            downloadedAdapter.updateView(false);
            downloadedAdapter.updateViewSelectedAll(false);
            htSelectAll.setText("全选");
        } else {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 注意
            intent.addCategory(Intent.CATEGORY_HOME);
            this.startActivity(intent);
        }

    }
}
