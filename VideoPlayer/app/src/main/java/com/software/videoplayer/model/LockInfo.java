package com.software.videoplayer.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * User: Moon
 * Data: 2017/3/24.
 */

public class LockInfo implements Parcelable {

    private String name;
    private String oldPath;
    private String newPath;

    public LockInfo() {
        super();
    }

    public LockInfo(String name,String oldPath,String newPath) {
        this.name = name;
        this.oldPath = oldPath;
        this.newPath = newPath;
    }

    protected LockInfo(Parcel in) {
        name = in.readString();
        oldPath = in.readString();
        newPath = in.readString();
    }

    public static final Creator<LockInfo> CREATOR = new Creator<LockInfo>() {
        @Override
        public LockInfo createFromParcel(Parcel in) {
            return new LockInfo(in);
        }

        @Override
        public LockInfo[] newArray(int size) {
            return new LockInfo[size];
        }
    };

    public String getName() {
        return name;
    }

    public String getOldPath() {
        return oldPath;
    }

    public String getNewPath() {
        return newPath;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNewPath(String newPath) {
        this.newPath = newPath;
    }
    public void setOldPath(String oldPath) {
        this.oldPath = oldPath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(oldPath);
        dest.writeString(newPath);
    }
}
