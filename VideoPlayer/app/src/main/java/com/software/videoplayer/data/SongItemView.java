package com.software.videoplayer.data;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.software.videoplayer.R;
import com.software.videoplayer.data.model.Song;
import com.software.videoplayer.interfaces.IAdapterView;
import com.software.videoplayer.util.TimeUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/11/16
 * Time: 6:27 AM
 * Desc: SongItemView
 */
public class SongItemView extends RelativeLayout implements IAdapterView<Song> {

    @BindView(R.id.text_view_index)
    TextView textViewIndex;
    @BindView(R.id.text_view_name)
    TextView textViewName;
    @BindView(R.id.text_view_info)
    TextView textViewInfo;
    @BindView(R.id.layout_action)
    public View buttonAction;

    public SongItemView(Context context) {
        super(context);
        View.inflate(context, R.layout.item_play_list_details_song, this);
        ButterKnife.bind(this);
    }

    @Override
    public void bind(Song song, int position) {
        textViewIndex.setText(String.valueOf(position + 1));
        textViewName.setText(song.getDisplayName());
        textViewInfo.setText(String.format("%s | %s", TimeUtils.formatDuration(song.getDuration()), song.getArtist()));
    }

    @Override
    public CheckBox getCheckBox() {
        return null;
    }
}
