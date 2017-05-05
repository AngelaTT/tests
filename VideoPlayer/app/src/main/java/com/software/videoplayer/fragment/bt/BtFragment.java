package com.software.videoplayer.fragment.bt;


import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.software.videoplayer.R;
import com.software.videoplayer.activity.MainActivity;
import com.software.videoplayer.activity.file.LocaleFileBrowser;
import com.software.videoplayer.constants.MyConstants;
import com.software.videoplayer.interfaces.MyOnBackPressed;
import com.software.videoplayer.service.BtCacheSerVice;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;/*
import qixiao.com.btdownload.AddTorrentActivity;
import qixiao.com.btdownload.constants.btConstant;
import qixiao.com.btdownload.core.Torrent;
import qixiao.com.btdownload.dialogs.ErrorReportAlertDialog;
import qixiao.com.btdownload.dialogs.filemanager.FileManagerConfig;
import qixiao.com.btdownload.dialogs.filemanager.FileManagerDialog;*/

/**
 * A simple {@link Fragment} subclass.
 */
public class BtFragment extends Fragment implements MyOnBackPressed {


    @BindView(R.id.add_link_button)
    FloatingActionButton addLinkButton;
    @BindView(R.id.open_file_button)
    FloatingActionButton openFileButton;
    @BindView(R.id.add_torrent_button)
    FloatingActionMenu addTorrentButton;
    Unbinder unbinder;
    @BindView(R.id.bt_root)
    ViewPager btRoot;

    DownloadingBtFragment downloadingBtFragment;
    DownloadedBtFragment downloadedBtFragment;
    private MainActivity activity;
    private RadioButton left_check;
    private RadioButton right_check;
    List<Fragment> list;

    private static BtFragment instance = null;
    public static BtFragment getInstance(){
        if (instance == null){
            instance = new BtFragment();
        }
        return instance;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
         activity = (MainActivity) context;
        RadioGroup radioGroup = (RadioGroup) activity.findViewById(R.id.bt_check);
        left_check = ((RadioButton) activity.findViewById(R.id.left_check));
        right_check = ((RadioButton) activity.findViewById(R.id.right_check));
        if (radioGroup != null) {
            radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                switch (checkedId) {
                    case R.id.left_check:
                        btRoot.setCurrentItem(0);

                        break;
                    case R.id.right_check:
                        btRoot.setCurrentItem(1);
                        break;
                }
            });
        }
        getContext().startService(new Intent(getContext(), BtCacheSerVice.class));
        activity.myOnBackPressed = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bt, container, false);
        unbinder = ButterKnife.bind(this, view);

        list = new ArrayList<>();
        downloadingBtFragment = new DownloadingBtFragment();
        downloadedBtFragment = new DownloadedBtFragment();
        list.add(downloadingBtFragment);
        list.add(downloadedBtFragment);
        btRoot.setAdapter(new FragmentPagerAdapter(getActivity().getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {

                return list.get(position);
            }

            @Override
            public int getCount() {
                return list.size();
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                //null
            }
        });
        btRoot.setOnPageChangeListener(onPageChangeListener);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
             if (position == 0){
                    left_check.setChecked(true);
                }else if (position == 1){
                    right_check.setChecked(true);
                }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @OnClick({R.id.add_link_button, R.id.open_file_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.add_link_button:

                EditText editText = new EditText(getContext());

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                builder.setTitle("输入磁力链接:")
                        .setView(editText, 40, 0, 40, 0)
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定", (dialog, which) -> {
                            String magnet = editText.getText().toString();
                            if (magnet.startsWith("magnet:")) {

                                Intent intent = new Intent(MyConstants.NEW_BT_TASK);
                                intent.putExtra("magnet", magnet);
                                getContext().sendBroadcast(intent);

                            } else {
                                builder.setMessage("无效的链接地址！");
                                builder.create().show();
                            }

                        })
                        .create().show();
                addTorrentButton.close(true);
                break;
            case R.id.open_file_button:
                Intent intent4 = new Intent(getActivity(), LocaleFileBrowser.class);
                intent4.putExtra("title", getString(R.string.bxfile_extsdcard));
                intent4.putExtra("startPath", Environment.getExternalStorageDirectory().getAbsolutePath());
                startActivity(intent4);
                addTorrentButton.close(true);
//                torrentFileChooserDialog();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (btRoot.getCurrentItem() == 0) {
            if (downloadingBtFragment.changeState) {
                downloadingBtFragment.updateViewToNormal();
            } else {
                gotoHome();
            }
        } else {
            if (downloadedBtFragment.changeStateDown) {
                downloadedBtFragment.updateViewToNormal();
            } else {
                gotoHome();
            }
        }

    }

   /* @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case btConstant.TORRENT_FILE_CHOOSE_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    if (data.hasExtra(FileManagerDialog.TAG_RETURNED_PATH)) {
                        String path = data.getStringExtra(FileManagerDialog.TAG_RETURNED_PATH);

                        try {
                            addTorrentDialog(Uri.fromFile(new File(path)));

                        } catch (Exception e) {
//                            sentError = e;

//                            Log.e(TAG, Log.getStackTraceString(e));

                            if (getFragmentManager()
                                    .findFragmentByTag(btConstant.TAG_ERROR_OPEN_TORRENT_FILE_DIALOG) == null) {
                                ErrorReportAlertDialog errDialog = ErrorReportAlertDialog.newInstance(
                                        activity.getApplicationContext(),
                                        getString(qixiao.com.btdownload.R.string.error),
                                        getString(qixiao.com.btdownload.R.string.error_open_torrent_file),//无法打开目标种子文件
                                        Log.getStackTraceString(e),
                                        this);

                               *//* FragmentTransaction ft = getFragmentManager().beginTransaction();
                                ft.add(errDialog, TAG_ERROR_OPEN_TORRENT_FILE_DIALOG);
                                ft.commitAllowingStateLoss();*//*
                            }
                        }
                    }
                }
                break;
            case btConstant.ADD_TORRENT_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    if (data.hasExtra(AddTorrentActivity.TAG_RESULT_TORRENT)) {
                        Torrent torrent = data.getParcelableExtra(AddTorrentActivity.TAG_RESULT_TORRENT);
                        if (torrent != null) {
                            ArrayList<Torrent> list = new ArrayList<>();
                            list.add(torrent);
//                            addTorrentsRequest(list);
                        }
                    }
                }
                break;
        }
    }*/

    private void gotoHome() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 注意
        intent.addCategory(Intent.CATEGORY_HOME);
        this.startActivity(intent);
    }

   /* private void addTorrentDialog(Uri uri)
    {
        if (uri == null) {
            return;
        }

        Intent i = new Intent(activity, AddTorrentActivity.class);
        i.putExtra(AddTorrentActivity.TAG_URI, uri);
        startActivityForResult(i, btConstant.ADD_TORRENT_REQUEST);
    }

    private void torrentFileChooserDialog()
    {
        Intent i = new Intent(activity, FileManagerDialog.class);

        List<String> fileType = new ArrayList<>();
        fileType.add("torrent");
        FileManagerConfig config = new FileManagerConfig(null,
                getString(qixiao.com.btdownload.R.string.torrent_file_chooser_title),//选择 torrent 文件
                fileType,//torrent
                FileManagerConfig.FILE_CHOOSER_MODE);

        i.putExtra(FileManagerDialog.TAG_CONFIG, config);

        startActivityForResult(i, btConstant.TORRENT_FILE_CHOOSE_REQUEST);
    }*/
}
