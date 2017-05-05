package com.software.videoplayer.util;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import com.software.videoplayer.activity.video.DetailPlayerActivity;
import com.software.videoplayer.activity.video.ImageShowActivity;
import com.software.videoplayer.constants.VideoConstants;
import com.software.videoplayer.model.VideoInfo;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 *  User: Moon
 *  Data: 2017/2/15.
 */

public class VideoHelper {

    private static VideoHelper videoHelper = new VideoHelper();

    private VideoHelper() {}

    public static VideoHelper getVideoHelper() {
        return videoHelper;
    }

    public List<VideoInfo> getVideoInfoList() {
        List<VideoInfo> allVideoList;// 视频信息集合
        allVideoList = new ArrayList<>();
        getVideoFile(allVideoList, Environment.getExternalStorageDirectory());// 获得视频文件
        return allVideoList;
    }
    private List<VideoInfo> getVideoFile(final List<VideoInfo> list, File file) {// 获得视频文件

        file.listFiles(file1 -> {
            // sdCard找到视频名称
            String name = file1.getName();

            int i = name.lastIndexOf('.');
            if (i != -1 && !file1.isDirectory()) {
                    name = name.substring(i);
                if (name.equalsIgnoreCase(".mp4")
                        ||name.equalsIgnoreCase(".blv")
                        || name.equalsIgnoreCase(".3gp")
                        || name.equalsIgnoreCase(".wmv")
                        || name.equalsIgnoreCase(".ts")
                        || name.equalsIgnoreCase(".rmvb")
                        || name.equalsIgnoreCase(".mov")
                        || name.equalsIgnoreCase(".m4v")
                        || name.equalsIgnoreCase(".avi")
                        || name.equalsIgnoreCase(".m3u8")
                        || name.equalsIgnoreCase(".3gpp")
                        || name.equalsIgnoreCase(".3gpp2")
                        || name.equalsIgnoreCase(".mkv")
                        || name.equalsIgnoreCase(".flv")
                        || name.equalsIgnoreCase(".divx")
                        || name.equalsIgnoreCase(".f4v")
                        || name.equalsIgnoreCase(".rm")
                        || name.equalsIgnoreCase(".asf")
                        || name.equalsIgnoreCase(".ram")
                        || name.equalsIgnoreCase(".mpg")
                        || name.equalsIgnoreCase(".v8")
                        || name.equalsIgnoreCase(".swf")
                        || name.equalsIgnoreCase(".m2v")
                        || name.equalsIgnoreCase(".asx")
                        || name.equalsIgnoreCase(".ra")
                        || name.equalsIgnoreCase(".ndivx")
                        || name.equalsIgnoreCase(".xvid")) {

                    if (file1.length() > 1048576) {
                        MediaPlayer player = new MediaPlayer();
                        try {
                            player.setDataSource(file1.getAbsolutePath());  //recordingFilePath（）为音频文件的路径
                            player.prepare();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        double duration = player.getDuration();//获取音频的时间
                        player.release();//记得释放资源
                        VideoInfo vi = new VideoInfo(file1.getName(), file1.getParentFile().getName(), file1.getAbsolutePath(), formatTime((int) duration), formatFileSize(file1.length()));
                        vi.setNew(false);
                        list.add(vi);
                        return true;
                    }
                }
                } else if (file1.isDirectory()) {
                    getVideoFile(list, file1);
                }
            return false;
        });
        return list;
    }

    public static Bitmap getVideoThumbnail(String filePath) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        finally {
            try {
                retriever.release();
            }
            catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    public String formatFileSize(long fileS) {//转换文件大小
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString ;
        if (fileS == 0) {
            fileSizeString = "0 B";
        }
        else if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }

    public List<VideoInfo> queryVideo(Context context) {
        List<VideoInfo> videoList = new ArrayList<>();
        Cursor mCursor=context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        try {
            if (mCursor != null) {
                while (mCursor.moveToNext()) {
                    String name = mCursor.getString(mCursor.getColumnIndex(MediaStore.Video.VideoColumns.DISPLAY_NAME));
                    String bucket = mCursor.getString(mCursor.getColumnIndex(MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME));
                    String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA));
                    String time = formatTime(mCursor.getInt(mCursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DURATION)));
                    String size = formatFileSize(mCursor.getLong(mCursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.SIZE)));
                    VideoInfo videoInfo = new VideoInfo(name,bucket,path,time,size);
                    videoInfo.setNew(false);
                    if (!videoList.contains(videoInfo)&&!videoInfo.getName().contains(".mds")&&!videoInfo.getName().contains(".apk")) {
                        videoList.add(videoInfo);
                    }
                }
                return videoList;
            } else {
                return null;
            }
        } finally {
            if (mCursor != null) {
                mCursor.close();
            }
        }
    }

    private String formatTime(int ms) {
        int ss = 1000;
        int mi = ss * 60;
        int hh = mi * 60;

        long hour = ms / hh;
        long minute = (ms- hour * hh) / mi;
        long second = (ms- hour * hh - minute * mi) / ss;

        String strHour = hour < 10 ? "0" + hour : "" + hour;
        String strMinute = minute < 10 ? "0" + minute : "" + minute;
        String strSecond = second < 10 ? "0" + second : "" + second;
        if (strHour.equals("00")) {
            return strMinute + ":" + strSecond;
        }
        return  strHour + ":" + strMinute + ":" + strSecond;
    }

    public void playerVideo(Context context,VideoInfo videoInfo) {
        Intent intent = new Intent(context, DetailPlayerActivity.class);
        intent.putExtra(VideoConstants.VIDEO_INFO, videoInfo);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
    public void playerVideo(Context context,VideoInfo videoInfo,boolean cash) {
        Intent intent = new Intent(context, DetailPlayerActivity.class);
        intent.putExtra(VideoConstants.VIDEO_INFO, videoInfo);
        intent.putExtra(VideoConstants.VIDEO_CASH, cash);
        context.startActivity(intent);
    }
    public void showImage(Context context,String path) {
        Intent intent = new Intent(context, ImageShowActivity.class);
        intent.putExtra(VideoConstants.VIDEO_PATH,path);
        context.startActivity(intent);
    }
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }

                    // TODO handle non-primary volumes
                }
                // DownloadsProvider
                else if (isDownloadsDocument(uri)) {

                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    return getDataColumn(context, contentUri, null, null);
                }
                // MediaProvider
                else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[] {
                            split[1]
                    };

                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            }
            // MediaStore (and general)
            else if ("content".equalsIgnoreCase(uri.getScheme())) {
                return getDataColumn(context, uri, null, null);
            }
            // File
            else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
        }

        return null;
    }
    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
