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
import com.software.videoplayer.adapter.DownloadingBtAdapter;
import com.software.videoplayer.constants.MyConstants;
import com.software.videoplayer.data.Stroe;
import com.software.videoplayer.service.BtCacheSerVice;
import com.software.videoplayer.ui.DefaultDividerDecoration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class DownloadingBtFragment extends Fragment{

    @BindView(R.id.downloading_bt)
    RecyclerView downloadingBt;
    Unbinder unbinder;
    @BindView(R.id.ht_select_all)
    TextView htSelectAll;
    @BindView(R.id.ht_delete)
    TextView htDelete;
    @BindView(R.id.ht_menu)
    LinearLayout htMenu;
    private BroadcastReceiver broadcastReceiver;
    private List<String> magnetList = new ArrayList<>();
    DownloadingBtAdapter downloadingBtAdapter;
    public  boolean changeState = false;
    private Map<Integer, Boolean> checkMap = new HashMap<>();
    private BtCacheSerVice btCacheSerVice= new BtCacheSerVice();

    @Override
    @SuppressWarnings("unchecked")
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dolading_bt, container, false);
        unbinder = ButterKnife.bind(this, view);
        if (Stroe.getData(getContext(), MyConstants.BT_DOWNLOADING, String.class) != null) {
            magnetList = (List<String>) Stroe.getData(getContext(), MyConstants.BT_DOWNLOADING, String.class);
        }
        downloadingBtAdapter = new DownloadingBtAdapter(getContext(), magnetList);
        downloadingBt.addItemDecoration(new DefaultDividerDecoration());
        downloadingBt.setAdapter(downloadingBtAdapter);
        getInfo();
        downloadingBtAdapter.setOnItemClickListener(position -> {
            if (changeState) {
                DownloadingBtAdapter.BtViewHolder viewHolder = (DownloadingBtAdapter.BtViewHolder) downloadingBt.getChildViewHolder(downloadingBt.getChildAt(position));
                viewHolder.btCheck.toggle();
                checkMap.put(position, viewHolder.btCheck.isChecked());
            }
        });
        downloadingBtAdapter.setOnItemLongClickListener(position -> {
            changeState= true;
            downloadingBtAdapter.updateView(true);
            htMenu.setVisibility(View.VISIBLE);
        });

        htSelectAll.setOnClickListener(v -> {
            if (htSelectAll.getText().equals("全选")) {
                downloadingBtAdapter.updateViewAllSelect(true);
                htSelectAll.setText("取消全选");
                for (int i=0;i<magnetList.size();i++) {
                    checkMap.put(i, true);
                }
            } else {
                downloadingBtAdapter.updateViewAllSelect(false);
                htSelectAll.setText("全选");
                for (int i=0;i<magnetList.size();i++) {
                    checkMap.put(i, false);
                }
            }
        });

        htDelete.setOnClickListener(v -> new Thread(() -> {
            List<String> dlList = new ArrayList<>();

            for (int i=0;i<magnetList.size();i++) {
                if (checkMap.get(i) != null) {
                    if (checkMap.get(i)) {
                        dlList.add(magnetList.get(i));
                    }
                }
            }
            for (int k=0;k<dlList.size();k++) {
                magnetList.remove(dlList.get(k));
                downloadingBtAdapter.deleteFileDownloading(dlList.get(k));
            }
            getActivity().runOnUiThread(() -> {
                changeState  = false;
                htMenu.setVisibility(View.GONE);
                downloadingBtAdapter.updateView(false);
                downloadingBtAdapter.updateViewAllSelect(false);
                btCacheSerVice.storeList(getActivity(), magnetList, MyConstants.BT_DOWNLOADING);
            });
        }).start());

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        getContext().unregisterReceiver(broadcastReceiver);
    }

    public void getInfo() {

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MyConstants.NEW_BT_TASK);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null) {
                    String magnet = intent.getStringExtra("magnet");
                    if (!magnetList.contains(magnet)) {
                        magnetList.add(magnet);
                        downloadingBtAdapter.notifyDataSetChanged();
                        btCacheSerVice.storeList(getContext(),magnetList, MyConstants.BT_DOWNLOADING);
                    }
                }

            }
        };
        getContext().registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getContext().unregisterReceiver(broadcastReceiver);
    }


    public void updateViewToNormal() {
        if (changeState) {
            htMenu.setVisibility(View.GONE);
            downloadingBtAdapter.updateView(false);
            downloadingBtAdapter.updateViewAllSelect(false);
            changeState = false;
        } else {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 注意
            intent.addCategory(Intent.CATEGORY_HOME);
            getContext().startActivity(intent);
        }
    }
}
