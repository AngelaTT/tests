package com.software.videoplayer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.software.videoplayer.R;
import com.software.videoplayer.data.SongItemView;
import com.software.videoplayer.data.model.Song;

import java.util.List;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/11/16
 * Time: 6:41 AM
 * Desc: SongAdapter
 */
public class SongAdapter extends AbstractSummaryAdapter<Song, SongItemView> {

    private Context mContext;
    private ActionCallback mCallback;

    public SongAdapter(Context context, List<Song> data) {
        super(context, data);
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final RecyclerView.ViewHolder holder = super.onCreateViewHolder(parent, viewType);
        if (holder.itemView instanceof SongItemView) {
            SongItemView itemView = (SongItemView) holder.itemView;
            itemView.buttonAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();
                    if (mCallback != null) {
                        mCallback.onAction(v, position);
                    }
                }
            });
        }
        return holder;
    }

    @Override
    protected String getEndSummaryText(int dataCount) {
        return mContext.getString(R.string.mp_play_list_details_footer_end_summary_formatter, dataCount);
    }

    @Override
    protected SongItemView createView(Context context) {
        return new SongItemView(context);
    }

    // Callback

    public void setActionCallback(ActionCallback callback) {
        mCallback = callback;
    }

    public interface ActionCallback {
        void onAction(View actionView, int position);
    }
}
