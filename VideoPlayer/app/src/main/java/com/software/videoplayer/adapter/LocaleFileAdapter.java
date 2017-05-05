package com.software.videoplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.software.videoplayer.R;
import com.software.videoplayer.constants.MyConstants;
import com.software.videoplayer.util.FileManager;
import com.software.videoplayer.util.SyncImageLoader;
import com.software.videoplayer.util.TFile;

import java.util.List;


/**
 * 本地文件adapter
 * 
 * @author zhanglei
 *
 */
public class LocaleFileAdapter extends BaseAdapter {
	
	private FileManager bfm;
	private List<TFile> data;
	private Context cxt;
	private List<TFile> choosedFiles;
	int w;
	private boolean isSelected = false;
	private int isSelectedAll = 0;//0为单选模式，1为全选  2为取消全选
	private SyncImageLoader syncImageLoader;
	private SyncImageLoader.OnImageLoadListener imageLoadListener;

	public LocaleFileAdapter(List<TFile> data, Context cxt , SyncImageLoader syncImageLoader , SyncImageLoader.OnImageLoadListener imageLoadListener) {
		super();
		this.data = data;
		this.cxt = cxt;
		this.syncImageLoader = syncImageLoader;
		this.imageLoadListener = imageLoadListener;
		bfm = FileManager.getInstance();
		choosedFiles = bfm.getChoosedFiles();
		w = cxt.getResources().getDimensionPixelSize(R.dimen.view_36dp);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(null!=data)
			return data.size();
		return 0;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	//目录：显示目录view;文件：显示文件view及勾选状况
	@Override
	public View getView(int pos, View view, ViewGroup arg2) {
		// TODO Auto-generated method stub
		if(null == view){
			view = LayoutInflater.from(cxt).inflate(R.layout.locale_file_item, null);
		}
		View dirView = view.findViewById(R.id.dirRl);
		TextView dirName = (TextView) view.findViewById(R.id.dirName);
		CheckBox checkBox = (CheckBox) view.findViewById(R.id.item_check);
		
		View fileView = view.findViewById(R.id.fileLl);


		view.setTag(pos);
		TFile bxFile = data.get(pos);
		if(bxFile.isDir()){
			dirView.setVisibility(View.VISIBLE);
			dirName.setText(bxFile.getFileName());
			fileView.setVisibility(View.GONE);
		}else{
			dirView.setVisibility(View.GONE);
			fileView.setVisibility(View.VISIBLE);
			ImageView fileType = (ImageView) view.findViewById(R.id.fileType);
			TextView fileName = (TextView) view.findViewById(R.id.fileName);
			TextView fileSize = (TextView) view.findViewById(R.id.fileSize);
			TextView fileModifyDate = (TextView) view.findViewById(R.id.fileModifyDate);
			fileName.setText(bxFile.getFileName());
			fileSize.setText(bxFile.getFileSizeStr());
			fileModifyDate.setText(bxFile.getLastModifyTimeStr());
			if(bxFile.getMimeType().equals(TFile.MimeType.IMAGE)){
				fileType.setImageResource(R.drawable.bxfile_file_default_pic);
				if(null!=syncImageLoader && null!=imageLoadListener)
					syncImageLoader.loadDiskImage(pos, bxFile.getFilePath(), imageLoadListener);
			}else{
				fileType.setImageResource(bfm.getMimeDrawable(bxFile.getMimeType()));
			}

		}
		initCheckBox(checkBox,pos);
		return view;
	}

	private void initCheckBox(CheckBox checkBox ,int position) {
		checkBox.setOnCheckedChangeListener(null);//先设置一次CheckBox的选中监听器，传入参数null

		if (isSelected) {
			checkBox.setVisibility(View.VISIBLE);
			if (isSelectedAll == 1) {
				checkBox.setChecked(true);
			} else if (isSelectedAll == 2){
				checkBox.setChecked(false);
			}else {
				checkBox.setChecked(MyConstants.checkedFlagFile[position]);//用数组中的值设置CheckBox的选中状态
			}
		} else {
			checkBox.setVisibility(View.GONE);
		}

		checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
				MyConstants.checkedFlagFile[position] = b;
			}
		});
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
