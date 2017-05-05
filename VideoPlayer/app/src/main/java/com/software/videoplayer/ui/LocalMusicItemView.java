package com.software.videoplayer.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.software.videoplayer.R;
import com.software.videoplayer.data.model.Song;
import com.software.videoplayer.interfaces.IAdapterView;
import com.software.videoplayer.util.AlbumUtils;
import com.software.videoplayer.util.TimeUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/2/16
 * Time: 5:58 PM
 * Desc: LocalMusicItemView
 */
public class LocalMusicItemView extends RelativeLayout implements IAdapterView<Song> {

    @BindView(R.id.text_view_name)
    TextView textViewName;
    @BindView(R.id.text_view_artist)
    TextView textViewArtist;
    @BindView(R.id.text_view_duration)
    TextView textViewDuration;
    @BindView(R.id.music_thumb)
    ImageView musicThumb;
    @BindView(R.id.item_check)
    CheckBox checkBox;

   /* private boolean isSelected = false;
    private boolean isSelectedAll = false;*/

    public LocalMusicItemView(Context context) {
        super(context);
        View.inflate(context, R.layout.item_local_music, this);
        ButterKnife.bind(this);
    }

    @Override
    public void bind(Song song, int position) {
        //修复个别条目文字乱码bug
        String displayName = song.getDisplayName();
        String Name = transcoding(displayName,"UTF-8");
        textViewName.setText(Name);
        textViewArtist.setText(song.getArtist());
        textViewDuration.setText(TimeUtils.formatDuration(song.getDuration()));
        Bitmap bitmap = AlbumUtils.parseAlbum(song);
        if (bitmap == null) {
            musicThumb.setImageResource(R.drawable.default_thumb);
        } else {
            musicThumb.setImageBitmap(AlbumUtils.getCroppedBitmap(bitmap));
        }

    }

    @Override
    public CheckBox getCheckBox(){
        return this.checkBox;
    }

    /**
     * 将字符串转换成指定编码格式
     *
     * @param str
     * @param encode
     * @return
     */
    public static String transcoding(String str, String encode) {
        String df = "ISO-8859-1";
        try {
            String en = getEncoding(str);
            if (en == null)
                en = df;
            return new String(str.getBytes(en), encode);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    /**
     * 判断字符串的编码
     *
     * @param str
     * @return
     */
    public static String getEncoding(String str) {
        String encode[] = new String[]{
                "UTF-8",
                "ISO-8859-1",
                "GB2312",
                "GBK",
                "GB18030",
                "Big5",
                "Unicode",
                "ASCII"
        };
        for (int i = 0; i < encode.length; i++){
            try {
                if (str.equals(new String(str.getBytes(encode[i]), encode[i]))) {
                    return encode[i];
                }
            } catch (Exception ex) {
            }
        }

        return "";
    }

}
