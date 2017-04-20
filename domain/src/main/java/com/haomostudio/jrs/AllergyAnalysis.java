package com.haomostudio.jrs;

/**
 * Created by shidaizhoukan on 2017/4/19.
 *
 * 	过敏分析表格模板
 * 	模板名称 : AllergyAnalysis
 */
public class AllergyAnalysis {

    private String title;


    public AllergyAnalysis(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
