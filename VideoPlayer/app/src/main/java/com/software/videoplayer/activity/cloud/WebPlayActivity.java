package com.software.videoplayer.activity.cloud;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.software.videoplayer.R;
import com.software.videoplayer.constants.MyConstants;
import com.software.videoplayer.service.NetWorkStateReceiver;

import butterknife.BindView;
import butterknife.ButterKnife;


public class WebPlayActivity extends Activity {
//    @BindView(R.id.web_filechooser)
//    X5WebView webView;

    @BindView(R.id.web_show_activity)
    WebView webView;
    @BindView(R.id.full_play)
    FrameLayout fullPlay;

    private WebChromeClient.CustomViewCallback mCallBack;
    private NetWorkStateReceiver netWorkStateReceiver;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_play);
        ButterKnife.bind(this);
        Intent intent = getIntent();

        if (intent != null) {

            String url = intent.getStringExtra(MyConstants.WEB_URL);

            WebSettings ws = webView.getSettings();
            // 网页内容的宽度是否可大于WebView控件的宽度
            ws.setLoadWithOverviewMode(false);
            // 保存表单数据
            ws.setSaveFormData(true);
            // 是否应该支持使用其屏幕缩放控件和手势缩放
            ws.setSupportZoom(false);
            ws.setBuiltInZoomControls(false);
            ws.setDisplayZoomControls(false);
            // 启动应用缓存
            ws.setAppCacheEnabled(true);
            // 设置缓存模式
            ws.setCacheMode(WebSettings.LOAD_DEFAULT);
            // setDefaultZoom  api19被弃用
            // 设置此属性，可任意比例缩放。
            ws.setUseWideViewPort(false);
            // 告诉WebView启用JavaScript执行。默认的是false。
            ws.setJavaScriptEnabled(true);
            //  页面加载好以后，再放开图片
            ws.setBlockNetworkImage(false);
//            // 使用localStorage则必须打开
//            ws.setDomStorageEnabled(true);
            // 排版适应屏幕
            ws.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
            // WebView是否支持多个窗口。
            ws.setSupportMultipleWindows(false);

            // webview从5.0开始默认不允许混合模式,https中不能加载http资源,需要设置开启。
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ws.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            }
            webView.setWebChromeClient(new WebChromeClient(){
                @Override
                public void onShowCustomView(View view, CustomViewCallback callback) {
                    fullScreen();
                    webView.setVisibility(View.GONE);
                    fullPlay.setVisibility(View.VISIBLE);
                    fullPlay.addView(view);
                    mCallBack=callback;
                    super.onShowCustomView(view, callback);
                }

                @Override
                public void onHideCustomView() {
                    fullScreen();
                    if (mCallBack!=null){
                        mCallBack.onCustomViewHidden();
                    }
                    webView.setVisibility(View.VISIBLE);
                    fullPlay.removeAllViews();
                    fullPlay.setVisibility(View.GONE);
                    super.onHideCustomView();
                }
            });
            webView.setWebViewClient(new WebViewClient() {

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                }

                @Override
                public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                    handler.proceed();
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    Log.e("fuck", url + "地址是什么");
                }

                @Override
                public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                    Log.e("fuck", url + "载入的地址了");
                    if (url.startsWith("https://s.175bar.com/")) {
                        return new WebResourceResponse(null, null, null);
                    } else {
                        return super.shouldInterceptRequest(view, url);
                    }
                }


            });
            /**
             *       https://api.47ks.com/webcloud/?v=http://v.youku.com/v_show/id_XMjUxMDkzOTkyNA==.html
             *       https://aikan-tv.com/tong.php?url=http://v.youku.com/v_show/id_XMjUxMDkzOTkyNA==.html
             *      http://www.wmxz.wang/video.php?url=
             *     http://www.sfsft.com/video.php?url=
             */
            webView.loadUrl("https://api.47ks.com/webcloud/?v=" + url);

//            webView.loadUrl("https://api.47ks.com/webcloud/?v=" + url);
//
//            getWindow().setFormat(PixelFormat.TRANSLUCENT);
//
//            webView.getView().setOverScrollMode(View.OVER_SCROLL_ALWAYS);
//
//            webView.setWebViewClient(new WebViewClient(){
//                @Override
//                public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
//                    if (url.startsWith("https://s.175bar.com/")) {
//                        return new WebResourceResponse(null, null, null);
//                    } else {
//                        return super.shouldInterceptRequest(view, url);
//                    }
//                }
//            });


//            webView.addJavascriptInterface(new WebViewJavaScriptFunction() {
//
//                @Override
//                public void onJsFunctionCalled(String tag) {
//                    // TODO Auto-generated method stub
//
//                }
//
//                @JavascriptInterface
//                public void onX5ButtonClicked(){
//                    WebPlayActivity.this.enableX5FullscreenFunc();
//                }
//
//                @JavascriptInterface
//                public void onCustomButtonClicked(){
//                    WebPlayActivity.this.disableX5FullscreenFunc();
//                }
//
//                @JavascriptInterface
//                public void onLiteWndButtonClicked(){
//                    WebPlayActivity.this.enableLiteWndFunc();
//                }
//
//                @JavascriptInterface
//                public void onPageVideoClicked(){
//                    WebPlayActivity.this.enablePageVideoFunc();
//                }
//            }, "Android");
//        }
        }
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        // TODO Auto-generated method stub
//        try{
//            super.onConfigurationChanged(newConfig);
//            if(getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE){
//
//            }else if(getResources().getConfiguration().orientation==Configuration.ORIENTATION_PORTRAIT){
//
//            }
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//
//    }
//
//    ///////////////////////////////////////////
//    //向webview发出信息
//    private void enableX5FullscreenFunc(){
//        Log.i("jsToAndroid","enableX5FullscreenFunc happend!");
//
//        if(webView.getX5WebViewExtension()!=null){
//            Toast.makeText(this, "开启X5全屏播放模式", Toast.LENGTH_LONG).show();
//            Bundle data = new Bundle();
//
//            data.putBoolean("standardFullScreen", false);//true表示标准全屏，false表示X5全屏；不设置默认false，
//
//            data.putBoolean("supportLiteWnd", false);//false：关闭小窗；true：开启小窗；不设置默认true，
//
//            data.putInt("DefaultVideoScreen", 2);//1：以页面内开始播放，2：以全屏开始播放；不设置默认：1
//
//            webView.getX5WebViewExtension().invokeMiscMethod("setVideoParams", data);
//        }
//    }
//    private void disableX5FullscreenFunc(){
//        Log.i("jsToAndroid","disableX5FullscreenFunc happend!");
//        if(webView.getX5WebViewExtension()!=null){
//            Toast.makeText(this, "恢复webkit初始状态", Toast.LENGTH_LONG).show();
//            Bundle data = new Bundle();
//
//            data.putBoolean("standardFullScreen", true);//true表示标准全屏，会调起onShowCustomView()，false表示X5全屏；不设置默认false，
//
//            data.putBoolean("supportLiteWnd", false);//false：关闭小窗；true：开启小窗；不设置默认true，
//
//            data.putInt("DefaultVideoScreen", 2);//1：以页面内开始播放，2：以全屏开始播放；不设置默认：1
//
//            webView.getX5WebViewExtension().invokeMiscMethod("setVideoParams", data);
//        }
//    }
//    private void enableLiteWndFunc(){
//        Log.i("jsToAndroid","enableLiteWndFunc happend!");
//        if(webView.getX5WebViewExtension()!=null){
//            Toast.makeText(this, "开启小窗模式", Toast.LENGTH_LONG).show();
//            Bundle data = new Bundle();
//
//            data.putBoolean("standardFullScreen", false);//true表示标准全屏，会调起onShowCustomView()，false表示X5全屏；不设置默认false，
//
//            data.putBoolean("supportLiteWnd", true);//false：关闭小窗；true：开启小窗；不设置默认true，
//
//            data.putInt("DefaultVideoScreen", 2);//1：以页面内开始播放，2：以全屏开始播放；不设置默认：1
//
//            webView.getX5WebViewExtension().invokeMiscMethod("setVideoParams", data);
//        }
//    }
//    private void enablePageVideoFunc(){
//        Log.i("jsToAndroid","enablePageVideoFunc happend!");
//        if(webView.getX5WebViewExtension()!=null){
//            Toast.makeText(this, "页面内全屏播放模式", Toast.LENGTH_LONG).show();
//            Bundle data = new Bundle();
//
//            data.putBoolean("standardFullScreen", false);//true表示标准全屏，会调起onShowCustomView()，false表示X5全屏；不设置默认false，
//
//            data.putBoolean("supportLiteWnd", false);//false：关闭小窗；true：开启小窗；不设置默认true，
//
//            data.putInt("DefaultVideoScreen", 1);//1：以页面内开始播放，2：以全屏开始播放；不设置默认：1
//
//            webView.getX5WebViewExtension().invokeMiscMethod("setVideoParams", data);
//        }
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

    private void fullScreen() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }
}
