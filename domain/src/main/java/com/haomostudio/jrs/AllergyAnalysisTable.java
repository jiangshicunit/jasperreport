package com.haomostudio.jrs;

import java.util.List;

/**
 * Created by shidaizhoukan on 2017/4/19.
 */
public class AllergyAnalysisTable {

    private String title;

    private String date;

    private List<String> items;

    private List<String> values;

    public AllergyAnalysisTable(String title, String date, List<String> items, List<String> values) {
        this.title = title;
        this.date = date;
        this.items = items;
        this.values = values;
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

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }
}
