package com.software.videoplayer.fragment.music;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.software.videoplayer.R;
import com.software.videoplayer.activity.music.PlayListActivity;
import com.software.videoplayer.adapter.PlayListAdapter;
import com.software.videoplayer.bean.playListbean;
import com.software.videoplayer.constants.MyConstants;
import com.software.videoplayer.data.model.PlayList;
import com.software.videoplayer.data.model.Song;
import com.software.videoplayer.data.source.AppRepository;
import com.software.videoplayer.data.source.PreferenceManager;
import com.software.videoplayer.event.PlayListNowEvent;
import com.software.videoplayer.event.PlaySongEvent;
import com.software.videoplayer.interfaces.IPlayback;
import com.software.videoplayer.interfaces.MusicPlayerContract;
import com.software.videoplayer.model.PlayMode;
import com.software.videoplayer.service.PlaybackService;
import com.software.videoplayer.ui.SlidingMenu;
import com.software.videoplayer.ui.widget.ShadowImageView;
import com.software.videoplayer.util.AlbumUtils;
import com.software.videoplayer.util.MusicPlayerPresenter;
import com.software.videoplayer.util.RxBus;
import com.software.videoplayer.util.SPUtils;
import com.software.videoplayer.util.TimeUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/1/16
 * Time: 9:58 PM
 * Desc: MusicPlayerFragment
 */

public class MusicPlayerFragment extends MusicBaseFragment implements MusicPlayerContract.View, IPlayback.Callback {

    // private static final String TAG = "MusicPlayerFragment";

    // Update seek bar every second
    private static final long UPDATE_PROGRESS_INTERVAL = 1000;

    @BindView(R.id.image_view_album)
    ShadowImageView imageViewAlbum;
    @BindView(R.id.text_view_name)
    TextView textViewName;
    @BindView(R.id.text_view_artist)
    TextView textViewArtist;
    @BindView(R.id.text_view_progress)
    TextView textViewProgress;
    @BindView(R.id.text_view_duration)
    TextView textViewDuration;
    @BindView(R.id.seek_bar)
    SeekBar seekBarProgress;

    @BindView(R.id.button_play_mode_toggle)
    ImageView buttonPlayModeToggle;
    @BindView(R.id.button_play_toggle)
    ImageView buttonPlayToggle;
    @BindView(R.id.button_favorite_toggle)
    ImageView buttonFavoriteToggle;
    @BindView(R.id.mp_back)
    ImageView mpBack;
    @BindView(R.id.mp_list)
    ImageView mpList;
    @BindView(R.id.layout_progress)
    LinearLayout layoutProgress;
    @BindView(R.id.button_play_last)
    AppCompatImageView buttonPlayLast;
    @BindView(R.id.button_play_next)
    AppCompatImageView buttonPlayNext;
    @BindView(R.id.layout_play_controls)
    LinearLayout layoutPlayControls;
    @BindView(R.id.drawerLayout)
    DrawerLayout drawerLayout;
    @BindView(R.id.left_drawer)
    LinearLayout left_drawer;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private IPlayback mPlayer;

    private Handler mHandler = new Handler();

    private MusicPlayerContract.Presenter mPresenter;

    private Runnable mProgressCallback = new Runnable() {
        @Override
        public void run() {
            if (isDetached()) return;

            if (mPlayer.isPlaying()) {
                int progress = (int) (seekBarProgress.getMax()
                        * ((float) mPlayer.getProgress() / (float) getCurrentSongDuration()));
                updateProgressTextWithDuration(mPlayer.getProgress());
                if (progress >= 0 && progress <= seekBarProgress.getMax()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        seekBarProgress.setProgress(progress, true);
                    } else {
                        seekBarProgress.setProgress(progress);
                    }
                    mHandler.postDelayed(this, UPDATE_PROGRESS_INTERVAL);
                }
            }
        }
    };
    private Context context;
    private PlayListAdapter playListAdapter;
    PlayList playList;
    private ArrayList<PlayList> playListArrayListList = new ArrayList<PlayList>();
    private PlayList musicList;

    private PlayList playList1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_player, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        context = getActivity();
        initPlayList();

        seekBarProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    updateProgressTextWithProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mHandler.removeCallbacks(mProgressCallback);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekTo(getDuration(seekBar.getProgress()));
                if (mPlayer.isPlaying()) {
                    mHandler.removeCallbacks(mProgressCallback);
                    mHandler.post(mProgressCallback);
                }
            }
        });

        new MusicPlayerPresenter(getActivity(), AppRepository.getInstance(), this).subscribe();

        mpBack.setOnClickListener(view1 -> getActivity().finish());
        mpList.setOnClickListener(view1 -> showPlayList());
    }

    private void initPlayList() {
        playList1 = new PlayList();
        playList1.setName("收藏列表");
        playList1.setNumOfSongs(MyConstants.songs.size());
        playList1.setFavorite(true);
        playList1.setAlbum(R.drawable.ic_favorite_yes);
        playListArrayListList.add(0,playList1);
        playList = playListArrayListList.get(0);

        PlayList playList2 = new PlayList();
        playList2.setName("本地列表");
        playList2.setNumOfSongs(MyConstants.songsList.size());
        playList2.setFavorite(false);
        playList2.setAlbum(R.drawable.ic_main_nav_music_selected);
        playListArrayListList.add(1,playList2);
        musicList = playListArrayListList.get(1);
        musicList.setSongs(MyConstants.songsList);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        playListAdapter = new PlayListAdapter(context, this.playListArrayListList);
        recyclerView.setAdapter(playListAdapter);
        playListAdapter.setOnItemClickListener(onItemClickListener);//条目点击事件
    }

    private void showPlayList() {
        if (drawerLayout.isDrawerOpen(left_drawer)){
            drawerLayout.closeDrawer(left_drawer);
        }else {
            drawerLayout.openDrawer(left_drawer);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mPlayer != null && mPlayer.isPlaying()) {
            mHandler.removeCallbacks(mProgressCallback);
            mHandler.post(mProgressCallback);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mHandler.removeCallbacks(mProgressCallback);
    }

    @Override
    public void onDestroyView() {
        mPresenter.unsubscribe();
        super.onDestroyView();
    }

    // Click Events

    @OnClick(R.id.button_play_toggle)
    public void onPlayToggleAction(View view) {
        if (mPlayer == null) return;

        if (mPlayer.isPlaying()) {
            mPlayer.pause();
        } else {
            mPlayer.play();
        }
    }

    @OnClick(R.id.button_play_mode_toggle)
    public void onPlayModeToggleAction(View view) {
        if (mPlayer == null) return;

        PlayMode current = PreferenceManager.lastPlayMode(getActivity());
        PlayMode newMode = PlayMode.switchNextMode(current);
        PreferenceManager.setPlayMode(getActivity(), newMode);
        mPlayer.setPlayMode(newMode);
        updatePlayMode(newMode);
    }

    @OnClick(R.id.button_play_last)
    public void onPlayLastAction(View view) {
        if (mPlayer == null) return;

        mPlayer.playLast();
    }

    @OnClick(R.id.button_play_next)
    public void onPlayNextAction(View view) {
        if (mPlayer == null) return;

        mPlayer.playNext();
    }

    @OnClick(R.id.button_favorite_toggle)
    public void onFavoriteToggleAction(View view) {
        if (mPlayer == null) return;

      /*  Song currentSong = mPlayer.getPlayingSong();
        if (currentSong != null) {
            if (buttonFavoriteToggle.isSelected()){
                buttonFavoriteToggle.setSelected(false);
                currentSong.setFavorite(false);
                MyConstants.songs.remove(currentSong);
                playList.removeSong(currentSong);
            }else {
                currentSong.setFavorite(true);
                buttonFavoriteToggle.setSelected(true);
                MyConstants.songs.add(currentSong);
                playList.addSong(currentSong);
            }
            for (int i = 0; i < MyConstants.songsList.size(); i++) {
                for (int i1 = 0; i1 < MyConstants.songs.size(); i1++) {
                    if (MyConstants.songsList.get(i) == MyConstants.songs.get(i1)){
                        MyConstants.songsList.get(i).setFavorite(true);
                    }else {
                        MyConstants.songsList.get(i).setFavorite(false);
                    }
                }
            }
        }
        handler.obtainMessage(MUSICS,MyConstants.songs.size()).sendToTarget();*/

        Song currentSong = mPlayer.getPlayingSong();
        if (currentSong != null) {
            view.setEnabled(false);
            mPresenter.setSongAsFavorite(currentSong, !currentSong.isFavorite());
        }
    }

    // RXBus Events

    @Override
    protected Subscription subscribeEvents() {
        return RxBus.getInstance().toObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        if (o instanceof PlaySongEvent) {
                            onPlaySongEvent((PlaySongEvent) o);
                        } else if (o instanceof PlayListNowEvent) {
                            onPlayListNowEvent((PlayListNowEvent) o);
                        }
                    }
                })
                .subscribe(RxBus.defaultSubscriber());
    }

    private void onPlaySongEvent(PlaySongEvent event) {
        Song song = event.song;
        playSong(song);
    }

    private void onPlayListNowEvent(PlayListNowEvent event) {
        PlayList playList = event.playList;
        int playIndex = event.playIndex;
        playSong(playList, playIndex);
    }

    // Music Controls

    private void playSong(Song song) {
        PlayList playList = new PlayList(song);
        playSong(playList, 0);
    }

    private void playSong(PlayList playList, int playIndex) {
        if (playList == null) return;

        playList.setPlayMode(PreferenceManager.lastPlayMode(getActivity()));
        // boolean result =
        mPlayer.play(playList, playIndex);

        Song song = playList.getCurrentSong();
        onSongUpdated(song);

        /*
        seekBarProgress.setProgress(0);
        seekBarProgress.setEnabled(result);
        textViewProgress.setText(R.string.mp_music_default_duration);

        if (result) {
            imageViewAlbum.startRotateAnimation();
            buttonPlayToggle.setImageResource(R.drawable.ic_pause);
            textViewDuration.setText(TimeUtils.formatDuration(song.getDuration()));
        } else {
            buttonPlayToggle.setImageResource(R.drawable.ic_play);
            textViewDuration.setText(R.string.mp_music_default_duration);
        }

        mHandler.removeCallbacks(mProgressCallback);
        mHandler.post(mProgressCallback);

        getActivity().startService(new Intent(getActivity(), PlaybackService.class));
        */
    }

    private void updateProgressTextWithProgress(int progress) {
        int targetDuration = getDuration(progress);
        textViewProgress.setText(TimeUtils.formatDuration(targetDuration));
    }

    private void updateProgressTextWithDuration(int duration) {
        textViewProgress.setText(TimeUtils.formatDuration(duration));
    }

    private void seekTo(int duration) {
        mPlayer.seekTo(duration);
    }

    private int getDuration(int progress) {
        return (int) (getCurrentSongDuration() * ((float) progress / seekBarProgress.getMax()));
    }

    private int getCurrentSongDuration() {
        Song currentSong = mPlayer.getPlayingSong();
        int duration = 0;
        if (currentSong != null) {
            duration = currentSong.getDuration();
        }
        return duration;
    }

    // Player Callbacks

    @Override
    public void onSwitchLast(Song last) {
        onSongUpdated(last);
    }

    @Override
    public void onSwitchNext(Song next) {
        onSongUpdated(next);
    }

    @Override
    public void onComplete(Song next) {
        onSongUpdated(next);
    }

    @Override
    public void onPlayStatusChanged(boolean isPlaying) {
        updatePlayToggle(isPlaying);
        if (isPlaying) {
            imageViewAlbum.resumeRotateAnimation();
            mHandler.removeCallbacks(mProgressCallback);
            mHandler.post(mProgressCallback);
        } else {
            imageViewAlbum.pauseRotateAnimation();
            mHandler.removeCallbacks(mProgressCallback);
        }
    }

    // MVP View

    @Override
    public void handleError(Throwable error) {
        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPlaybackServiceBound(PlaybackService service) {
        mPlayer = service;
        mPlayer.registerCallback(this);
    }

    @Override
    public void onPlaybackServiceUnbound() {
        mPlayer.unregisterCallback(this);
        mPlayer = null;
    }

    @Override
    public void onSongSetAsFavorite(@NonNull Song song) {
        Log.e("txh","song.path:"+ song.getPath());
        buttonFavoriteToggle.setEnabled(true);
        updateFavoriteToggle(song.isFavorite());
        if (song.isFavorite()){
            MyConstants.songs.add(song);
        }else {
            MyConstants.songs.remove(song);
        }
        playList1.setNumOfSongs(MyConstants.songs.size());
        playListAdapter.notifyDataSetChanged();
    }
    public int index;
    PlayListAdapter.OnItemClickListener onItemClickListener = new PlayListAdapter.OnItemClickListener() {
        @Override
        public void onClick(View view, int position) {
             index = position;
            if (view.getId() == R.id.image_button_action){ //点击条目右边的  image_button_action
                PopupMenu actionMenu = new PopupMenu(getActivity(),view, Gravity.END | Gravity.BOTTOM);
                actionMenu.inflate(R.menu.play_list_action);
                actionMenu.getMenu().findItem(R.id.menu_item_rename).setVisible(false);
                actionMenu.getMenu().findItem(R.id.menu_item_delete).setVisible(false);
                actionMenu.setOnMenuItemClickListener(onMenuItemClickListener);
                actionMenu.show();
            }else { //点击的是条目
                switch (position) {
                    case 0://点击收藏条目
                        List<Song> songs = playList.getSongs();
                        songs.clear();
                        playList.addSong(MyConstants.songs, position);//收藏
                        startActivity(PlayListActivity.launchIntentForPlayList(getActivity(), playList));
                        break;

                    case 1://点击音乐列表条目
                        startActivity(PlayListActivity.launchIntentForMusicList(getActivity(), musicList));
                        break;
                }
            }
        }
    };

    PopupMenu.OnMenuItemClickListener onMenuItemClickListener = new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (item.getItemId() == R.id.menu_item_play_now) {
                PlayListNowEvent playListNowEvent = null;
                if (index == 0){
                    Log.e("txh","playList.getSongs():"+playList.getSongs());
                    if (playList.getSongs().size() > 1) {
                        playListNowEvent = new PlayListNowEvent(playList, 0);
                    }
                }else {
                    Log.e("txh","musicList.getSongs():"+musicList.getSongs());
                    if (musicList.getSongs().size() > 1) {
                        playListNowEvent = new PlayListNowEvent(musicList, 0);
                    }
                }
                RxBus.getInstance().post(playListNowEvent);
            }
            return true;
        }
    };

    public void onSongUpdated(@Nullable Song song) {
        if (song == null) {
            imageViewAlbum.cancelRotateAnimation();
            buttonPlayToggle.setImageResource(R.drawable.ic_play);
            seekBarProgress.setProgress(0);
            updateProgressTextWithProgress(0);
            seekTo(0);
            mHandler.removeCallbacks(mProgressCallback);
            return;
        }

        // Step 1: Song name and artist
        textViewName.setText(song.getDisplayName());
        textViewArtist.setText(song.getArtist());
        // Step 2: favorite
        buttonFavoriteToggle.setImageResource(song.isFavorite() ? R.drawable.ic_favorite_yes : R.drawable.ic_favorite_no);
        // Step 3: Duration
        textViewDuration.setText(TimeUtils.formatDuration(song.getDuration()));
        // Step 4: Keep these things updated
        // - Album rotation
        // - Progress(textViewProgress & seekBarProgress)
        Bitmap bitmap = AlbumUtils.parseAlbum(song);
        if (bitmap == null) {
            imageViewAlbum.setImageResource(R.drawable.default_record_album);
        } else {
            imageViewAlbum.setImageBitmap(AlbumUtils.getCroppedBitmap(bitmap));
        }
        imageViewAlbum.pauseRotateAnimation();
        mHandler.removeCallbacks(mProgressCallback);
        if (mPlayer.isPlaying()) {
            imageViewAlbum.startRotateAnimation();
            mHandler.post(mProgressCallback);
            buttonPlayToggle.setImageResource(R.drawable.ic_pause);
        }
    }

    @Override
    public void updatePlayMode(PlayMode playMode) {
        if (playMode == null) {
            playMode = PlayMode.getDefault();
        }
        switch (playMode) {
            case LIST:
                buttonPlayModeToggle.setImageResource(R.drawable.ic_play_mode_list);
                break;
            case LOOP:
                buttonPlayModeToggle.setImageResource(R.drawable.ic_play_mode_loop);
                break;
            case SHUFFLE:
                buttonPlayModeToggle.setImageResource(R.drawable.ic_play_mode_shuffle);
                break;
            case SINGLE:
                buttonPlayModeToggle.setImageResource(R.drawable.ic_play_mode_single);
                break;
        }
    }

    @Override
    public void updatePlayToggle(boolean play) {
        buttonPlayToggle.setImageResource(play ? R.drawable.ic_pause : R.drawable.ic_play);
    }

    @Override
    public void updateFavoriteToggle(boolean favorite) {
        buttonFavoriteToggle.setImageResource(favorite ? R.drawable.ic_favorite_yes : R.drawable.ic_favorite_no);
    }

    @Override
    public void setPresenter(MusicPlayerContract.Presenter presenter) {
        mPresenter = presenter;
    }
}
