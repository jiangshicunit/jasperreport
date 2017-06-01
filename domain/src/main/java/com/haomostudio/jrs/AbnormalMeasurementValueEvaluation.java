package com.haomostudio.jrs;

import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * Created by guanpb on 2017/6/1.
 */
public class AbnormalMeasurementValueEvaluation {

    private JRBeanCollectionDataSource dataBase;
    private JasperReport subreport;

    public AbnormalMeasurementValueEvaluation(JRBeanCollectionDataSource dataBase, JasperReport subreport) {
        this.dataBase = dataBase;
        this.subreport = subreport;
    }

    public JRBeanCollectionDataSource getDataBase() {
        return dataBase;
    }

    public void setDataBase(JRBeanCollectionDataSource dataBase) {
        this.dataBase = dataBase;
    }

    public JasperReport getSubreport() {
        return subreport;
    }

    public void setSubreport(JasperReport subreport) {
        this.subreport = subreport;
    }
}
