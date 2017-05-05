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
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.io.FilenameUtils;
import qixiao.com.btdownload.MainActivity;
import qixiao.com.btdownload.R;

import qixiao.com.btdownload.dialogs.filemanager.FileManagerNode;
import qixiao.com.btdownload.core.filetree.FileNode;

import java.util.Collections;
import java.util.List;

/*
 * The adapter for directory or file chooser dialog.
 */

public class FileManagerAdapter extends BaseFileListAdapter<FileManagerAdapter.ViewHolder, FileManagerNode>
{
    @SuppressWarnings("unused")
    private static final String TAG = FileManagerAdapter.class.getSimpleName();

    private Context context;
    private ViewHolder.ClickListener clickListener;
    private int rowLayout;
    private List<String> highlightFileTypes;


    public FileManagerAdapter(List<FileManagerNode> files, List<String> highlightFileTypes,
                              Context context, int rowLayout, ViewHolder.ClickListener clickListener)
    {
        this.context = context;
        this.rowLayout = rowLayout;
        this.clickListener = clickListener;
        Collections.sort(files);
        this.files = files;
        this.highlightFileTypes = highlightFileTypes;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);

        return new ViewHolder(v, clickListener, files);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {

        final FileManagerNode file = files.get(position);

        if (highlightFileTypes != null && highlightFileTypes.contains(FilenameUtils.getExtension(file.getName()))) {
            holder.fileName.setTextColor(ContextCompat.getColor(context, R.color.colorHome));
        } else {
            TypedArray a = context.obtainStyledAttributes(new TypedValue().data, new int[]{ android.R.attr.textColorPrimary });
            holder.fileName.setTextColor(a.getColor(0, 0));
            a.recycle();
        }

        holder.fileName.setText(file.getName());
        holder.fileTime.setText(file.getTime());


        holder.fileSelect.setSelected(file.isSelected());//先设置一次CheckBox的选中监听器，传入参数null
        MainActivity.flags.add(holder.fileSelect.isSelected());


        if (file.getType() == FileNode.Type.DIR) {
            holder.fileIcon.setImageResource(R.drawable.wenjianjia);
            if (file.getName().equals(FileManagerNode.PARENT_DIR)){
                holder.fileName.setText(file.getName());
                holder.fileSelect.setVisibility(View.GONE);
                holder.fileSelect.setSelected(file.isSelected());
                holder.fileTime.setVisibility(View.GONE);
            }else {
                holder.fileSelect.setVisibility(View.VISIBLE);
                holder.fileTime.setVisibility(View.VISIBLE);
                holder.fileSelect.setSelected(file.isSelected());
                holder.fileName.setText(file.getName()+"("+file.getFileNumber()+")");
            }

        } else if (file.getType() == FileNode.Type.FILE) {
            holder.fileIcon.setImageResource(R.drawable.weizhi);
        }else if (file.getType() == FileNode.Type.MP3) {
            holder.fileIcon.setImageResource(R.drawable.yinyue);
        }else if (file.getType() == FileNode.Type.MP4) {
            holder.fileIcon.setImageResource(R.drawable.shiping);
        }else if (file.getType() == FileNode.Type.TXT) {
            holder.fileIcon.setImageResource(R.drawable.wenben);
        }else if (file.getType() == FileNode.Type.APK) {
            holder.fileIcon.setImageResource(R.drawable.anzhuo);
        }else if (file.getType() == FileNode.Type.ZIP) {
            holder.fileIcon.setImageResource(R.drawable.yasuo);
        }else if (file.getType() == FileNode.Type.PNG) {
            holder.fileIcon.setImageResource(R.drawable.tupian);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private final TextView fileTime;
        private final Button fileSelect;
        private ClickListener listener;
        TextView fileName;
        ImageView fileIcon;
        private List<FileManagerNode> files;

        public ViewHolder(View itemView, final ClickListener listener, final List<FileManagerNode> files)
        {
            super(itemView);

            this.listener = listener;
            this.files = files;
            fileName = (TextView) itemView.findViewById(R.id.file_name);
            fileIcon = (ImageView) itemView.findViewById(R.id.file_icon);
            fileTime = ((TextView) itemView.findViewById(R.id.tv_time));
            fileSelect = ((Button) itemView.findViewById(R.id.bt_select));


            itemView.setOnClickListener(this);
            fileSelect.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (listener != null) {
                        fileSelect.setSelected(!fileSelect.isSelected());
                        MainActivity.flags.set(getAdapterPosition(),fileSelect.isSelected());
                        listener.onItemCheckedChanged(FileManagerAdapter.ViewHolder.this.files.get(getAdapterPosition()),
                                fileSelect.isSelected());
                    }
                }
            });
        }


        @Override
        public void onClick(View v)
        {
            int position = getAdapterPosition();

            if (listener != null && position >= 0) {
                FileManagerNode file = files.get(position);

                if (file.getType() == FileNode.Type.FILE) {
                    /* Check file if it clicked */
                    fileSelect.performClick();
                }
                listener.onItemClicked(file.getName(), file.getType());


            }
            switch (v.getId()){



                /*default:
                    int position = getAdapterPosition();

                    if (listener != null && position >= 0) {
                        listener.onItemClicked(file.getName(), file.getType());
                    }
                    break;*/
            }


        }

        public interface ClickListener
        {
            void onItemClicked(String objectName, int objectType);
            void onItemCheckedChanged(FileManagerNode node, boolean selected);
        }
    }
}