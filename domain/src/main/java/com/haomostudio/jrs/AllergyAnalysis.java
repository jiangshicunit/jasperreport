package com.haomostudio.jrs;

import java.util.Map;

import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class AllergyAnalysis {

    private int id;
    private String name;
    private JRBeanCollectionDataSource cityDs;
    private JRBeanCollectionDataSource cityDs1;
    private JasperReport subReportObject1;
    private JasperReport subReportObject2;

    public AllergyAnalysis() {
        // TODO Auto-generated constructor stub
    }

    public AllergyAnalysis(int id, String name, JRBeanCollectionDataSource cityDs, JRBeanCollectionDataSource cityDs1, JasperReport subReportObject1, JasperReport subReportObject2) {
        this.id = id;
        this.name = name;
        this.cityDs = cityDs;
        this.cityDs1 = cityDs1;
        this.subReportObject1 = subReportObject1;
        this.subReportObject2 = subReportObject2;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public JRBeanCollectionDataSource getCityDs() {
        return cityDs;
    }

    public void setCityDs(JRBeanCollectionDataSource cityDs) {
        this.cityDs = cityDs;
    }

    public JRBeanCollectionDataSource getCityDs1() {
        return cityDs1;
    }

    public void setCityDs1(JRBeanCollectionDataSource cityDs1) {
        this.cityDs1 = cityDs1;
    }

    public JasperReport getSubReportObject1() {
        return subReportObject1;
    }

    public void setSubReportObject1(JasperReport subReportObject1) {
        this.subReportObject1 = subReportObject1;
    }

    public JasperReport getSubReportObject2() {
        return subReportObject2;
    }

    public void setSubReportObject2(JasperReport subReportObject2) {
        this.subReportObject2 = subReportObject2;
    }
}
