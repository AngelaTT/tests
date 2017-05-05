package com.software.videoplayer.adapter;

import android.annotation.SuppressLint;
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
import java.util.Map;

/**
 * Created by moon on 2017/2/20.
 */

public class VideoFolderShowAdapter extends BaseAdapter {

    private Context mContext;
    private Map<String,List<VideoInfo>> map;
    private List<String> maxList;
    private boolean isSelected = false;
    private boolean isSelectedAll = false;


    public VideoFolderShowAdapter(Context context, List<String> list, Map<String,List<VideoInfo>> map) {
        mContext = context;
        maxList = list;
        this.map = map;
    }

    @Override
    public int getCount() {
        return maxList.size() == 0 ? 0 : maxList.size();
    }

    @Override
    public Object getItem(int i) {
        return maxList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolderFolder viewHolder;

        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.video_show_layout, null);
            viewHolder = new ViewHolderFolder(view);
            view.setTag(viewHolder);
            int mWidth = mContext.getResources().getDisplayMetrics().widthPixels;
            ViewGroup.LayoutParams layoutParams = viewHolder.imageView.getLayoutParams();
            layoutParams.height = (int) (mWidth / 2.5);
            viewHolder.imageView.setLayoutParams(layoutParams);
        } else {
            viewHolder= (ViewHolderFolder) view.getTag();
        }
        if (i <= maxList.size()) {//ArrayList.throwIndexOutOfBoundsException
            if (map.get(maxList.get(i)).size() > 0) {
                Glide.with(mContext).load(map.get(maxList.get(i)).get(0).getPath()).into(viewHolder.imageView);
                viewHolder.textView.setText("(" + map.get(maxList.get(i)).size() + ") " + maxList.get(i));
            }


            List<VideoInfo> list = map.get(maxList.get(i));
            for (int j = 0; j < list.size(); j++) {
                if (list.get(j).getNew()) {
                    viewHolder.findNew.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.findNew.setVisibility(View.GONE);
                }
            }
        }

        if (isSelected) {
            viewHolder.checked.setVisibility(View.VISIBLE);
//            viewHolder.checked.setChecked(false);
            if (isSelectedAll) {
                    viewHolder.checked.setChecked(true);
            } else {
                    viewHolder.checked.setChecked(false);
            }
        } else {
            viewHolder.checked.setVisibility(View.GONE);
        }


        return view;
    }

    public class ViewHolderFolder {

        private ImageView imageView;
        private TextView textView;
        private TextView findNew;
        public CheckBox checked;

        ViewHolderFolder(View view) {
            imageView = (ImageView) view.findViewById(R.id.video_show_iv);
            textView = (TextView) view.findViewById(R.id.video_show_tv);
            findNew = (TextView) view.findViewById(R.id.new_find);
            checked = (CheckBox) view.findViewById(R.id.has_selected);
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
}
