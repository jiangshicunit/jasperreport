package com.haomostudio.jrs;

import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * Created by shidaizhoukan on 2017/5/3.
 */
public class County1 {
    private JRBeanCollectionDataSource cityDs;
    private JasperReport subReportObject;

    public County1(JRBeanCollectionDataSource cityDs, JasperReport subReportObject) {
        this.cityDs = cityDs;
        this.subReportObject = subReportObject;
    }

    public JRBeanCollectionDataSource getCityDs() {
        return cityDs;
    }

    public void setCityDs(JRBeanCollectionDataSource cityDs) {
        this.cityDs = cityDs;
    }

    public JasperReport getSubReportObject() {
        return subReportObject;
    }

    public void setSubReportObject(JasperReport subReportObject) {
        this.subReportObject = subReportObject;
    }
}
