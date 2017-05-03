package com.haomostudio.jrs;

/**
 * Created by shidaizhoukan on 2017/4/19.
 */
public class LaboratoryTestTable {

    private String title;
    private String date;
    private String name;
    private String ename;
    private double min;
    private double max;
    private double value;
    private String unit;

    public LaboratoryTestTable(String title, String date, String name, String ename, double max, double min,double value, String unit) {
        this.title = title;
        this.date = date;
        this.name = name;
        this.ename = ename;
        this.max = max;
        this.value = value;
        this.unit = unit;
        this.min = min ;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEname() {
        return ename;
    }

    public void setEname(String ename) {
        this.ename = ename;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }
}
