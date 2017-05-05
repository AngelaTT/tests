package com.software.videoplayer.model;

/**
 * User: Moon
 * Data: 2017/3/16.
 */

public class CloudInfo {

    private String name;
    private String path;

    public CloudInfo() {
        super();
    }

    public CloudInfo(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }
}
