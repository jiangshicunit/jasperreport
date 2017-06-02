package com.haomostudio.jrs;

import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * Created by guanpb on 2017/5/16.
 */
public class PredefinedContentConvergence {

    private String title;
    private JRBeanCollectionDataSource ds;
    private JasperReport subReport;

    public PredefinedContentConvergence(String title, JRBeanCollectionDataSource ds, JasperReport subReport) {
        this.title = title;
        this.ds = ds;
        this.subReport = subReport;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
