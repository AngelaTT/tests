/*
 * Copyright (C) 2016 Yaroslav Pronin <proninyaroslav@mail.ru>
 *
 * This file is part of LibreTorrent.
 *
 * LibreTorrent is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * LibreTorrent is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with LibreTorrent.  If not, see <http://www.gnu.org/licenses/>.
 */

package qixiao.com.btdownload.adapters;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.text.format.Formatter;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wnafee.vector.ui.MorphButton;

import qixiao.com.btdownload.R;
import qixiao.com.btdownload.core.TorrentStateCode;
import qixiao.com.btdownload.core.sorting.TorrentSortingComparator;
import qixiao.com.btdownload.core.stateparcel.TorrentStateParcel;
import qixiao.com.btdownload.core.utils.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class TorrentListAdapter extends SelectableAdapter<TorrentListAdapter.ViewHolder>
{
    @SuppressWarnings("unused")
    private static final String TAG = TorrentListAdapter.class.getSimpleName();

    private Context context;
    private ViewHolder.ClickListener clickListener;
    private int rowLayout;
    /* Filtered items */
    private List<TorrentStateParcel> currentItems;
    private List<TorrentStateParcel> allItems;
    private TorrentStateParcel curOpenTorrent;
    private DisplayFilter displayFilter = new DisplayFilter();
    private SearchFilter searchFilter = new SearchFilter();
    private TorrentSortingComparator sorting;

    public TorrentListAdapter(List<TorrentStateParcel> states, Context context,
                              int rowLayout, ViewHolder.ClickListener clickListener,
                              TorrentSortingComparator sorting)
    {
        this.context = context;
        this.rowLayout = rowLayout;
        this.clickListener = clickListener;
        this.sorting = sorting;
        allItems = states;
        setEqualsMethod(allItems);
        currentItems = new ArrayList<>(allItems);
        Collections.sort(currentItems, sorting);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);

        return new ViewHolder(v, clickListener, currentItems);
    }

    @SuppressWarnings("ResourceType")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        TorrentStateParcel state = currentItems.get(position);

        /*Utils.setBackground(holder.indicatorCurOpenTorrent,
                ContextCompat.getDrawable(context, android.R.color.transparent));*/

        TypedArray a = context.obtainStyledAttributes(new TypedValue().data, new int[] {
                R.attr.defaultSelectRect,
                R.attr.defaultRectRipple
        });

        if (isSelected(position)) {
            Utils.setBackground(
                    holder.itemTorrentList,
                    a.getDrawable(0));
        } else {
            Utils.setBackground(
                    holder.itemTorrentList,
                    a.getDrawable(1));
        }
        a.recycle();

        holder.name.setText(state.name);

        holder.progress.setProgress(state.progress);

        String stateString = "";
        switch (state.stateCode) {
            case DOWNLOADING:
                stateString = context.getString(R.string.torrent_status_downloading)+" :   " + state.progress+"%";
                break;
            case SEEDING:
                stateString = context.getString(R.string.torrent_status_seeding);
                break;
            case PAUSED:
                stateString = context.getString(R.string.torrent_status_paused);
                break;
            case STOPPED:
                stateString = context.getString(R.string.torrent_status_stopped);
                break;
            case FINISHED:
                stateString = context.getString(R.string.torrent_status_finished);
                break;
            case CHECKING:
                stateString = context.getString(R.string.torrent_status_checking);
                break;
        }
        holder.state.setText(stateString);

        String counterTemplate = context.getString(R.string.download_counter_template);
        String totalBytes = Formatter.formatFileSize(context, state.totalBytes);
        String receivedBytes;
        if (state.progress == 100) {
            receivedBytes = totalBytes;
        } else {
            receivedBytes = Formatter.formatFileSize(context, state.receivedBytes);
        }

        holder.downloadCounter.setText("大小 : "+totalBytes+"    已下载 : "+receivedBytes);

        String speedTemplate = context.getString(R.string.download_upload_speed_template);
        String downloadSpeed = Formatter.formatFileSize(context, state.downloadSpeed);
        String uploadSpeed = Formatter.formatFileSize(context, state.uploadSpeed);
        holder.downloadUploadSpeed.setText(
                "上载 : "+uploadSpeed+"/s    下载 : "+downloadSpeed+"/s");

        String ETA;
        if (state.ETA == -1) {
            ETA = Utils.INFINITY_SYMBOL;
        } else if (state.ETA == 0) {
            ETA = " ";
        } else {
            ETA = DateUtils.formatElapsedTime(state.ETA);
        }
        holder.ETA.setText("ETA : "+ETA);

        holder.pauseButton.setStartDrawable(R.drawable.pause);
        holder.pauseButton.setEndDrawable(R.drawable.jixun);
        holder.pauseButton.setState(
                (state.stateCode == TorrentStateCode.PAUSED ?
                        MorphButton.MorphState.END :
                        MorphButton.MorphState.START));

        if (curOpenTorrent != null) {
            curOpenTorrent.setEqualsById(true);
            if (getItemPosition(curOpenTorrent) == position && Utils.isTwoPane(context)) {
                if (!isSelected(position)) {
                    a = context.obtainStyledAttributes(new TypedValue().data, new int[]{ R.attr.curOpenTorrentIndicator });
                    Utils.setBackground(
                            holder.itemTorrentList, a.getDrawable(0));
                    a.recycle();
                }

               /* Utils.setBackground(holder.indicatorCurOpenTorrent,
                        ContextCompat.getDrawable(context, R.color.accent));*/
            }
        }
    }

    private void setEqualsMethod(Collection<TorrentStateParcel> states)
    {
        for (TorrentStateParcel state : states) {
            if (state == null) {
                continue;
            }
            state.setEqualsById(true);
        }
    }

    public synchronized void addItems(Collection<TorrentStateParcel> states)
    {
        if (states == null) {
            return;
        }

        setEqualsMethod(states);
        List<TorrentStateParcel> statesList = displayFilter.filter(states);
        currentItems.addAll(statesList);
        Collections.sort(currentItems, sorting);

        notifyItemRangeInserted(0, statesList.size());

        allItems.addAll(states);
    }

    /*
     * Mark the torrent as currently pause.
     */

    public synchronized void markAsOpen(TorrentStateParcel state)
    {
        if (state != null) {
            curOpenTorrent = state;
            notifyDataSetChanged();
        }
    }

    public synchronized void updateItem(TorrentStateParcel torrentState)
    {
        if (torrentState == null) {
            return;
        }

        torrentState.setEqualsById(true);

        if (!currentItems.contains(torrentState)) {
            TorrentStateParcel state = displayFilter.filter(torrentState);

            if (state != null) {
                currentItems.add(torrentState);
                Collections.sort(currentItems, sorting);

                notifyItemInserted(currentItems.indexOf(state));
            }

        } else {
            int position = currentItems.indexOf(torrentState);

            if (position >= 0) {
                currentItems.remove(position);
                TorrentStateParcel state = displayFilter.filter(torrentState);

                if (state != null) {
                    currentItems.add(position, torrentState);
                    Collections.sort(currentItems, sorting);
                    notifyItemChanged(position);
                } else {
                    notifyItemRemoved(position);
                }
            }
        }

        if (!allItems.contains(torrentState)) {
            allItems.add(torrentState);

            return;
        }

        int position = allItems.indexOf(torrentState);

        if (position < 0) {
            return;
        }

        allItems.remove(position);
        allItems.add(position, torrentState);
    }

    public void setDisplayFilter(DisplayFilter displayFilter)
    {
        if (displayFilter == null) {
            return;
        }

        this.displayFilter = displayFilter;

        currentItems.clear();
        List<TorrentStateParcel> filter = displayFilter.filter(allItems);
        currentItems.addAll(filter);
        Collections.sort(currentItems, sorting);

        notifyDataSetChanged();
    }

    public void search(String searchPattern)
    {
        if (searchPattern == null) {
            return;
        }

        searchFilter.filter(searchPattern);
    }

    public void clearAll()
    {
        allItems.clear();

        int size = currentItems.size();
        if (size > 0) {
            currentItems.clear();

            notifyItemRangeRemoved(0, size);
        }
    }

    public void deleteItems(Collection<TorrentStateParcel> states)
    {
        if (states == null) {
            return;
        }

        setEqualsMethod(states);

        for (TorrentStateParcel state : states) {
            if (state == null) {
                continue;
            }

            currentItems.remove(state);
            allItems.remove(state);
        }

        notifyDataSetChanged();
    }



    public void setSorting(TorrentSortingComparator sorting)
    {
        if (sorting == null) {
            return;
        }

        this.sorting = sorting;
        Collections.sort(currentItems, sorting);

        notifyItemRangeChanged(0, currentItems.size());
    }

    public TorrentStateParcel getItem(int position)
    {
        if (position < 0 || position >= currentItems.size()) {
            return null;
        }

        return currentItems.get(position);
    }

    public int getItemPosition(TorrentStateParcel state)
    {
        if (state == null) {
            return -1;
        }

        state.setEqualsById(true);

        return currentItems.indexOf(state);
    }

    public boolean isEmpty()
    {
        return currentItems.isEmpty();
    }

    @Override
    public int getItemCount()
    {
        return currentItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener
    {
        private final ImageView iv_delet;
        private Context context;
        private ClickListener listener;
        private List<TorrentStateParcel> states;
        LinearLayout itemTorrentList;
        TextView name;
        MorphButton pauseButton;
        ProgressBar progress;
        TextView state;
        TextView downloadCounter;
        TextView downloadUploadSpeed;
        TextView ETA;
//        View indicatorCurOpenTorrent;

        public ViewHolder(View itemView, final ClickListener listener, final List<TorrentStateParcel> states)
        {
            super(itemView);

            this.context = itemView.getContext();
            this.listener = listener;
            this.states = states;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            itemTorrentList = (LinearLayout) itemView.findViewById(R.id.item_torrent_list);
            name = (TextView) itemView.findViewById(R.id.torrent_name);
            pauseButton = (MorphButton) itemView.findViewById(R.id.pause_torrent);
            iv_delet = ((ImageView) itemView.findViewById(R.id.iv_delete));
            iv_delet.setOnClickListener(this);
            pauseButton.setOnClickListener(onClickListener);
            progress = (ProgressBar) itemView.findViewById(R.id.torrent_progress);
            Utils.colorizeProgressBar(context, progress);
            state = (TextView) itemView.findViewById(R.id.torrent_status);
            downloadCounter = (TextView) itemView.findViewById(R.id.torrent_download_counter);
            downloadUploadSpeed = (TextView) itemView.findViewById(R.id.torrent_download_upload_speed);
            ETA = (TextView) itemView.findViewById(R.id.torrent_ETA);
//            indicatorCurOpenTorrent = itemView.findViewById(R.id.indicator_cur_open_torrent);
        }

        View.OnClickListener onClickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int position = getAdapterPosition();
                if (listener != null && position >= 0) {
                    TorrentStateParcel state = states.get(position);
                    listener.onPauseButtonClicked(position, state);
                }
            }
        };

        @Override
        public void onClick(View v)
        {
            int position = getAdapterPosition();

            if (listener != null && position >= 0) {
                TorrentStateParcel state = states.get(position);
                listener.onItemClicked(v,position, state);
            }
        }

        @Override
        public boolean onLongClick(View v)
        {
            int position = getAdapterPosition();

            if (listener != null && position >= 0) {
                TorrentStateParcel state = states.get(getAdapterPosition());
                listener.onItemLongClicked(position, state);

                return true;
            }

            return false;
        }

        public interface ClickListener
        {
            void onItemClicked(View view,int position, TorrentStateParcel torrentState);

            boolean onItemLongClicked(int position, TorrentStateParcel torrentState);

            void onPauseButtonClicked(int position, TorrentStateParcel torrentState);
        }
    }

    public static class DisplayFilter
    {
        TorrentStateCode constraintCode;

        /* Without filtering */
        public DisplayFilter()
        {

        }

        public DisplayFilter(TorrentStateCode constraint)
        {
            constraintCode = constraint;
        }

        public List<TorrentStateParcel> filter(Collection<TorrentStateParcel> states)
        {
            List<TorrentStateParcel> filtered = new ArrayList<>();

            if (states != null) {
                if (constraintCode != null) {
                    for (TorrentStateParcel state : states) {
                        if (state.stateCode == constraintCode) {
                            filtered.add(state);
                        }
                    }

                } else {
                    filtered.addAll(states);
                }
            }

            return filtered;
        }

        public TorrentStateParcel filter(TorrentStateParcel state)
        {
            if (state == null) {
                return null;
            }

            if (constraintCode != null) {
                if (state.stateCode == constraintCode) {
                    return state;
                }

            } else {
                return state;
            }

            return null;
        }
    }

    private class SearchFilter extends Filter
    {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence)
        {
            currentItems.clear();

            if (charSequence.length() == 0) {
                currentItems.addAll(displayFilter.filter(allItems));
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for (TorrentStateParcel state : allItems) {
                    if (state.name.toLowerCase().contains(filterPattern)) {
                        currentItems.add(state);
                    }
                }
            }

            Collections.sort(currentItems, sorting);

            return new FilterResults();
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults)
        {
            notifyDataSetChanged();
        }
    }

    public List<TorrentStateParcel> getAllItems(){
        return allItems;
    }

    public List<TorrentStateParcel> getCurrentItems() {
        return currentItems;
    }
}
