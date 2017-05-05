package com.software.videoplayer.fragment.music;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.software.videoplayer.R;
import com.software.videoplayer.activity.music.MusicPlayerActivity;
import com.software.videoplayer.activity.video.HistoryActivity;
import com.software.videoplayer.adapter.LocalMusicAdapter;
import com.software.videoplayer.adapter.VideoHistoryShowAdapter;
import com.software.videoplayer.constants.MyConstants;
import com.software.videoplayer.constants.VideoConstants;
import com.software.videoplayer.data.Stroe;
import com.software.videoplayer.data.model.PlayList;
import com.software.videoplayer.data.model.Song;
import com.software.videoplayer.data.source.AppRepository;
import com.software.videoplayer.event.PlayListUpdatedEvent;
import com.software.videoplayer.event.PlaySongEvent;
import com.software.videoplayer.fragment.video.VideoFragment;
import com.software.videoplayer.interfaces.IAdapterView;
import com.software.videoplayer.interfaces.LocalMusicContract;
import com.software.videoplayer.interfaces.OnItemLongClickListener;
import com.software.videoplayer.model.VideoInfo;
import com.software.videoplayer.ui.DefaultDividerDecoration;
import com.software.videoplayer.ui.widget.RecyclerViewFastScroller;
import com.software.videoplayer.util.LocalMusicPresenter;
import com.software.videoplayer.util.Player;
import com.software.videoplayer.util.RxBus;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class MusicFragment extends MusicBaseFragment implements LocalMusicContract.View {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.fast_scroller)
    RecyclerViewFastScroller fastScroller;
    @BindView(R.id.text_view_empty)
    View emptyView;
    @BindView(R.id.ht_select_all)
    TextView htSelectAll;
    @BindView(R.id.ht_delete)
    TextView htDelete;
    @BindView(R.id.ht_menu)
    LinearLayout htMenu;

    LocalMusicAdapter mAdapter;
    private boolean changeState = false;
    LocalMusicContract.Presenter mPresenter;
    private ProgressDialog progressDialog;
    private Map<Integer, Boolean> selectedMap = new HashMap<>();
    private List<Song> history_show = new ArrayList<>();

    /**
     * 单例对象实例
     */
    private static MusicFragment instance = null;

    public static MusicFragment getInstance() {
        if (instance == null) {
            instance = new MusicFragment();
        }
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_all_local_music, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
//        initProgressDialog();
        mAdapter = new LocalMusicAdapter(getActivity(), null);
        Log.e("txh",getActivity()+"");
        mAdapter.setOnItemLongClickListener(onItemLongClickListener);
        mAdapter.setOnItemClickListener(position -> {
            if (changeState) {
                mAdapter.updateViewAllSelect(0);
                int firVisPos = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                IAdapterView childAt = (IAdapterView) recyclerView.getChildAt(position - firVisPos);
                if (childAt == null) return;
                CheckBox checkBox = childAt.getCheckBox();
                checkBox.toggle();
                selectedMap.put(position, checkBox.isChecked());
            } else {
                startActivity(new Intent(getActivity(), MusicPlayerActivity.class));
                Song song = mAdapter.getItem(position);
                RxBus.getInstance().post(new PlaySongEvent(song));
                PlayList playList = new PlayList();
                playList.addSong(song);
                Player.getInstance().play(song);
            }
        });
        history_show = MyConstants.songsList;
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new DefaultDividerDecoration());

        fastScroller.setRecyclerView(recyclerView);

        new LocalMusicPresenter(AppRepository.getInstance(), this).subscribe();

        IntentFilter in = new IntentFilter();
        in.addAction(MyConstants.UPDATE_HISTORY);
        getActivity().registerReceiver(mBR, in);
    }

    @SuppressWarnings("unchecked")
    BroadcastReceiver mBR = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MyConstants.UPDATE_HISTORY)) {
                VideoInfo videoInfo = intent.getParcelableExtra(MyConstants.UPDATE_HISTORY);
                history_show.remove(videoInfo);
                mAdapter.notifyDataSetChanged();
            }
        }
    };

    private void initProgressDialog() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("加载中...");
        progressDialog.show();
    }

    // RxBus Events

    @Override
    protected Subscription subscribeEvents() {
        return RxBus.getInstance().toObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(o -> {
                    if (o instanceof PlayListUpdatedEvent) {
                        mPresenter.loadLocalMusic();
                    }
                })
                .subscribe();
    }

    // MVP View
    @Override
    public void emptyView(boolean visible) {
        emptyView.setVisibility(visible ? View.VISIBLE : View.GONE);
        fastScroller.setVisibility(visible ? View.GONE : View.VISIBLE);
       /* if (visible){
            progressDialog.show();
        }else {
            progressDialog.hide();
        }*/
    }

    @Override
    public void handleError(Throwable error) {
        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocalMusicLoaded(List<Song> songs) {
        Log.e("txh",songs.size()+"");
        //同步数据到侧滑菜单的本地播放列表中
        if (MyConstants.songsList.size() != 0){
            MyConstants.songsList.clear();
        }
        for (int i = 0; i < songs.size(); i++) {
            MyConstants.songsList.add(songs.get(i));
        }

        //将本地数据显示出来
        mAdapter.setData( MyConstants.songsList);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void setPresenter(LocalMusicContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @OnClick({R.id.ht_select_all, R.id.ht_delete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ht_select_all:
                if (htSelectAll.getText().toString().equals("全选")) {
                    mAdapter.updateViewAllSelect(1);
                    htSelectAll.setText("取消全选");
                    for (int i = 0; i < history_show.size(); i++) {
                        selectedMap.put(i, true);
                        MyConstants.checkedFlag[i] = true;
                    }
                } else {
                    mAdapter.updateViewAllSelect(2);
                    htSelectAll.setText("全选");
                    for (int i = 0; i < history_show.size(); i++) {
                        selectedMap.put(i, false);
                        MyConstants.checkedFlag[i] = false;
                    }
                }
                break;
            case R.id.ht_delete:
                List<Song> dlList = new ArrayList<>();
                for (int i = 0; i < MyConstants.songsList.size(); i++) {
                    if (selectedMap.get(i) != null) {
                        if (selectedMap.get(i)) {
                            dlList.add(history_show.get(i));
                            MyConstants.checkedFlag[i] = false;//改变删除条目的选择状态
                        }
                    }
                }
                for (int j = 0; j < dlList.size(); j++) {
                    MyConstants.songsList.remove(dlList.get(j));
                    File file = new File(dlList.get(j).getPath());
                    file.delete();
                }
                selectedMap.clear();
                mAdapter.updateToNormal(0, false);
                changeState = false;
                htMenu.setVisibility(View.GONE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Stroe.storeData(getActivity(), VideoConstants.HISTORY, history_show);
                        getActivity().sendBroadcast(new Intent(MyConstants.UPDATE_HISTORY));
                    }
                }).start();
                break;
        }
    }

    public OnItemLongClickListener onItemLongClickListener = new OnItemLongClickListener() {
        @Override
        public void onItemClick(int position) {
            mAdapter.updateView(true);
            changeState = true;
            htMenu.setVisibility(View.VISIBLE);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mBR);
    }
}
