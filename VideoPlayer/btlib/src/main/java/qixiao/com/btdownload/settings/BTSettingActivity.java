package qixiao.com.btdownload.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.frostwire.jlibtorrent.SettingsPack;

import qixiao.com.btdownload.R;
import qixiao.com.btdownload.core.utils.SPUtils;

/**
 * Created by admin on 2017/4/5.
 */

public class BTSettingActivity extends Activity implements View.OnClickListener {
    private ImageView iv_jian;
    private ImageView iv_jia;
    private TextView tv_number;
    private RelativeLayout aboutMe;
    private int i;
    private RelativeLayout title;
    private ImageView ivBack;
    private TextView ivUnfold;
    private TextView ivTitle;
    private SettingsPack settingsPack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        findViews();
        initViews();
        initLisenter();
    }

    public void findViews() {
        iv_jian = ((ImageView) findViewById(R.id.iv_jian));
        iv_jia = ((ImageView) findViewById(R.id.iv_jia));
        tv_number = ((TextView) findViewById(R.id.tv_number));
        aboutMe = ((RelativeLayout) findViewById(R.id.rl_about_me));

        title = ((RelativeLayout) findViewById(R.id.rl_about_title));
        ivBack = ((ImageView) title.findViewById(R.id.iv_back));
        ivUnfold = ((TextView) title.findViewById(R.id.iv_unfold));
        ivTitle = ((TextView) title.findViewById(R.id.tv_title));
    }

    public void initViews() {
        ivUnfold.setVisibility(View.GONE);
        ivTitle.setText("设置");
        ivBack.setImageResource(R.drawable.fanhui);
        //默认可同时下载5个任务
        if ( 0 < SPUtils.getInt(this, SettingConstant.DOWNLOADNUMBER)){
            tv_number.setText(SPUtils.getInt(this,SettingConstant.DOWNLOADNUMBER)+"");
        }else if (tv_number.getText().toString().isEmpty()){
            SPUtils.put(this,SettingConstant.DOWNLOADNUMBER,5);
            tv_number.setText(SPUtils.getInt(this, SettingConstant.DOWNLOADNUMBER)+"");
        }
        i = Integer.parseInt(tv_number.getText().toString());
        settingsPack = new SettingsPack();
        settingsPack.connectionsLimit(i);
        settingsPack.activeDownloads(i);
    }

    public void initLisenter() {
        iv_jia.setOnClickListener(this);
        iv_jian.setOnClickListener(this);
        aboutMe.setOnClickListener(this);
        ivBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int i1 = view.getId();
        if (i1 == R.id.iv_jian) {
            if (i < 1) {
                tv_number.setText(0 + "");
            } else {
                i--;
                SPUtils.put(this, SettingConstant.DOWNLOADNUMBER, i);
                tv_number.setText(SPUtils.getInt(this, SettingConstant.DOWNLOADNUMBER) + "");

            }
            settingsPack.connectionsLimit(i);
            settingsPack.activeDownloads(i);

        } else if (i1 == R.id.iv_jia) {
            i++;
            SPUtils.put(this, SettingConstant.DOWNLOADNUMBER, i);
            tv_number.setText(SPUtils.getInt(this, SettingConstant.DOWNLOADNUMBER) + "");
            settingsPack.connectionsLimit(i);
            settingsPack.activeDownloads(i);

        } else if (i1 == R.id.rl_about_me) {
            startActivity(new Intent(this, AboutBTActivity.class));

        } else if (i1 == R.id.iv_back) {
            finish();

        }
    }
}
