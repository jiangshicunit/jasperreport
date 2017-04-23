package com.haomostudio.jrs;

/**
 * Created by Caeson on 2017/4/23.
 */
public class MainReportsG extends ReportG {

    private String customerID;

    private String customerBirthDate;

    public void setCustomerID(String customerID){
        this.customerID = customerID;
    }

    public String getCustomerID(){
        return this.customerID;
    }

    public void setCustomerBirthDate(String customerBirthDate){
        this.customerBirthDate = customerBirthDate;
    }

    public String getCustomerBirthDate(){
        return this.customerBirthDate;
    }
}
