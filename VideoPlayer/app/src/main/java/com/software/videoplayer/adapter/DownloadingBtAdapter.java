package com.software.videoplayer.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.se_bastiaan.torrentstream.StreamStatus;
import com.github.se_bastiaan.torrentstream.Torrent;
import com.github.se_bastiaan.torrentstream.TorrentOptions;
import com.github.se_bastiaan.torrentstream.TorrentStream;
import com.github.se_bastiaan.torrentstream.listeners.TorrentListener;
import com.software.videoplayer.R;
import com.software.videoplayer.constants.MyConstants;
import com.software.videoplayer.interfaces.OnItemClickListener;
import com.software.videoplayer.interfaces.OnItemLongClickListener;
import com.software.videoplayer.model.VideoInfo;
import com.software.videoplayer.util.FileUtils;
import com.software.videoplayer.util.Utils;
import com.software.videoplayer.util.VideoHelper;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * User: Moon
 * Data: 2017/4/1.
 */

public class DownloadingBtAdapter extends RecyclerView.Adapter<DownloadingBtAdapter.BtViewHolder> {

    private Context context;
    private List<String> magnet;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    private boolean isSelected = false;
    private boolean isSelectedAll = false;
    private Map<String, Torrent> list = new HashMap<>();

    public DownloadingBtAdapter(Context context,List<String> magnet) {

        this.context = context;
        this.magnet = magnet;

    }




    @Override
    public BtViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BtViewHolder(LayoutInflater.from(context).inflate(R.layout.bt_downloading, parent,false));
    }

    @Override
    public void onBindViewHolder(BtViewHolder holder, @SuppressLint("RecyclerView") int position) {

        TorrentOptions torrentOptions = new TorrentOptions.Builder()
                .saveLocation(Utils.ensureCreated(new File(MyConstants.BTDOWN_PATH)))
                .removeFilesAfterStop(true)
                .build();

        TorrentStream torrentStream = TorrentStream.init(torrentOptions);
        torrentStream.startStream(magnet.get(position));

        holder.downMessage.setText("解析中...");
        holder.name.setText("");
        holder.stateText.setText("");


        torrentStream.addListener(new TorrentListener() {
            @Override
            public void onStreamPrepared(Torrent torrent) {
                holder.name.setText(torrent.getSaveLocation().getName());
                torrent.startDownload();
            }

            @Override
            public void onStreamStarted(Torrent torrent) {
                holder.stateImg.setImageResource(R.drawable.icon_stop);
                holder.downMessage.setText("资源链接中...");
                list.put(magnet.get(position),torrent);
            }

            @Override
            public void onStreamError(Torrent torrent, Exception e) {
                holder.stateImg.setImageResource(R.drawable.icon_wrong);
                holder.stateText.setTextColor(context.getResources().getColor(android.R.color.holo_red_light));
                holder.stateText.setText("错误");
            }

            @Override
            public void onStreamReady(Torrent torrent) {
                holder.btPlay.setVisibility(View.VISIBLE);
                holder.btPlay.setOnClickListener(v -> {
                    VideoInfo videoInfo = new VideoInfo();
                    videoInfo.setName(torrent.getVideoFile().getName());
                    videoInfo.setPath(torrent.getVideoFile().getPath());
                    VideoHelper.getVideoHelper().playerVideo(context, videoInfo);
                });
            }

            @Override
            public void onStreamProgress(Torrent torrent, StreamStatus status) {
                long length = (long) (torrent.getVideoFile().length() * 0.01);

                String fileSize = VideoHelper.getVideoHelper().formatFileSize((long) ( length * status.progress ));
                String allSize = VideoHelper.getVideoHelper().formatFileSize(torrent.getVideoFile().length());
                holder.downMessage.setText("已下载:" + fileSize + "/" + allSize);

                int downSize = (int) (length * status.progress);
                int allDownSize = (int) torrent.getVideoFile().length();
                int progress = downSize / allDownSize * 100;
                holder.progress.setMax(allDownSize);
                holder.progress.setProgress(downSize);
              /*  if(status.progress <= 100 && holder.progress.getProgress() < 100 && holder.progress.getProgress() != status.progress) {
                    holder.progress.setProgress(progress);
                    Log.d("txh", "Progress: " + status.progress);
                }*/
                if (downSize == allDownSize) {
                    magnet.remove(position);
                    Intent intent = new Intent(MyConstants.DOWNLOADED_BT);
                    intent.putExtra(MyConstants.DOWNLOADED_BT, torrent.getSaveLocation().getAbsolutePath());
                    context.sendBroadcast(intent);
                }
                String speed = VideoHelper.getVideoHelper().formatFileSize((long) status.downloadSpeed) + "/s";
                holder.stateText.setText(speed);
                Log.e("fuck",status.progress +"比率" + length + torrent.getVideoFile().length()*0.01);
            }

            @Override
            public void onStreamStopped() {
                holder.downMessage.setText("下载暂停");
            }


        });

        holder.stateImg.setOnClickListener(v -> {
            if (torrentStream.isStreaming()) {
                torrentStream.stopStream();
                holder.stateImg.setImageResource(R.drawable.icon_download);
            } else {
                torrentStream.startStream(magnet.get(position));
                holder.stateImg.setImageResource(R.drawable.icon_stop);
            }
        });

        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(position));
        holder.btCheck.setOnClickListener(v -> onItemClickListener.onItemClick(position));
        holder.itemView.setOnLongClickListener(v -> {onItemLongClickListener.onItemClick(position); return true;});
        if (isSelected) {
            holder.btCheck.setVisibility(View.VISIBLE);
            if (isSelectedAll) {
                holder.btCheck.setChecked(true);
            } else {
                holder.btCheck.setChecked(false);
            }
            torrentStream.pauseSession();
        } else {
            holder.btCheck.setVisibility(View.GONE);
            torrentStream.resumeSession();
        }
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    @Override
    public int getItemCount() {
        return magnet.size();
    }

    public void updateView(boolean isSelected) {
        this.isSelected = isSelected;
        notifyDataSetChanged();
    }
    public void updateViewAllSelect(boolean isSelectedAll) {
        this.isSelectedAll = isSelectedAll;
        notifyDataSetChanged();
    }

    public void deleteFileDownloading(String magnetUrl) {
            if (list.get(magnetUrl) != null) {
                FileUtils.delete(list.get(magnetUrl).getSaveLocation().getAbsolutePath());
            }
    }

    public class BtViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private ProgressBar progress;
        private TextView downMessage;
        private ImageView stateImg;
        private TextView stateText;
        public CheckBox btCheck;
        private View itemView;
        private ImageView btPlay;

        public BtViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            name = (TextView) itemView.findViewById(R.id.name);
            progress = (ProgressBar) itemView.findViewById(R.id.process_bar);
            downMessage = (TextView) itemView.findViewById(R.id.dowm_message);
            stateImg = (ImageView) itemView.findViewById(R.id.state_img);
            stateText = (TextView) itemView.findViewById(R.id.state_txt);
            btCheck = (CheckBox) itemView.findViewById(R.id.bt_check);
            btPlay = (ImageView) itemView.findViewById(R.id.bt_play);
        }
    }

}
