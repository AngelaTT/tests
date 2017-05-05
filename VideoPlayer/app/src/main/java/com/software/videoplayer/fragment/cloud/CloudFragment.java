package com.software.videoplayer.fragment.cloud;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.software.videoplayer.R;
import com.software.videoplayer.activity.cloud.WebShowActivity;
import com.software.videoplayer.adapter.CloudAdapter;
import com.software.videoplayer.constants.MyConstants;
import com.software.videoplayer.data.Stroe;
import com.software.videoplayer.model.CloudInfo;
import com.software.videoplayer.model.VideoInfo;
import com.software.videoplayer.service.NetWorkStateReceiver;
import com.software.videoplayer.util.VideoHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class CloudFragment extends Fragment {

    @BindView(R.id.cdf_et)
    EditText cdfEt;
    @BindView(R.id.go_go)
    ImageButton go;
    @BindView(R.id.cdf_gv)
    GridView cdfGv;
    @BindView(R.id.search_delete)
    ImageView searchDelete;

    private List<CloudInfo> cloudInfoList;
    CloudAdapter cloudAdapter;
    View adView;
    EditText name, url;
    private NetWorkStateReceiver netWorkStateReceiver;

    private static CloudFragment instence = null;
    public static CloudFragment getInstence(){
        if (instence == null){
            instence = new CloudFragment();
        }
        return instence;
    }

    @SuppressWarnings("unchecked")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cloud, container, false);
        ButterKnife.bind(this, view);

        cdfEt.setOnClickListener(v -> cdfEt.setCursorVisible(true));

       cdfEt.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence s, int start, int count, int after) {

           }

           @Override
           public void onTextChanged(CharSequence s, int start, int before, int count) {

           }

           @Override
           public void afterTextChanged(Editable s) {
               if (cdfEt.getText().toString().equals("")) {
                   searchDelete.setVisibility(View.GONE);
               } else {
                   searchDelete.setVisibility(View.VISIBLE);
               }
           }
       });

        searchDelete.setOnClickListener(v -> cdfEt.setText(""));

        go.setOnClickListener(v -> {
            String stream = cdfEt.getText().toString();
            if (stream.equals("")) {

                Toast.makeText(getContext(), "请输输入地址!", Toast.LENGTH_SHORT).show();

            } else {
                VideoInfo videoInfo = new VideoInfo();
                videoInfo.setPath(stream);
                File file = new File(stream);
                videoInfo.setName(file.getName());
                VideoHelper.getVideoHelper().playerVideo(getContext(), videoInfo, true);
            }
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm.isActive()) {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });

        new Thread(() -> {

            if (Stroe.getData(getActivity(), MyConstants.CLOUD, CloudInfo.class) == null) {
                cloudInfoList = new ArrayList<>();
                cloudInfoList.add(new CloudInfo("优酷", "http://www.youku.com/"));
                cloudInfoList.add(new CloudInfo("土豆", "http://www.tudou.com/"));
                cloudInfoList.add(new CloudInfo("腾讯视频", "https://v.qq.com/"));
                cloudInfoList.add(new CloudInfo("聚力视频", "http://www.pptv.com/"));
                cloudInfoList.add(new CloudInfo("爱奇艺", "http://www.iqiyi.com/"));
                cloudInfoList.add(new CloudInfo("乐视", "http://www.le.com/"));
                cloudInfoList.add(new CloudInfo("搜狐视频", "http://tv.sohu.com/"));
                cloudInfoList.add(new CloudInfo("新浪视频", "http://video.sina.com.cn/"));
                cloudInfoList.add(new CloudInfo("华数TV", "http://www.wasu.cn/"));
                cloudInfoList.add(new CloudInfo("56网", "http://www.56.com/"));
                cloudInfoList.add(new CloudInfo("酷6网", "http://www.ku6.com/"));
                cloudInfoList.add(new CloudInfo("add", ""));
                Stroe.storeData(getActivity(), MyConstants.CLOUD, cloudInfoList);
            } else {
                cloudInfoList = (List<CloudInfo>) Stroe.getData(getActivity(), MyConstants.CLOUD, CloudInfo.class);
            }
            getActivity().runOnUiThread(() -> {
                cloudAdapter = new CloudAdapter(getContext(), cloudInfoList);
                cdfGv.setAdapter(cloudAdapter);
            });
        }).start();

        cdfGv.setOnItemClickListener((parent, view1, position, id) -> {

            if (cloudInfoList.get(position).getName().equals("add")) {
                showDialog(false, position);
            } else {
                String url = cloudInfoList.get(position).getPath();
                Intent intent = new Intent(getContext(), WebShowActivity.class);
                intent.putExtra(MyConstants.WEB_URL, url);
                intent.putExtra(MyConstants.WEB_NAME, cloudInfoList.get(position).getName());
                startActivity(intent);
            }
        });
        cdfGv.setOnItemLongClickListener((parent, view12, position, id) -> {
            String[] strings = new String[]{"删除", "修改"};
            if (!cloudInfoList.get(position).getName().equals("add")) {
                new AlertDialog.Builder(getContext())
                        .setItems(strings, (dialog, which) -> {
                            switch (which) {
                                case 0:
                                    cloudInfoList.remove(position);
                                    cloudAdapter = new CloudAdapter(getContext(), cloudInfoList);
                                    cdfGv.setAdapter(cloudAdapter);
                                    break;
                                case 1:
                                    showDialog(true, position);
                                    break;
                                default:
                                    break;
                            }
                            new Thread(() -> Stroe.storeData(getActivity(), MyConstants.CLOUD, cloudInfoList)).start();
                        })
                        .create().show();
            }

            return true;
        });


        return view;
    }

    @SuppressLint("SetTextI18n")
    private void showDialog(boolean isChange, int position) {

        adView = LayoutInflater.from(getContext()).inflate(R.layout.alertdialog, null);
        name = (EditText) adView.findViewById(R.id.ad_name);
        url = (EditText) adView.findViewById(R.id.ad_url);
        if (!isChange) {
            url.setText("http://");
        } else {
            name.setText(cloudInfoList.get(position).getName());
            url.setText(cloudInfoList.get(position).getPath());
        }


        new AlertDialog.Builder(getActivity())
                .setView(adView)
                .setTitle("网站添加")
                .setPositiveButton("确定", (dialog, which) -> {
                    String gN = name.getText().toString();
                    String gU = url.getText().toString();
                    if (!gN.equals("") && !gU.equals("")) {

                        if (!isChange) {
                            cloudInfoList.add(cloudInfoList.size() - 1, new CloudInfo(gN, gU));
                            cloudAdapter = new CloudAdapter(getContext(), cloudInfoList);
                            cdfGv.setAdapter(cloudAdapter);
                        } else {
                            cloudInfoList.get(position).setName(gN);
                            cloudInfoList.get(position).setPath(gU);
                        }
                        new Thread(() -> Stroe.storeData(getActivity(), MyConstants.CLOUD, cloudInfoList)).start();
                    }

                })
                .setNegativeButton("取消", null)
                .setOnDismissListener(dialog -> getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN))
                .create().show();

    }

  /*  *//**
     * 网络状态变化监测（利用广播），在onResume()方法中注册，onPause()方法中注销
     *//*
    @Override
    public void onResume() {
        if (netWorkStateReceiver == null) {
            netWorkStateReceiver = new NetWorkStateReceiver();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        getActivity().registerReceiver(netWorkStateReceiver, filter);
        System.out.println("注册");
        super.onResume();
    }

    //onPause()方法注销
    @Override
    public void onPause() {
        getActivity().unregisterReceiver(netWorkStateReceiver);
        System.out.println("注销");
        super.onPause();
    }
*/
}
