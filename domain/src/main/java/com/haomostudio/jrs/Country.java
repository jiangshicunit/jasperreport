package com.haomostudio.jrs;

import java.util.List;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class Country {

    private int id;
    private String countryName;
    private JRBeanCollectionDataSource cityDs;

    public Country() {
        // TODO Auto-generated constructor stub
    }

    public Country(int id, String countryName, JRBeanCollectionDataSource cityDs) {
        super();
        this.id = id;
        this.countryName = countryName;
        this.cityDs = cityDs;
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

}
