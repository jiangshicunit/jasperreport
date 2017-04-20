package com.haomostudio.jrs;

/**
 * Created by shidaizhoukan on 2017/4/19.
 */
public class AllergyAnalysisTable {

    private String items;

    private String values;

    public AllergyAnalysisTable(String items, String values) {
        this.items = items;
        this.values = values;
    }

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }

    public String getValues() {
        return values;
    }

    public void setValues(String values) {
        this.values = values;
    }
}
