package com.haomostudio.jrs;

/**
 * Created by shidaizhoukan on 2017/4/19.
 */
public class SectionHeader {

    private String title;
    private String subTitle;

    public SectionHeader(String title, String subTitle) {
        this.title = title;
        this.subTitle = subTitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }
}
