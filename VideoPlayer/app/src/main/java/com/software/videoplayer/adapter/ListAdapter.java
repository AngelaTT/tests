package com.software.videoplayer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.software.videoplayer.constants.MyConstants;
import com.software.videoplayer.interfaces.IAdapterView;
import com.software.videoplayer.interfaces.OnItemClickListener;
import com.software.videoplayer.interfaces.OnItemLongClickListener;
import com.software.videoplayer.ui.LocalMusicItemView;

import java.util.List;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 7/11/16
 * Time: 11:45 AM
 * Desc: ListAdapter
 */
public abstract class ListAdapter<T, V extends IAdapterView> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<T> mData;

    private OnItemClickListener mItemClickListener;
    private OnItemLongClickListener mItemLongClickListener;
    private int mLastItemClickPosition = RecyclerView.NO_POSITION;
    private boolean isSelected = false;
    private int isSelectedAll = 0;//0为单选模式，1为全选  2为取消全选

    public ListAdapter(Context context, List<T> data) {
        mContext = context;
        mData = data;
    }

    protected abstract V createView(Context context);

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = (View) createView(mContext);
        final RecyclerView.ViewHolder holder = new RecyclerView.ViewHolder(itemView) {};
        if (mItemClickListener != null) {
            itemView.setOnClickListener(v -> {
                int position = holder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mLastItemClickPosition = position;
                    mItemClickListener.onItemClick(position);
                }
            });
        }
        if (mItemLongClickListener != null) {
            itemView.setOnLongClickListener(v -> {
                int position = holder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mItemLongClickListener.onItemClick(position);
                }
                return false;
            });
        }
        return holder;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        IAdapterView itemView = (V) holder.itemView;
        itemView.bind(getItem(position), position);
        CheckBox checkBox = itemView.getCheckBox();
       /* if (localMusicItemView == null){
            localMusicItemView = new LocalMusicItemView(mContext);
        }*/
       if (checkBox == null) return;
        checkBox.setOnCheckedChangeListener(null);//先设置一次CheckBox的选中监听器，传入参数null

         if (isSelected) {
            checkBox.setVisibility(View.VISIBLE);
            if (isSelectedAll == 1) {
                checkBox.setChecked(true);
            } else if (isSelectedAll == 2){
                checkBox.setChecked(false);
            }else {
                checkBox.setChecked(MyConstants.checkedFlag[position]);//用数组中的值设置CheckBox的选中状态
            }
        } else {
            checkBox.setVisibility(View.GONE);
        }

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                MyConstants.checkedFlag[position] = b;
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mData == null)
            return 0;
        return mData.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public List<T> getData() {
        return mData;
    }

    public void setData(List<T> data) {
        mData = data;
    }

    public void addData(List<T> data) {
        if (mData == null) {
            mData = data;
        } else {
            mData.addAll(data);
        }
    }

    public T getItem(int position) {
        return mData.get(position);
    }

    public void clear() {
        if (mData != null)
            mData.clear();
    }

    public OnItemClickListener getItemClickListener() {
        return mItemClickListener;
    }

    public int getLastItemClickPosition() {
        return mLastItemClickPosition;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mItemClickListener = listener;
    }

    public OnItemLongClickListener getItemLongClickListener() {
        return mItemLongClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        mItemLongClickListener = listener;
    }

    public void updateView(boolean isSelected) {
        this.isSelected = isSelected;
        notifyDataSetChanged();
    }
    public void updateViewAllSelect(int isSelectedAll) {
        this.isSelectedAll = isSelectedAll;
        notifyDataSetChanged();
    }

    public void updateToNormal(int isSelectedAll, boolean isSelected) {
        this.isSelectedAll = isSelectedAll;
        this.isSelected = isSelected;
        notifyDataSetChanged();
    }
}
