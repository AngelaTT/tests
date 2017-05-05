package com.software.videoplayer.activity.file;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.software.videoplayer.MyApplication;
import com.software.videoplayer.R;
import com.software.videoplayer.activity.BaseActivity1;
import com.software.videoplayer.adapter.LocaleFileAdapter;
import com.software.videoplayer.constants.MyConstants;
import com.software.videoplayer.constants.VideoConstants;
import com.software.videoplayer.data.Stroe;
import com.software.videoplayer.data.model.Song;
import com.software.videoplayer.interfaces.IAdapterView;
import com.software.videoplayer.model.VideoInfo;
import com.software.videoplayer.util.FileManager;
import com.software.videoplayer.util.MIMEUtils;
import com.software.videoplayer.util.TFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.OnClick;


/***
 * 本地多媒体文件 浏览器
 * 图片 音频 视频
 * 
 * @author zhanglei
 *
 */
public class LocaleMediaFileBrowser extends BaseActivity1 implements OnItemClickListener {

	private ListView lv;
	private List<TFile> data;
	private LocaleFileAdapter adapter;
	private TextView emptyView;
	private FileManager bfm;
	private boolean changeState = false;
	private Map<Integer, Boolean> selectedMap = new HashMap<>();
	
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if(1 == msg.what){
				lv.setVisibility(View.VISIBLE);
				emptyView.setVisibility(View.GONE);
				adapter = new LocaleFileAdapter(data,LocaleMediaFileBrowser.this,null,null);
				lv.setAdapter(adapter);
			}else if(0 == msg.what){
				lv.setVisibility(View.GONE);
				emptyView.setVisibility(View.VISIBLE);
				emptyView.setText(getString(R.string.curCatagoryNoFiles));
			}
			super.handleMessage(msg);
		}
		
	};
	private LinearLayout ht_menu;
	private TextView ht_delete;
	private TextView ht_select_all;
	private String title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.localefile_browser);
		bfm = FileManager.getInstance();
		initViews();
		initData();
		IntentFilter in = new IntentFilter();
		in.addAction(MyConstants.UPDATE_HISTORY);
		registerReceiver(mBR, in);
	}

	@SuppressWarnings("unchecked")
	BroadcastReceiver mBR = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(MyConstants.UPDATE_HISTORY)) {
				VideoInfo videoInfo = intent.getParcelableExtra(MyConstants.UPDATE_HISTORY);
				data.remove(videoInfo);
				adapter.notifyDataSetChanged();
			}
		}
	};

	private void initData() {
		// TODO Auto-generated method stub
		Intent intent = getIntent();
		title = intent.getStringExtra("title");
		setTitle(title);

		setData(intent.getData());
	}
	
	private void setData(final Uri uri){
		MyApplication app = (MyApplication) getApplication();
		app.execRunnable(() -> {
            // TODO Auto-generated method stub
            data= bfm.getMediaFiles(LocaleMediaFileBrowser.this, uri);
            if(null != data){
                Collections.sort(data);
                handler.sendEmptyMessage(1);
            }
            else
                handler.sendEmptyMessage(0);
        });
	}

	private void initViews() {
		// TODO Auto-generated method stub
		TextView curDir = (TextView) findViewById(R.id.curDir);
		curDir.setVisibility(View.GONE);
		lv = (ListView) findViewById(R.id.listView);
		lv.setOnItemClickListener(this);
//		lv.setOnItemLongClickListener(onItemLongClickListener);
		emptyView = (TextView) findViewById(R.id.emptyView);
		ht_menu = ((LinearLayout) findViewById(R.id.ht_menu));
		ht_delete = ((TextView) ht_menu.findViewById(R.id.ht_delete));
		ht_select_all = ((TextView) ht_menu.findViewById(R.id.ht_select_all));
		ht_delete.setOnClickListener(onClickListener);
		ht_select_all.setOnClickListener(onClickListener);
	}

	AdapterView.OnItemLongClickListener onItemLongClickListener = new AdapterView.OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
			adapter.updateView(true);
			changeState = true;
			ht_menu.setVisibility(View.VISIBLE);
			return false;
		}
	};

	//点击文件进行勾选操作
	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int pos, long arg3) {
		if (changeState) {
			adapter.updateViewAllSelect(0);
			int firVisPos = lv.getFirstVisiblePosition();
			View childAt = lv.getChildAt(pos - firVisPos);
			CheckBox checkBox = (CheckBox)childAt.findViewById(R.id.item_check);
			checkBox.toggle();
			selectedMap.put(pos, checkBox.isChecked());
		} else {

			TFile bxfile = data.get(pos);
			try {
				startActivity(MIMEUtils.getInstance().getPendingIntent(new File(bxfile.getFilePath())));
			} catch (Exception e) {
				Toast.makeText(this, "没有找到打开此类文件的应用", Toast.LENGTH_SHORT).show();
			}
		}
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(null!=data){
			data.clear();
		}
		data = null;
		adapter = null;
		handler = null;
		unregisterReceiver(mBR);
	}

	View.OnClickListener onClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			switch (view.getId()) {
				case R.id.ht_select_all:
					if (ht_select_all.getText().toString().equals("全选")) {
						adapter.updateViewAllSelect(1);
						ht_select_all.setText("取消全选");
						for (int i = 0; i < data.size(); i++) {
							selectedMap.put(i, true);
						}
					} else {
						adapter.updateViewAllSelect(2);
						ht_select_all.setText("全选");
						for (int i = 0; i < data.size(); i++) {
							selectedMap.put(i, false);
						}
					}
					break;
				case R.id.ht_delete:
					List<TFile> dlList = new ArrayList<>();
					for (int i = 0; i < data.size(); i++) {
						if (selectedMap.get(i) != null) {
							if (selectedMap.get(i)) {
								dlList.add(data.get(i));
								MyConstants.checkedFlagFile[i] = false;//改变删除条目的选择状态
							}
						}
					}
					for (int j = 0; j < dlList.size(); j++) {
						data.remove(dlList.get(j));
						File file = new File(dlList.get(j).getFilePath());
						file.delete();
					}
					selectedMap.clear();
					adapter.updateToNormal(0, false);
					changeState = false;
					ht_menu.setVisibility(View.GONE);
					if (!title.isEmpty() && title.equals(getString(R.string.bxfile_music))){
						MyConstants.localeMP3Number = data.size();
					}else if (!title.isEmpty() && title.equals(getString(R.string.bxfile_video))){
						MyConstants.localeMP4Number = data.size();
					}
					/*new Thread(new Runnable() {
						@Override
						public void run() {
							Stroe.storeData(getBaseContext(), VideoConstants.HISTORY, data);
							sendBroadcast(new Intent(MyConstants.UPDATE_HISTORY));
						}
					}).start();*/
					break;
			}
		}
	};
}
