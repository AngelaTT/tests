package com.software.videoplayer.activity.cloud;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.software.videoplayer.R;
import com.software.videoplayer.constants.MyConstants;
import com.software.videoplayer.service.NetWorkStateReceiver;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WebShowActivity extends AppCompatActivity {

    @BindView(R.id.wsa_wb)
    WebView wsaWb;
    @BindView(R.id.cloud_play)
    TextView cloudPlay;
    @BindView(R.id.toolbar_main)
    Toolbar toolbarMain;
    @BindView(R.id.pb_progress)
    ProgressBar pbProgress;
    private String getUrl;
    private NetWorkStateReceiver netWorkStateReceiver;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_show);
        ButterKnife.bind(this);


        cloudPlay.setVisibility(View.GONE);

        if (getIntent() != null) {
            toolbarMain.setTitle(getIntent().getStringExtra(MyConstants.WEB_NAME));
            setSupportActionBar(toolbarMain);
            WebSettings webSettings = wsaWb.getSettings();
                webSettings.setJavaScriptEnabled(true);
            webSettings.setDomStorageEnabled(true);
            webSettings.setSupportZoom(true);

            wsaWb.setWebChromeClient(new WebChromeClient(){
                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    super.onProgressChanged(view, newProgress);
                    for (int i=0;i<newProgress;i++) {
                        pbProgress.setProgress(i);
                    }
                }
            });


            wsaWb.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    pbProgress.setVisibility(View.VISIBLE);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    getUrl = url;
                    cloudPlay.setVisibility(View.VISIBLE);
                    pbProgress.setVisibility(View.GONE);
                }
            });
            wsaWb.loadUrl(getIntent().getStringExtra(MyConstants.WEB_URL));
        }
    }

    @Override
    public void onBackPressed() {
        if (wsaWb.canGoBack()) {
            wsaWb.goBack();
        } else {
            super.onBackPressed();
        }

    }

    @OnClick(R.id.cloud_play)
    public void onViewClicked() {
        Intent intent = new Intent(WebShowActivity.this, WebPlayActivity.class);
        intent.putExtra(MyConstants.WEB_URL, getUrl);
        startActivity(intent);
    }

    /**
     * 网络状态变化监测（利用广播），在onResume()方法中注册，onPause()方法中注销
     */
    @Override
    protected void onResume() {
        if (netWorkStateReceiver == null) {
            netWorkStateReceiver = new NetWorkStateReceiver();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netWorkStateReceiver, filter);
        System.out.println("注册");
        super.onResume();
    }

    //onPause()方法注销
    @Override
    protected void onPause() {
        unregisterReceiver(netWorkStateReceiver);
        System.out.println("注销");
        super.onPause();
    }

}
