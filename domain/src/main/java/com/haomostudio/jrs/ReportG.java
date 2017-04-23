package com.haomostudio.jrs;

import java.util.List;

/**
 * Created by Caeson on 2017/4/23.
 */
public class ReportG {

    private String reportName;

    List subReportList;

    public void setReportName(String reportName){
        this.reportName = reportName;
    }

    public String getReportName(){
        return this.reportName;
    }

    public void setSubReportList(List subReportList){
        this.subReportList = subReportList;
    }

    public List getSubReportList(){
        return this.subReportList;
    }
}
