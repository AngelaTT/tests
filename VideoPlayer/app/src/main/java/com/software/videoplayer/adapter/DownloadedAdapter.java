package com.software.videoplayer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.software.videoplayer.R;
import com.software.videoplayer.interfaces.OnItemClickListener;
import com.software.videoplayer.interfaces.OnItemLongClickListener;
import com.software.videoplayer.util.VideoHelper;

import java.io.File;
import java.util.List;

/**
 * User: Moon
 * Data: 2017/4/5.
 */

public class DownloadedAdapter extends RecyclerView.Adapter<DownloadedAdapter.DownloadedViewHolder> {

    private Context context;
    private List<String> downloaded;

    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    private boolean isSelected = false;
    private boolean isSelectedAll = false;

    public DownloadedAdapter(Context context,List<String> downloaded) {

        this.context = context;
        this.downloaded = downloaded;

    }

    @Override
    public DownloadedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DownloadedViewHolder(LayoutInflater.from(context).inflate(R.layout.bt_downloaded, parent, false));
    }

    @Override
    public void onBindViewHolder(DownloadedViewHolder holder, int position) {
        File file = new File(downloaded.get(position));
        holder.name.setText(file.getName());
        holder.size.setText(VideoHelper.getVideoHelper().formatFileSize(file.length()));
        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(position));
        holder.itemView.setOnLongClickListener(v -> {onItemLongClickListener.onItemClick(position);return true;});
        if (isSelected) {
            holder.checkBox.setVisibility(View.VISIBLE);
            if (isSelectedAll) {
                holder.checkBox.setChecked(true);
            } else {
                holder.checkBox.setChecked(false);
            }
        } else {
            holder.checkBox.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return downloaded.size();
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

    public void updateViewSelectedAll(boolean isSelectedAll) {
        this.isSelectedAll = isSelectedAll;
        notifyDataSetChanged();
    }

    public class DownloadedViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private TextView size;
        private View itemView;
        public CheckBox checkBox;

        public DownloadedViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.downloaded_name);
            size = (TextView) itemView.findViewById(R.id.downloaded_size);
            checkBox = (CheckBox) itemView.findViewById(R.id.downloaded_checkbox);
            this.itemView = itemView;
        }
    }

}

