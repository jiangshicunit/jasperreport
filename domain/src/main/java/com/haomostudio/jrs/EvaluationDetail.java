package com.haomostudio.jrs;

/**
 * Created by shidaizhoukan on 2017/4/26.
 */
public class EvaluationDetail {

    private String maintitle;

    private String title;

    private String content;

    public EvaluationDetail(String maintitle, String title, String content) {
        this.maintitle = maintitle;
        this.title = title;
        this.content = content;
    }

    public String getMaintitle() {
        return maintitle;
    }

    public void setMaintitle(String maintitle) {
        this.maintitle = maintitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
