package com.haomostudio.jrs;

import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * Created by shidaizhoukan on 2017/4/19.
 */
public class DiseaseGeneticRiskTable {

    private JRBeanCollectionDataSource ds;

    private JasperReport subReport;

    public DiseaseGeneticRiskTable(JRBeanCollectionDataSource ds, JasperReport subReport) {
        this.ds = ds;
        this.subReport = subReport;
    }

    public JRBeanCollectionDataSource getDs() {
        return ds;
    }

    public void setDs(JRBeanCollectionDataSource ds) {
        this.ds = ds;
    }

    public JasperReport getSubReport() {
        return subReport;
    }

    public void setSubReport(JasperReport subReport) {
        this.subReport = subReport;
    }
}
