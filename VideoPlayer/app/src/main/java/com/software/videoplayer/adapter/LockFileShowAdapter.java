package com.software.videoplayer.adapter;

import android.app.Service;
import android.content.Context;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.software.videoplayer.R;
import com.software.videoplayer.model.LockInfo;
import com.software.videoplayer.model.VideoInfo;
import com.software.videoplayer.util.AlbumUtils;
import com.software.videoplayer.util.FileEnDecryptManager;
import com.software.videoplayer.util.FileUtils;

import java.io.File;
import java.util.List;

/**
 * User: Moon
 * Data: 2017/3/24.
 */

public class LockFileShowAdapter extends BaseAdapter {

        private List<LockInfo> mList;
        private Context mContext;
        private boolean isVideo = true;
        private boolean isSelected = false;
        private boolean isSelectedAll = false;

        public LockFileShowAdapter(Context context, List<LockInfo> list, boolean isVideo) {
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
                //初始化imageView的宽高
                int mWidth = mContext.getResources().getDisplayMetrics().widthPixels;
                ViewGroup.LayoutParams layoutParams = viewHolder.imageView.getLayoutParams();
                layoutParams.height = (int) (mWidth / 2.5);
                viewHolder.imageView.setLayoutParams(layoutParams);

            } else {
                viewHolder = (ViewHolder) view.getTag();
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

           /* if (getStrLast3(mList.get(i)).equals("png")){
//                FileEnDecryptManager.getInstance().Initdecrypt(mList.get(i).getNewPath());
                String oldName = mList.get(i).getNewPath();
//                String oldPath = mList.get(i).getOldPath();
                String newName = oldName.replace(new File(oldName).getName(), new File(mList.get(i).getOldPath()).getName());
                FileUtils.reNamePath(oldName, newName);
//                Log.e("txh",newName);
                Glide.with(mContext).load(newName).error(R.drawable.default_video).into(viewHolder.imageView);
            }else {
                Glide.with(mContext).load(mList.get(i).getOldPath()).error(R.drawable.default_video).into(viewHolder.imageView);
            }*/
            Glide.with(mContext).load(mList.get(i).getOldPath()).error(R.drawable.default_video).into(viewHolder.imageView);
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

    private String getStrLast3(LockInfo videoInfo1) {
        String name = videoInfo1.getName();
        String str = name.substring(name.length() - 3, name.length());
        return str;
    }
}

