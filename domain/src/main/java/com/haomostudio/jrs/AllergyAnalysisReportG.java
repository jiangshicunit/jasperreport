package com.haomostudio.jrs;

import java.util.List;

/**
 * Created by Caeson on 2017/4/23.
 */
public class AllergyAnalysisReportG extends ReportG {

    private String title;

    public AllergyAnalysisReportG(List<AllergyAnalysisTableReportG> subreportList){
        this.subReportList = subreportList;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getTitle(){
        return this.title;
    }

}
