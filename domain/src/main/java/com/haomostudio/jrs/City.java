package com.haomostudio.jrs;

/**
 * Created by shidaizhoukan on 2017/4/20.
 */

public class City {

    private int id;
    private String cityName;

    public City() {
        // TODO Auto-generated constructor stub
    }

    public City(int id, String cityName) {
        super();
        this.id = id;
        this.cityName = cityName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
}
