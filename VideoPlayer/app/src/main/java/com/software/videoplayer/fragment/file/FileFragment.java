package com.software.videoplayer.fragment.file;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.software.videoplayer.R;
import com.software.videoplayer.activity.file.LocaleFileBrowser;
import com.software.videoplayer.activity.file.LocaleFileGallery;
import com.software.videoplayer.activity.file.LocaleMediaFileBrowser;
import com.software.videoplayer.constants.MyConstants;
import com.software.videoplayer.util.FileManager;
import com.software.videoplayer.util.FileUtils;

import java.io.File;



/**
 * A simple {@link Fragment} subclass.
 */
public class FileFragment extends Fragment implements View.OnClickListener {

    private View root;

    private String extSdCardPath;
    private FileManager bfm;

    private LinearLayout local_music,local_video,local_picture,local_down,local_ram, local_sdcard;

    private final int REQUEST = 1;
    private String format;
    private String MP4Number;
    private TextView musicCnt;
    private TextView videoCnt;
    /**
     * 单例对象实例
     */
  private static FileFragment instance = null;

          public static FileFragment getInstance() {
               if (instance == null) {
                        instance = new FileFragment();
                  }
              return instance;
           }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.localefile_main, container, false);
        bfm = FileManager.getInstance();
        findView();
        clickView();
        return root;
    }

    private void clickView() {
        local_picture.setOnClickListener(this);
        local_music.setOnClickListener(this);
        local_video.setOnClickListener(this);
        local_down.setOnClickListener(this);
        local_ram.setOnClickListener(this);
        local_sdcard.setOnClickListener(this);
    }

    @SuppressLint("StringFormatMatches")
    private void findView() {

        local_picture = (LinearLayout) root.findViewById(R.id.localefile_picture);
        local_music = (LinearLayout) root.findViewById(R.id.localefile_music);
        local_video = (LinearLayout) root.findViewById(R.id.localefile_video);
        local_down = (LinearLayout) root.findViewById(R.id.localefile_download);
        local_ram = (LinearLayout) root.findViewById(R.id.localefile_ram);
        local_sdcard = (LinearLayout) root.findViewById(R.id.localefile_sdcard);
        musicCnt = (TextView) root.findViewById(R.id.localefile_music_cnt);
        videoCnt = (TextView) root.findViewById(R.id.localefile_video_cnt);

        TextView picCnt = (TextView) root.findViewById(R.id.localefile_pic_cnt);
        picCnt.setText(String.format(getString(R.string.bxfile_media_cnt), bfm.getMediaFilesCnt(getActivity(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI)));

        extSdCardPath = FileUtils.getExtSdCardPath();
        if(!TextUtils.isEmpty(extSdCardPath)){
            View local_sdcard = root.findViewById(R.id.localefile_sdcard);
            View localefile_sdcard2 = root.findViewById(R.id.localefile_sdcard2);
            View localefile_extSdcard = root.findViewById(R.id.localefile_extSdcard);
            localefile_sdcard2.setVisibility(View.VISIBLE);
            localefile_extSdcard.setVisibility(View.VISIBLE);
            local_sdcard.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //音乐数目
        if (MyConstants.localeMP3Number != 0){
            format = "("+MyConstants.localeMP3Number +")";
        }else {
            format = String.format(getString(R.string.bxfile_media_cnt), bfm.getMediaFilesCnt(getActivity(), MediaStore.Audio.Media.EXTERNAL_CONTENT_URI));
        }
        musicCnt.setText(format);
        //视频数目
        if (MyConstants.localeMP4Number != 0){
            MP4Number = "("+ MyConstants.localeMP4Number +")";
        }else {
            MP4Number = String.format(getString(R.string.bxfile_media_cnt), bfm.getMediaFilesCnt(getActivity(), MediaStore.Video.Media.EXTERNAL_CONTENT_URI));
        }
        videoCnt.setText(MP4Number);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.localefile_music:
                Intent intent5 = new Intent(getActivity(),LocaleMediaFileBrowser.class);
                intent5.putExtra("title", getString(R.string.bxfile_music));
                intent5.setData(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent5,REQUEST);
                break;
            case R.id.localefile_video:
                Intent intent6 = new Intent(getActivity(),LocaleMediaFileBrowser.class);
                intent6.putExtra("title", getString(R.string.bxfile_video));
                intent6.setData(MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent6,REQUEST);
                break;
            case R.id.localefile_picture:
                Intent intent7 = new Intent(getActivity(),LocaleFileGallery.class);
                intent7.putExtra("title", getString(R.string.bxfile_image));
                startActivityForResult(intent7,REQUEST);
                break;
            case R.id.localefile_download:
                if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                    Intent intent1 = new Intent(getActivity(),LocaleFileBrowser.class);
                    intent1.putExtra("title", getString(R.string.bxfile_download));
                    String base = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator;
                    String downloadPath = base+"download";
                    File file = new File(downloadPath);
                    if(!file.exists())
                        file.mkdir();
                    intent1.putExtra("startPath", downloadPath);
                    startActivityForResult(intent1,REQUEST);
                }else{
                    Toast.makeText(getContext(), getString(R.string.SDCardNotMounted), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.localefile_ram:
                Intent intent2 = new Intent(getActivity(),LocaleFileBrowser.class);
                intent2.putExtra("title", getString(R.string.bxfile_ram));
                intent2.putExtra("startPath", "/");
                startActivityForResult(intent2,REQUEST);
                break;
            case R.id.localefile_sdcard:
            case R.id.localefile_sdcard2:
                if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                    Intent intent3 = new Intent(getActivity(),LocaleFileBrowser.class);
                    intent3.putExtra("title", getString(R.string.bxfile_sdcard));
                    intent3.putExtra("startPath", Environment.getExternalStorageDirectory().getAbsolutePath());
                    startActivityForResult(intent3,REQUEST);
                }else{
                    Toast.makeText(getContext(), getString(R.string.SDCardNotMounted), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.localefile_extSdcard:
                Intent intent4 = new Intent(getActivity(),LocaleFileBrowser.class);
                intent4.putExtra("title", getString(R.string.bxfile_extsdcard));
                intent4.putExtra("startPath", extSdCardPath);
                startActivityForResult(intent4,REQUEST);
                break;
        }
    }
}
