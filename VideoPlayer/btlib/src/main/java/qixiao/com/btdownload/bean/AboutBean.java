package qixiao.com.btdownload.bean;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2017/3/21.
 */

public class AboutBean {

    Bitmap icon;
    String title;
    String describe;

    public AboutBean() {
    }

    public AboutBean(Bitmap icon, String title, String describe) {
        this.icon = icon;
        this.title = title;
        this.describe = describe;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    @Override
    public String toString() {
        return "AboutBean{" +
                "icon=" + icon +
                ", title='" + title + '\'' +
                ", describe='" + describe + '\'' +
                '}';
    }
}
