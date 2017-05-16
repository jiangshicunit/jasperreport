package com.haomostudio.jrs;

/**
 * Created by guanpb on 2017/5/16.
 */
public class IndicatorRiskLevel {

    private String name ;
    private String ename;
    private String level;

    public IndicatorRiskLevel(String name, String ename, String level) {
        this.name = name;
        this.ename = ename;
        this.level = level;
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

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
