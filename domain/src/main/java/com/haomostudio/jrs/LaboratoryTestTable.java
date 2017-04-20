package com.haomostudio.jrs;

/**
 * Created by shidaizhoukan on 2017/4/19.
 */
public class LaboratoryTestTable {

    private String name;
    private String ChineseName;
    private String testValue;
    private String MaxReferValue;
    private String MinReferValue;
    private String unit;

    public LaboratoryTestTable(String name, String chineseName, String testValue, String maxReferValue,String minReferValue, String unit) {
        this.name = name;
        this.ChineseName = chineseName;
        this.testValue = testValue;
        this.MaxReferValue = maxReferValue;
        this.unit = unit;
        this.MinReferValue = minReferValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChineseName() {
        return ChineseName;
    }

    public void setChineseName(String chineseName) {
        ChineseName = chineseName;
    }

    public String getTestValue() {
        return testValue;
    }

    public void setTestValue(String testValue) {
        this.testValue = testValue;
    }

    public String getMaxReferValue() {
        return MaxReferValue;
    }

    public void setMaxReferValue(String maxReferValue) {
        MaxReferValue = maxReferValue;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getMinReferValue() {
        return MinReferValue;
    }

    public void setMinReferValue(String minReferValue) {
        MinReferValue = minReferValue;
    }
}
