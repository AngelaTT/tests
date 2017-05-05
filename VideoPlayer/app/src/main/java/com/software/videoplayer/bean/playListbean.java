package com.software.videoplayer.bean;

/**
 * Created by txh on 2017/4/12.
 */

public class playListbean {

    String name;
    String info;
    int album;

    public playListbean(String name, String info, int album) {
        this.name = name;
        this.info = info;
        this.album = album;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getAlbum() {
        return album;
    }

    public void setAlbum(int album) {
        this.album = album;
    }

    @Override
    public String toString() {
        return "playListbean{" +
                "name='" + name + '\'' +
                ", info='" + info + '\'' +
                ", album=" + album +
                '}';
    }
}
