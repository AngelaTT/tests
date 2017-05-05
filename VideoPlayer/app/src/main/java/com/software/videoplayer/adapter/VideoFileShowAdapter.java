package com.software.videoplayer.adapter;

import android.app.Service;
import android.content.Context;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.software.videoplayer.R;
import com.software.videoplayer.model.VideoInfo;

import java.util.List;



/**
 * Created by moon on 2017/2/20.
 */

public class VideoFileShowAdapter extends BaseAdapter {

    private List<VideoInfo> mList;
    private Context mContext;
    private boolean isVideo = true;
    private boolean isSelected = false;
    private boolean isSelectedAll = false;



    public VideoFileShowAdapter(Context context, List<VideoInfo> list, boolean isVideo) {
        mList = list;
        mContext = context;
        this.isVideo = isVideo;
    }


    @Override
    public int getCount() {
        return mList.size() == 0 ? 0 : mList.size();
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.video_show_layout, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
            int mWidth = mContext.getResources().getDisplayMetrics().widthPixels;
            ViewGroup.LayoutParams layoutParams = viewHolder.imageView.getLayoutParams();
            if (isVideo) {
                layoutParams.height = (int) (mWidth / 2.5);
            } else {
                layoutParams.height = (int) (mWidth / 3.5);
            }
            viewHolder.imageView.setLayoutParams(layoutParams);

        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        if (mList.get(i).getNew()) {
            viewHolder.findNew.setVisibility(View.VISIBLE);
        } else {
            viewHolder.findNew.setVisibility(View.GONE);
        }

        if (isSelected) {
            viewHolder.selected.setVisibility(View.VISIBLE);
            if (isSelectedAll) {
                viewHolder.selected.setChecked(true);
            } else {
                viewHolder.selected.setChecked(false);
            }
        } else {
            viewHolder.selected.setVisibility(View.GONE);
        }


        Glide.with(mContext).load(mList.get(i).getPath()).error(R.drawable.default_video).into(viewHolder.imageView);
        viewHolder.textView.setText(mList.get(i).getName());

        return view;
    }

    public class ViewHolder {

        ImageView imageView;
        TextView textView;
        TextView findNew;
        public CheckBox selected;

        ViewHolder(View view) {
            imageView = (ImageView) view.findViewById(R.id.video_show_iv);
            textView = (TextView) view.findViewById(R.id.video_show_tv);
            findNew = (TextView) view.findViewById(R.id.new_find);
            selected = (CheckBox) view.findViewById(R.id.has_selected);
        }
    }
    public void updateView(boolean isSelected) {
        this.isSelected = isSelected;
        if (isSelected) {
            Vibrator vib = (Vibrator) mContext.getSystemService(Service.VIBRATOR_SERVICE);
            vib.vibrate(100);
        }
        notifyDataSetChanged();
    }
    public void updateViewAllSelect(boolean isSelectedAll) {
        this.isSelectedAll = isSelectedAll;
        notifyDataSetChanged();
    }
    public void updateViewToNormal(boolean isSelected,boolean isSelectedAll) {
        this.isSelectedAll = isSelectedAll;
        this.isSelected = isSelected;
        notifyDataSetChanged();
    }



}
