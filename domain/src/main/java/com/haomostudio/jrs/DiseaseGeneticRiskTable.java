package com.haomostudio.jrs;

/**
 * Created by shidaizhoukan on 2017/4/19.
 */
public class DiseaseGeneticRiskTable {

    private String name;

    private String fx;

    private String item;

    private Integer value;

    public DiseaseGeneticRiskTable(String name, String fx, String item, Integer value) {
        this.name = name;
        this.fx = fx;
        this.item = item;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFx() {
        return fx;
    }

    public void setFx(String fx) {
        this.fx = fx;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
