package com.software.videoplayer.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.software.videoplayer.R;
import com.software.videoplayer.fragment.bt.BtFragment;
import com.software.videoplayer.fragment.cloud.CloudFragment;
import com.software.videoplayer.fragment.file.FileFragment;
import com.software.videoplayer.fragment.music.MusicFragment;
import com.software.videoplayer.fragment.video.VideoFragment;
import com.software.videoplayer.interfaces.MyOnBackPressed;
import com.software.videoplayer.ui.BottomNavigationGroup;
import com.software.videoplayer.util.PermissionHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity1 {

    @BindView(R.id.main_view_pager)
    ViewPager mViewPager;
    @BindView(R.id.main_radio_button)
    BottomNavigationGroup mBNG;
    @BindView(R.id.bt_check)
    RadioGroup btCheck;
    @BindView(R.id.toolbar_bt)
    Toolbar toolbarBt;

    public MyOnBackPressed myOnBackPressed;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbarBt);

        boolean readStorage = PermissionHelper.getPermissionHelper().checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (readStorage) {
            initFragment();
        }
    }

    private void initFragment() {
        List<Fragment> list = new ArrayList<>();
        FileFragment ff = FileFragment.getInstance();
        VideoFragment vf = VideoFragment.getInstance();
        MusicFragment mf = MusicFragment.getInstance();
        CloudFragment cf = CloudFragment.getInstence();
        BtFragment bf = BtFragment.getInstance();
        list.add(vf);
        list.add(mf);
        list.add(cf);
        list.add(bf);
        list.add(ff);
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
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
        mBNG.setViewPager(mViewPager);

        toolbarBt.setTitle(getString(R.string.video));
        changeTitle();
    }

    private void changeTitle() {
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        toolbarBt.setTitle(getString(R.string.video));
                        btCheck.setVisibility(View.GONE);
                        break;
                    case 1:
                        toolbarBt.setTitle(getString(R.string.music));
                        btCheck.setVisibility(View.GONE);
                        break;
                    case 2:
                        toolbarBt.setTitle(getString(R.string.cloud_player));
                        btCheck.setVisibility(View.GONE);
                        break;
                    case 3:
                        toolbarBt.setTitle(getString(R.string.bt));
                        btCheck.setVisibility(View.VISIBLE);
                        break;
                    case 4:
                        toolbarBt.setTitle(getString(R.string.local_file));
                        btCheck.setVisibility(View.GONE);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mViewPager.getCurrentItem() == 3) {
                myOnBackPressed.onBackPressed();
                return true;
            } else {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 注意
                intent.addCategory(Intent.CATEGORY_HOME);
                this.startActivity(intent);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("fuck", "应用被暂停了");
    }

}
