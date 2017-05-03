package com.haomostudio.jrs;

import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * Created by shidaizhoukan on 2017/5/4.
 */
public class LaboratoryTest {

    private JRBeanCollectionDataSource data;
    private JasperReport subReport;

    public LaboratoryTest(JRBeanCollectionDataSource data, JasperReport subReport) {
        this.data = data;
        this.subReport = subReport;
    }

    public JRBeanCollectionDataSource getData() {
        return data;
    }

    public void setData(JRBeanCollectionDataSource data) {
        this.data = data;
    }

    public JasperReport getSubReport() {
        return subReport;
    }

    public void setSubReport(JasperReport subReport) {
        this.subReport = subReport;
    }
}
