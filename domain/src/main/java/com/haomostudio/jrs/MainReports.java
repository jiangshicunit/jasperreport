package com.haomostudio.jrs;

import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * Created by shidaizhoukan on 2017/4/20.
 */
public class MainReports {

    private JasperReport jasper;
    private JasperReport subJasper;
    private JRBeanCollectionDataSource cityDs;

    public MainReports() {
        // TODO Auto-generated constructor stub
    }

    public MainReports(JasperReport jasper, JasperReport subJasper, JRBeanCollectionDataSource cityDs) {
        this.jasper = jasper;
        this.subJasper = subJasper;
        this.cityDs = cityDs;
    }

    public JasperReport getJasper() {
        return jasper;
    }

    public void setJasper(JasperReport jasper) {
        this.jasper = jasper;
    }

    public JasperReport getSubJasper() {
        return subJasper;
    }

    public void setSubJasper(JasperReport subJasper) {
        this.subJasper = subJasper;
    }

    public JRBeanCollectionDataSource getCityDs() {
        return cityDs;
    }

    public void setCityDs(JRBeanCollectionDataSource cityDs) {
        this.cityDs = cityDs;
    }
}
