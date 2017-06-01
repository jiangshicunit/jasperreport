package com.haomostudio.jrs;

/**
 * Created by guanpb on 2017/6/1.
 */
public class AbnormalMeasurementValueTable {

    private String title;

    private String measurementTitle;

    private String value;

    private String standardMinValue;

    private String standardMaxValue;

    private String pathologicalChanges;

    private String unit;

    private String isTop;

    private String isBottom;

    public AbnormalMeasurementValueTable(String title, String measurementTitle, String value, String standardMinValue,
                                         String standardMaxValue, String pathologicalChanges, String unit, String isTop, String isBottom) {
        this.title = title;
        this.measurementTitle = measurementTitle;
        this.value = value;
        this.standardMinValue = standardMinValue;
        this.standardMaxValue = standardMaxValue;
        this.pathologicalChanges = pathologicalChanges;
        this.unit = unit;
        this.isTop = isTop;
        this.isBottom = isBottom;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMeasurementTitle() {
        return measurementTitle;
    }

    public void setMeasurementTitle(String measurementTitle) {
        this.measurementTitle = measurementTitle;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getStandardMinValue() {
        return standardMinValue;
    }

    public void setStandardMinValue(String standardMinValue) {
        this.standardMinValue = standardMinValue;
    }

    public String getStandardMaxValue() {
        return standardMaxValue;
    }

    public void setStandardMaxValue(String standardMaxValue) {
        this.standardMaxValue = standardMaxValue;
    }

    public String getPathologicalChanges() {
        return pathologicalChanges;
    }

    public void setPathologicalChanges(String pathologicalChanges) {
        this.pathologicalChanges = pathologicalChanges;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getIsTop() {
        return isTop;
    }

    public void setIsTop(String isTop) {
        this.isTop = isTop;
    }

    public String getIsBottom() {
        return isBottom;
    }

    public void setIsBottom(String isBottom) {
        this.isBottom = isBottom;
    }
}
