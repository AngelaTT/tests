package com.software.videoplayer.activity.file;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.software.videoplayer.MyApplication;
import com.software.videoplayer.R;
import com.software.videoplayer.activity.BaseActivity1;
import com.software.videoplayer.util.FileManager;
import com.software.videoplayer.util.MIMEUtils;
import com.software.videoplayer.util.SyncImageLoader;
import com.software.videoplayer.util.TFile;
import com.software.videoplayer.util.Utils;

import java.io.File;
import java.util.List;




/***
 * 本地图片浏览
 * 
 * @author zhanglei
 *
 */
public class LocaleFileGallery extends BaseActivity1 implements OnItemClickListener {
	
	private String tag = "LocaleFileGallery";
	private GridView gv;
	private MyGVAdapter adapter;
	private List<TFile> data;
	private TextView emptyView;
	private FileManager bfm;
	
	private List<TFile> choosedFiles;
	private SyncImageLoader syncImageLoader;
	private int gridSize;
	private AbsListView.LayoutParams gridItemParams;//主要根据不同分辨率设置item宽高
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if(1 == msg.what){
				syncImageLoader = new SyncImageLoader();
				choosedFiles = bfm.getChoosedFiles();
				gridItemParams = new AbsListView.LayoutParams(gridSize,gridSize);
				adapter = new MyGVAdapter();
				gv.setAdapter(adapter);
				gv.setOnScrollListener(adapter.onScrollListener);
				gv.setOnItemClickListener(LocaleFileGallery.this);
			}else if(0 == msg.what){
				gv.setVisibility(View.GONE);
				emptyView.setVisibility(View.VISIBLE);
				emptyView.setText(getString(R.string.curCatagoryNoFiles));
			}
			super.handleMessage(msg);
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.localefile_gallery);
		setTitle(getIntent().getStringExtra("title"));
		bfm = FileManager.getInstance();
		gv = (GridView) findViewById(R.id.gridView);
		emptyView = (TextView) findViewById(R.id.emptyView);
		//计算一下在不同分辨率下gridItem应该站的宽度，在adapter里重置一下item宽高
		gridSize = (Utils.getScreenWidth(this) - getResources().getDimensionPixelSize(R.dimen.view_8dp)*5)/4 ;// 4列3个间隔，加上左右padding，共计5个

	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(null == data){
			MyApplication bxApp = (MyApplication) getApplication();
			bxApp.execRunnable(new Runnable(){
				@Override
				public void run() {
					// TODO Auto-generated method stub
					data = bfm.getMediaFiles(LocaleFileGallery.this, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					if(null!=data)
						handler.sendEmptyMessage(1);
					else
						handler.sendEmptyMessage(0);
				}
				
			});
		}
	}
	
	//点击文件，触发ui更新
	//onResume，触发ui更新
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if(null!=data)
			data.clear();
		syncImageLoader = null;
		handler = null;
		data = null;
		adapter = null;
		super.onDestroy();
	}

	class MyGVAdapter extends BaseAdapter {
		
		AbsListView.OnScrollListener onScrollListener = new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
					syncImageLoader.lock();
					break;
				case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
					loadImage();
					break;
				case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
					syncImageLoader.lock();
					break;
				default:
					break;
				}

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
								 int visibleItemCount, int totalItemCount) {

			}
		};
		
		public void loadImage() {
			int start = gv.getFirstVisiblePosition();
			int end = gv.getLastVisiblePosition();
			if (end >= getCount()) {
				end = getCount() - 1;
			}
			syncImageLoader.setLoadLimit(start, end);
			syncImageLoader.unlock();
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if(null!=data)
				return data.size();
			return 0;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if(null == convertView){
				convertView = LayoutInflater.from(LocaleFileGallery.this).inflate(R.layout.gallery_item, null);
			}
			ImageView img = (ImageView) convertView.findViewById(R.id.img);
//			img.setImageResource(R.drawable.bxfile_file_default_pic);
			View itemView = convertView.findViewById(R.id.itemView);
			//重置宽高
			itemView.setLayoutParams(gridItemParams);
			TFile bxfile = data.get(position);
//			img.setTag(position);
//			syncImageLoader.loadDiskImage(position, bxfile.getFilePath(), imageLoadListener);
			Glide.with(LocaleFileGallery.this).load(bxfile.getFilePath()).into(img);
			return convertView;
		}
		
		
		SyncImageLoader.OnImageLoadListener imageLoadListener = new SyncImageLoader.OnImageLoadListener() {
			@Override
			public void onImageLoad(Integer t, Drawable drawable) {
				View view = gv.findViewWithTag(t);
				if (view != null) {
					ImageView iv = (ImageView) view
							.findViewById(R.id.img);
					iv.setImageDrawable(drawable);
				}else{
					Log.i(tag, "View not exists");
				}
			}

			@Override
			public void onError(Integer t) {
				View view = gv.findViewWithTag(t);
				if (view != null) {
					ImageView iv = (ImageView) view
							.findViewById(R.id.img);
					iv.setImageResource(R.drawable.bxfile_file_default_pic);
				}else{
					Log.i(tag, " onError View not exists");
				}
			}

		};
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View convertView, int pos, long arg3) {
		TFile bxfile = data.get(pos);
		try {
			startActivity(MIMEUtils.getInstance().getPendingIntent(new File(bxfile.getFilePath())));
		} catch (Exception e) {
			Toast.makeText(this, "没有找到打开此类文件的应用", Toast.LENGTH_SHORT).show();
		}
	}

}
