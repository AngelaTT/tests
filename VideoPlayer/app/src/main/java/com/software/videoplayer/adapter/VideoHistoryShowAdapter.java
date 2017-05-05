package com.software.videoplayer.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.software.videoplayer.R;
import com.software.videoplayer.interfaces.OnItemClickListener;
import com.software.videoplayer.interfaces.OnItemLongClickListener;
import com.software.videoplayer.model.VideoInfo;

import java.util.List;

/**
 * User: Moon
 * Data: 2017/3/9.
 */

public class VideoHistoryShowAdapter extends RecyclerView.Adapter<VideoHistoryShowAdapter.ViewHolder> {

    private Context mContext;
    private List<VideoInfo> list;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    private boolean isSelected = false;
    private boolean isSelectedAll = false;



    public VideoHistoryShowAdapter(@NonNull Context context, List<VideoInfo> list) {
        mContext = context;
        this.list = list;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_local_music, parent,false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        VideoInfo videoInfo = list.get(position);
        Glide.with(mContext).load(videoInfo.getPath()).error(R.drawable.default_video).into(viewHolder.thumbnail);
        viewHolder.name.setText(videoInfo.getName());
        viewHolder.duration.setText(videoInfo.getTime());
        viewHolder.size.setText(videoInfo.getSize());
        viewHolder.relativeLayout.setOnClickListener(v -> onItemClickListener.onItemClick(position));
        viewHolder.relativeLayout.setOnLongClickListener(v -> {onItemLongClickListener.onItemClick(position);
            return true;});
        if (isSelected) {
            viewHolder.checkBox.setVisibility(View.VISIBLE);
            if (isSelectedAll) {
                viewHolder.checkBox.setChecked(true);
            } else {
                viewHolder.checkBox.setChecked(false);
            }
        } else {
            viewHolder.checkBox.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView thumbnail;
        private TextView name;
        private TextView duration;
        private TextView size;
        private RelativeLayout relativeLayout;
        public CheckBox checkBox;

        public ViewHolder(View view) {
            super(view);
            thumbnail = (ImageView) view.findViewById(R.id.music_thumb);
            thumbnail.setScaleType(ImageView.ScaleType.CENTER_CROP);
            name = (TextView) view.findViewById(R.id.text_view_name);
            duration = (TextView) view.findViewById(R.id.text_view_artist);
            size = (TextView) view.findViewById(R.id.text_view_duration);
            relativeLayout = (RelativeLayout) view.findViewById(R.id.item_root);
            checkBox = (CheckBox) view.findViewById(R.id.item_check);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }
    public void updateView(boolean isSelected) {
        this.isSelected = isSelected;
        notifyDataSetChanged();
    }
    public void updateViewAllSelect(boolean isSelectedAll) {
        this.isSelectedAll = isSelectedAll;
        notifyDataSetChanged();
    }

    public void updateToNormal(boolean isSelectedAll, boolean isSelected) {
        this.isSelectedAll = isSelectedAll;
        this.isSelected = isSelected;
        notifyDataSetChanged();
    }
}
