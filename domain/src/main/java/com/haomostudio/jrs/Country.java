package com.haomostudio.jrs;

import java.util.Map;

import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class Country {

    private int id;
    private String countryName;
    private JRBeanCollectionDataSource cityDs;
    private JRBeanCollectionDataSource cityDs1;
    private JasperReport subReportObject1;
    private JasperReport subReportObject2;

    public Country() {
        // TODO Auto-generated constructor stub
    }

    public Country(int id, String countryName, JRBeanCollectionDataSource cityDs, JRBeanCollectionDataSource cityDs1, JasperReport subReportObject1, JasperReport subReportObject2) {
        this.id = id;
        this.countryName = countryName;
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

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
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
