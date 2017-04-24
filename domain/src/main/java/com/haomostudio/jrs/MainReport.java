package com.haomostudio.jrs;

import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * Created by shidaizhoukan on 2017/4/20.
 */
public class MainReport {

    private String customerID;
    private String customerBirthDate;
    private String reportName;


    public MainReport() {
        // TODO Auto-generated constructor stub
    }

    public MainReport(String customerID, String customerBirthDate,String reportName) {
        this.customerID = customerID;
        this.customerBirthDate = customerBirthDate;
        this.reportName = reportName;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getCustomerBirthDate() {
        return customerBirthDate;
    }

    public void setCustomerBirthDate(String customerBirthDate) {
        this.customerBirthDate = customerBirthDate;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }
}
