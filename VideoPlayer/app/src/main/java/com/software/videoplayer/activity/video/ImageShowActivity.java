package com.software.videoplayer.activity.video;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.software.videoplayer.R;
import com.software.videoplayer.constants.MyConstants;
import com.software.videoplayer.constants.VideoConstants;
import com.software.videoplayer.model.VideoInfo;
import com.software.videoplayer.util.FileUtils;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.senab.photoview.PhotoView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ImageShowActivity extends Activity {

    @BindView(R.id.img_show)
    PhotoView imageShow;
    @BindView(R.id.img_share)
    ImageView imgShare;
    @BindView(R.id.Img_delete)
    ImageView ImgDelete;

    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image_show);
        ButterKnife.bind(this);


        if (getIntent() != null) {
            if (getIntent().getStringExtra(VideoConstants.VIDEO_PATH) != null) {
                url = getIntent().getStringExtra(VideoConstants.VIDEO_PATH);
            } else {
                VideoInfo videoInfo = getIntent().getParcelableExtra(VideoConstants.SCREENSHOT);
                url = videoInfo.getPath();
            }

            Glide.with(this).load(url).into(imageShow);
        }
    }

    @OnClick({R.id.img_share, R.id.Img_delete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_share:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(url)));
                startActivity(intent);
                break;
            case R.id.Img_delete:

                new AlertDialog.Builder(this)
                        .setTitle("提示")
                        .setMessage("确认删除这张图片吗？")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定", (dialog, which) -> {
                            FileUtils.delete(url);
                            sendBroadcast(new Intent(MyConstants.UPDATE_SCREENSHOT));
                            this.setResult(1, getIntent());
                            this.finish();

                        }).create().show();
                break;
        }
    }

}
