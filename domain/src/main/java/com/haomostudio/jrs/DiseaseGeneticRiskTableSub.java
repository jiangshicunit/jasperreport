package com.haomostudio.jrs;

/**
 * Created by guanpb on 2017/5/16.
 */
public class DiseaseGeneticRiskTableSub {

    private String name;
    private String risk;
    private String title;
    private String riskTitle;

    public DiseaseGeneticRiskTableSub(String name, String risk) {
        this.name = name;
        this.risk = risk;
    }

    public DiseaseGeneticRiskTableSub(String name, String risk, String title, String riskTitle) {
        this.name = name;
        this.risk = risk;
        this.title = title;
        this.riskTitle = riskTitle;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRisk() {
        return risk;
    }

    public void setRisk(String risk) {
        this.risk = risk;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRiskTitle() {
        return riskTitle;
    }

    public void setRiskTitle(String riskTitle) {
        this.riskTitle = riskTitle;
    }
}
