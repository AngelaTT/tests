package com.software.videoplayer.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.software.videoplayer.R;
import com.software.videoplayer.model.CloudInfo;

import java.util.ArrayList;
import java.util.List;


/**
 * User: Moon
 * Data: 2017/3/16.
 */

public class CloudAdapter extends BaseAdapter {

    private Context mContext;
    private List<CloudInfo> mList;
    private ViewHolder viewHolder;
    private List<Drawable> drawableList;


    public CloudAdapter(Context context,List<CloudInfo> list) {
        mContext = context;
        mList = list;
        drawableList = new ArrayList<>();
        drawableList.add(context.getResources().getDrawable(R.drawable.colod_item_bg1));
        drawableList.add(context.getResources().getDrawable(R.drawable.colod_item_bg2));
        drawableList.add(context.getResources().getDrawable(R.drawable.colod_item_bg3));
    }

    @Override
    public int getCount() {
        if (mList != null) {
            return mList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int pos, View view, ViewGroup arg2) {

        if (null == view) {
            view = LayoutInflater.from(mContext).inflate(R.layout.locale_cloud_item, null);
            viewHolder= new ViewHolder(view);
            view.setTag(viewHolder);
            ViewGroup.LayoutParams layoutParams = viewHolder.textView.getLayoutParams();
            layoutParams.height = (mContext.getResources().getDisplayMetrics().widthPixels-100)/4;
            viewHolder.textView.setLayoutParams(layoutParams);
        } else {
            view.getTag();
        }

        if (pos < 11) {
            switch (pos) {
                case 0:
                    viewHolder.textView.setBackground(mContext.getResources().getDrawable(R.drawable.button_youku));
                    break;
                case 1:
                    viewHolder.textView.setBackground(mContext.getResources().getDrawable(R.drawable.button_tudou));
                    break;
                case 2:
                    viewHolder.textView.setBackground(mContext.getResources().getDrawable(R.drawable.button_tengxun));
                    break;
                case 3:
                    viewHolder.textView.setBackground(mContext.getResources().getDrawable(R.drawable.button_pptv));
                    break;
                case 4:
                    viewHolder.textView.setBackground(mContext.getResources().getDrawable(R.drawable.button_aiqiyi));
                    break;
                case 5:
                    viewHolder.textView.setBackground(mContext.getResources().getDrawable(R.drawable.button_leshi));
                    break;
                case 6:
                    viewHolder.textView.setBackground(mContext.getResources().getDrawable(R.drawable.button_souhu));
                    break;
                case 7:
                    viewHolder.textView.setBackground(mContext.getResources().getDrawable(R.drawable.button_xinlang));
                    break;
                case 8:
                    viewHolder.textView.setBackground(mContext.getResources().getDrawable(R.drawable.button_huashu));
                    break;
                case 9:
                    viewHolder.textView.setBackground(mContext.getResources().getDrawable(R.drawable.button_56));
                    break;
                case 10:
                    viewHolder.textView.setBackground(mContext.getResources().getDrawable(R.drawable.button_ku6));
                    break;
            }

        } else {
            if (mList.get(pos).getName().equals("add")) {
                viewHolder.textView.setText("");
                viewHolder.textView.setBackground(mContext.getResources().getDrawable(R.drawable.add_btn));
            } else {
                viewHolder.textView.setText(mList.get(pos).getName());
                viewHolder.textView.setBackground(drawableList.get(pos%2));
            }
        }
        return view;
    }
    private class ViewHolder{

        TextView textView;

        ViewHolder(View view) {

            textView = (TextView) view.findViewById(R.id.cloud_tv);
        }
    }
}
