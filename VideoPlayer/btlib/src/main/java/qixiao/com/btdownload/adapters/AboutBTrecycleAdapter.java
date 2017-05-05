package qixiao.com.btdownload.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import qixiao.com.btdownload.R;
import qixiao.com.btdownload.bean.AboutBean;

import java.util.List;

/**
 * Created by Administrator on 2017/3/21.
 */

public class AboutBTrecycleAdapter extends RecyclerView.Adapter<AboutBTrecycleAdapter.AboutViewHolder> {

    private final LayoutInflater mInflater;
    private final List<AboutBean> mDatas;
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public AboutBTrecycleAdapter(Context context, List<AboutBean> datats) {
        mInflater = LayoutInflater.from(context);
        mDatas = datats;
    }

    @Override
    public AboutViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_about_bt,
                parent, false);
        AboutViewHolder viewHolder = new AboutViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AboutViewHolder holder, int position) {
        holder.iv_icon.setImageBitmap(mDatas.get(position).getIcon());
        holder.tv_describe.setText(mDatas.get(position).getDescribe());
        holder.tv_title.setText(mDatas.get(position).getTitle());
    }


    @Override
    public int getItemCount() {
        return mDatas.size();

    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int i);
    }

    public class AboutViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_title;
        ImageView iv_icon;
        TextView tv_describe;
        Button bt_download;

        public AboutViewHolder(View arg0) {
            super(arg0);
            tv_title = (TextView) arg0.findViewById(R.id.tv_title);
            iv_icon = (ImageView) arg0.findViewById(R.id.iv_icon);
            tv_describe = (TextView) arg0.findViewById(R.id.tv_describe);
            bt_download = (Button) arg0.findViewById(R.id.bt_download);
            //将创建的View注册点击事件
            bt_download.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(v, getLayoutPosition());
            }
        }
    }
}
