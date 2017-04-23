package com.haomostudio.jrs;

import java.util.List;

/**
 * Created by Caeson on 2017/4/23.
 */
public class AllergyAnalysisTableReportG extends ReportG {

    private String title;

    private String date;

    private List<AllergyAnalysisItemG> allergyAnalysisItemGList;

    public void setTitle(String title){
        this.title = title;
    }

    public String getTitle(){
        return this.title;
    }

    public void setDate(String date){
        this.date = date;
    }

    public String getDate(){
        return this.date;
    }

    public void setAllergyAnalysisItemGList(List<AllergyAnalysisItemG> itemList){
        this.allergyAnalysisItemGList = itemList;
    }

    public List<AllergyAnalysisItemG> getAllergyAnalysisItemGList(){
        return this.allergyAnalysisItemGList;
    }

}
