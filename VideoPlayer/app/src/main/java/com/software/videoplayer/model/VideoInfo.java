package com.software.videoplayer.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *  User: Moon
 *  Data: 2017/2/15.
 */
public class VideoInfo implements Parcelable{

    private String name;
    private String bucket;
    private String path;
    private String time;
    private String size;
    private boolean aNew;

    public VideoInfo() {
        super();
    }
    public VideoInfo(String name,String bucket, String path, String time, String size) {
        this.name = name;
        this.bucket = bucket;
        this.path = path;
        this.time = time;
        this.size = size;
    }

    protected VideoInfo(Parcel in) {
        name = in.readString();
        bucket = in.readString();
        path = in.readString();
        time = in.readString();
        size = in.readString();
        aNew = in.readByte() != 0;
    }

    public static final Creator<VideoInfo> CREATOR = new Creator<VideoInfo>() {
        @Override
        public VideoInfo createFromParcel(Parcel in) {
            return new VideoInfo(in);
        }

        @Override
        public VideoInfo[] newArray(int size) {
            return new VideoInfo[size];
        }
    };

    public void setName(String name) {
        this.name = name;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setNew(boolean aNew) {
        this.aNew = aNew;
    }

    public String getName() {
        return name;
    }

    public String getBucket() {
        return bucket;
    }

    public String getPath() {
        return path;
    }

    public String getTime() {
        return time;
    }

    public String getSize() {
        return size;
    }

    public boolean getNew() {
        return aNew;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(bucket);
        dest.writeString(path);
        dest.writeString(time);
        dest.writeString(size);
        dest.writeByte((byte) (aNew ? 1 : 0));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof VideoInfo) {
            VideoInfo u = (VideoInfo) obj;
            return this.path.equals(u.path);
        }
        return super.equals(obj);
    }
}

