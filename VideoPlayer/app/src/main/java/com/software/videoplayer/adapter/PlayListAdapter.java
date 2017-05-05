package com.software.videoplayer.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.software.videoplayer.R;
import com.software.videoplayer.bean.playListbean;
import com.software.videoplayer.data.model.PlayList;

import java.util.List;

/**
 * Created by txh on 2017/4/12.
 */

public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.MyViewHolder> {
    private List<PlayList> mDatas;
    private Context mContext;
    private LayoutInflater inflater;
    private OnItemClickListener mOnItemClickListener;
    private int position;

    public PlayListAdapter(Context context, List<PlayList> datas) {
        this.mContext = context;
        this.mDatas = datas;
        inflater = LayoutInflater.from(mContext);
    }

    //重写onCreateViewHolder方法，返回一个自定义的ViewHolder
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_play_list, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    //填充onCreateViewHolder方法返回的holder中的控件
    @Override
    public void onBindViewHolder(PlayListAdapter.MyViewHolder holder, int position) {
        this.position = position;
        holder.text_view_name.setText(mDatas.get(position).getName());
        holder.text_view_info.setText(mDatas.get(position).getNumOfSongs()+"首歌");
        holder.image_view_album.setImageResource( mDatas.get(position).getAlbum());
//        holder.image_button_action.setText( mDatas.get(position));

    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        AppCompatImageView image_view_album;
        AppCompatImageView image_button_action;
        TextView text_view_name;
        TextView text_view_info;
        View v;

        public MyViewHolder(View view) {
            super(view);
            v = view;
            image_view_album = (AppCompatImageView) view.findViewById(R.id.image_view_album);
            image_button_action = (AppCompatImageView) view.findViewById(R.id.image_button_action);
            text_view_name = (TextView) view.findViewById(R.id.text_view_name);
            text_view_info = (TextView) view.findViewById(R.id.text_view_info);
            view.setOnClickListener(this);
            image_button_action.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onClick(view,getLayoutPosition());
            }
        }
    }

    public interface OnItemClickListener {
        void onClick(View view,int position);

//        void onLongClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    /*public PlayList getItem(int position) {
        return getItem(position);
    }*/
}
