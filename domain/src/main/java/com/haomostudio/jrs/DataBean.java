package com.haomostudio.jrs;

/**
 * Created by shidaizhoukan on 2017/4/14.
 */
public class DataBean {
    private String name;
    private String country;
    private String title;
    private String date;


    public DataBean(String name, String country,String title,String date) {
        this.name = name;
        this.country = country;
        this.title = title;
        this.date = date;
    }
    public DataBean() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
