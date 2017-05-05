package com.software.videoplayer.activity.music;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.software.videoplayer.R;
import com.software.videoplayer.adapter.SongAdapter;
import com.software.videoplayer.data.model.Folder;
import com.software.videoplayer.data.model.PlayList;
import com.software.videoplayer.data.model.Song;
import com.software.videoplayer.data.source.AppRepository;
import com.software.videoplayer.event.PlayListNowEvent;
import com.software.videoplayer.event.PlaySongEvent;
import com.software.videoplayer.interfaces.OnItemClickListener;
import com.software.videoplayer.interfaces.PlayListDetailsContract;
import com.software.videoplayer.ui.DefaultDividerDecoration;
import com.software.videoplayer.util.PlayListDetailsPresenter;
import com.software.videoplayer.util.RxBus;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by txh on 2017/4/12.
 */

public class PlayListActivity extends BaseActivity implements PlayListDetailsContract.View, SongAdapter.ActionCallback {

    private PlayListDetailsContract.Presenter mPresenter;
    private static final String TAG = "PlayListDetailsActivity";

    public static final String EXTRA_FOLDER = "extraFolder";
    public static final String EXTRA_PLAY_LIST = "extraPlayList";
    public static final String EXTRA_MUSIC_LIST = "extraMusicList";

  /*  @BindView(R.id.toolbar)
    TextView toolbar;*/
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.text_view_empty)
    View emptyView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    private PlayList mPlayList;
    private boolean isFolder;
    private SongAdapter mAdapter;
    int mDeleteIndex;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list);
        ButterKnife.bind(this);
        Folder folder = getIntent().getParcelableExtra(EXTRA_FOLDER);

        if (getIntent().getAction().equals(EXTRA_PLAY_LIST)){
            mPlayList = getIntent().getParcelableExtra(EXTRA_PLAY_LIST);
        }else if (getIntent().getAction().equals(EXTRA_MUSIC_LIST)){
            mPlayList = getIntent().getParcelableExtra(EXTRA_MUSIC_LIST);
        }

        if (folder == null && mPlayList == null) {
            Log.e(TAG, "onCreate: folder & play list can't be both null!");
            finish();
        }
        if (folder != null) {
            isFolder = true;
            mPlayList = PlayList.fromFolder(folder);
        }
//        supportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(mPlayList.getName());
        }

        mAdapter = new SongAdapter(this, mPlayList.getSongs());
        mAdapter.setActionCallback(this);
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                RxBus.getInstance().post(new PlayListNowEvent(mPlayList, position));
            }
        });
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new DefaultDividerDecoration());
        emptyView.setVisibility(mPlayList.getNumOfSongs() > 0 ? View.GONE : View.VISIBLE);
        Log.e("txh","songs:"+mPlayList.getNumOfSongs());

        new PlayListDetailsPresenter(AppRepository.getInstance(), this).subscribe();
    }

    @Override
    protected void onDestroy() {
        mPresenter.unsubscribe();
        super.onDestroy();
        mPlayList = null;
    }

    public static Intent launchIntentForFolder(Context context, Folder folder) {
        Intent intent = new Intent(context, PlayListActivity.class);
        intent.putExtra(EXTRA_FOLDER, folder);
        return intent;
    }

    public static Intent launchIntentForPlayList(Context context, PlayList playList) {
        Intent intent = new Intent(context, PlayListActivity.class);
        intent.setAction(EXTRA_PLAY_LIST);
        intent.putExtra(EXTRA_PLAY_LIST, playList);
        return intent;
    }

    public static Intent launchIntentForMusicList(Context context, PlayList musicList) {
        Intent intent = new Intent(context, PlayListActivity.class);
        intent.setAction(EXTRA_MUSIC_LIST);
        intent.putExtra(EXTRA_MUSIC_LIST, musicList);
        return intent;
    }

    @Override
    public void setPresenter(PlayListDetailsContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void handleError(Throwable e) {
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSongDeleted(Song song) {
        mAdapter.notifyItemRemoved(mDeleteIndex);
        mAdapter.updateSummaryText();
    }

    @Override
    public void onAction(View actionView, int position) {
        final Song song = mAdapter.getItem(position);
        PopupMenu actionMenu = new PopupMenu(this, actionView, Gravity.END | Gravity.BOTTOM);
        actionMenu.inflate(R.menu.music_action);
//        actionMenu.getMenu().findItem(R.id.menu_item_delete).setVisible(!isFolder);
        actionMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_item_add_to_play_list:
                        RxBus.getInstance().post(new PlaySongEvent(song));
                        finish();
                        break;
                  /*  case R.id.menu_item_delete:
                        mDeleteIndex = position;
                        mPresenter.delete(song, mPlayList);
                        break;*/
                }
                return true;
            }
        });
        actionMenu.show();
    }
}
