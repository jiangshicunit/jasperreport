package com.haomostudio.jrs;

/**
 * Created by guanpb on 2017/6/2.
 */
public class FoodSourceExplanation {

    private String mainTitle;

    private String title;

    private String content;

    public FoodSourceExplanation(String mainTitle, String title, String content) {
        this.mainTitle = mainTitle;
        this.title = title;
        this.content = content;
    }

    public String getMainTitle() {
        return mainTitle;
    }

    public void setMainTitle(String mainTitle) {
        this.mainTitle = mainTitle;
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
