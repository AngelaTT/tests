package com.software.videoplayer.util;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.shuyu.gsyvideoplayer.utils.GSYVideoType;
import com.shuyu.gsyvideoplayer.video.CustomGSYVideoPlayer;
import com.software.videoplayer.R;
import com.software.videoplayer.activity.video.DetailPlayerActivity;
import com.software.videoplayer.constants.MyConstants;
import com.software.videoplayer.constants.VideoConstants;
import com.software.videoplayer.ui.MySettingDialog;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;

import butterknife.BindView;
import butterknife.ButterKnife;
import ezy.assist.compat.SettingsCompat;


/**
 * Created by shuyu on 2016/12/23.
 * CustomGSYVideoPlayer是试验中，建议使用的时候使用StandardGSYVideoPlayer
 */
public class LandLayoutVideo extends CustomGSYVideoPlayer {

    @BindView(R.id.fly_screen)
    TextView flyScreen;
    @BindView(R.id.screenshot)
    TextView screenshot;
    @BindView(R.id.vcr)
    TextView vcr;
    @BindView(R.id.no_sound)
    TextView noSound;
    @BindView(R.id.video_settings)
    TextView videoSettings;

    private int oldSound = 0;
    public static boolean showsamll = false;
    private MySettingDialog mySettingDialog;

    /**
     * 1.5.0开始加入，如果需要不同布局区分功能，需要重载
     */
    public LandLayoutVideo(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public LandLayoutVideo(Context context) {
        super(context);
    }

    public LandLayoutVideo(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    //这个必须配置最上面的构造才能生效
    @Override
    public int getLayoutId() {
        if (showsamll) {
            return R.layout.sample_video;
        }
        return R.layout.sample_video_land;

    }

    @Override
    protected void updateStartImage() {

        ImageView imageView = (ImageView) mStartButton;
        if (mCurrentState == CURRENT_STATE_PLAYING) {
            imageView.setImageResource(R.drawable.video_click_pause_selector);
        } else if (mCurrentState == CURRENT_STATE_ERROR) {
            imageView.setImageResource(R.drawable.video_click_play_selector);
        } else {
            imageView.setImageResource(R.drawable.video_click_play_selector);
        }
    }

    @Override
    protected void init(Context context) {
        super.init(context);
        ButterKnife.bind(this);
        if (getAudioManager().getStreamVolume(AudioManager.STREAM_MUSIC) > 0) {
            oldSound = getAudioManager().getStreamVolume(AudioManager.STREAM_MUSIC);
            noSound.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.sound), null, null);
        } else {
            noSound.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.sound_close), null, null);
        }

        flyScreen.setOnClickListener(v -> flyScreen());
        screenshot.setOnClickListener(v -> screenshot());
        noSound.setOnClickListener(v -> closeSound());
        videoSettings.setOnClickListener(v -> settings());

        mFullscreenButton.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(getContext(), DetailPlayerActivity.class));
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            context.startActivity(intent);
        });
    }

    private void settings() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_setting, null);
        mySettingDialog = new MySettingDialog(getContext(), view);
        mySettingDialog.show();
        ImageView subtract = (ImageView) view.findViewById(R.id.txt_subtract);
        ImageView add = (ImageView) view.findViewById(R.id.txt_add);
        TextView textView = (TextView) view.findViewById(R.id.txt_speed);

        textView.setText(String.valueOf(getSpeed()));

        subtract.setOnClickListener(v -> {
            float speed = Float.parseFloat(textView.getText().toString());
            if (speed >= 0) {
                if (speed >0.5) {
                    speed = (float) (speed - 0.5);
                }
                textView.setText(String.valueOf(speed));
                setSpeed(speed);
            }
        });
        add.setOnClickListener(v -> {
            float speed = Float.parseFloat(textView.getText().toString());
            if (speed >= 0) {
                if (speed >=0.5) {
                    speed = (float) (speed + 0.5);
                }
                textView.setText(String.valueOf(speed));
                setSpeed(speed);
            }
        });
        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.screen_size);
        switch (GSYVideoType.getShowType()) {
            case 0:
                radioGroup.check(R.id.default_size);
                break;
            case 1:
                radioGroup.check(R.id.normal_size);
                break;
            case 2:
                radioGroup.check(R.id.small_size);
                break;
        }
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.default_size:
                    GSYVideoType.setShowType(0);
                    if (mTextureView != null) {
                        mTextureView.requestLayout();
                    }

                    break;
                case R.id.normal_size:
                    GSYVideoType.setShowType(1);
                    if (mTextureView != null) {
                        mTextureView.requestLayout();
                    }
                    break;
                case R.id.small_size:
                    GSYVideoType.setShowType(2);
                    if (mTextureView != null) {
                        mTextureView.requestLayout();
                    }
                    break;
                default:
                    break;
            }
        });
        Switch mSwitch = (Switch) view.findViewById(R.id.switch_circulation);
        if (isLooping()) {
            mSwitch.setChecked(true);
        } else {
            mSwitch.setSelected(false);
        }
        mSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                setLooping(true);
            } else {
                setLooping(false);
            }
        });
        Switch deCode = (Switch) view.findViewById(R.id.switch_hard_decode);
        if (GSYVideoType.isMediaCodec()) {
            deCode.setChecked(true);
        } else {
            deCode.setChecked(false);
        }
        deCode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                GSYVideoType.enableMediaCodec();
            } else {
                GSYVideoType.disableMediaCodec();
            }
        });
    }
    private void closeSound() {
        AudioManager audioManager = getAudioManager();
        int sound = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (sound > 0) {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
            oldSound = sound;
            noSound.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.sound_close), null, null);
        } else {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, oldSound, 0);
            noSound.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.sound), null, null);
          /*  此处注释为修复点击静音按钮无反应的bug
          if (oldSound > 0) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, oldSound, 0);
                noSound.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.sound), null, null);
            } else {
                noSound.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.sound_close), null, null);
            }*/
        }
    }

    private void screenshot() {
        ImageView imageView = new ImageView(getContext());
        mStartButton.performClick();
        Bitmap bitmap = getmFullPauseBitmap();
        imageView.setImageBitmap(bitmap);

        new AlertDialog.Builder(getContext())
                .setView(imageView)
                .setOnDismissListener(dialog -> mStartButton.performClick())
                .setTitle("截图保存")
                .setPositiveButton("保存", (dialog, which) ->
                        new Thread(() -> {
                            File imageFile = Utils.ensureCreated(new File(VideoConstants.SCREENSHOT_PATH));
                            BufferedOutputStream bos = null;
                            try {
                                bos = new BufferedOutputStream(new FileOutputStream(imageFile + "/" + System.currentTimeMillis() + ".png"));
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                                bos.flush();
                                bos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                                getContext().sendBroadcast(new Intent(MyConstants.UPDATE_SCREENSHOT));
                            }
                        }).start())
                .setNegativeButton("取消", null)
                .create().show();
    }

    private void flyScreen() {
        if (!SettingsCompat.canDrawOverlays(getContext())) {
            Toast.makeText(getContext(),"请开启悬浮窗权限，否则飞屏无法弹出！",Toast.LENGTH_LONG).show();
            SettingsCompat.manageDrawOverlays(getContext());
        } else {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 注意
            intent.addCategory(Intent.CATEGORY_HOME);
            getContext().startActivity(intent);

            showsamll = true;
            Point point = new Point(1000, 1000);
            showSmallVideo(point, false, false);
//        Toast.makeText(getContext(),"如果飞屏没有弹出，请到：安全中心-授权管理-权限管理中手动开启悬浮窗权限",Toast.LENGTH_LONG).show();
        }
    }

    public MySettingDialog getPopupWindow() {
        return mySettingDialog;
    }

}
