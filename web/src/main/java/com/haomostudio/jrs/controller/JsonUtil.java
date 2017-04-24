package com.haomostudio.jrs.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.haomostudio.jrs.MainReport;
import com.haomostudio.jrs.MainReports;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by shidaizhoukan on 2017/4/22.
 */
public class JsonUtil {
    public static String json = "{" +
            "\"reportName\" : \"MainReport\"," +
            "\"data\" : {" +
            "\"customerID\" : \"123333\"," +
            "\"customerBirthDate\": \"1979-02-09\"" +
            "}," +
            "\"subReports\" : [" +
            "{" +
            "\"subReportName\":\"SectionHeader\"," +
            "\"data\": {" +
            "\"title\": \"禁忌\"" +
            "}" +
            "}," +
            "{" +
            "\"subReportName\":\"AllergyAnalysis\"," +
            "\"data\": {" +
            "\"title\": \"慢性食物过敏分析\"" +
            "}," +
            "\"subReports\" : [" +
            "{" +
            "\"subReportName\": \"AllergyAnalysisTable\"," +
            "\"data\": {" +
            "\"title\": \"奶蛋类\"," +
            "\"date\": \"15/2/18\"," +
            "\"items\": [\"牛奶\",\"羊奶\", \"切达奶酪\",\"酸奶\",\"蛋白\", \"蛋黄\"]," +
            "\"values\": [0, 0, 0, 1, 0, 0]" +
            "}" +
            "}," +
            "{" +
            "\"subReportName\": \"AllergyAnalysisTable\"," +
            "\"data\": {" +
            "\"title\": \"其他类\"," +
            "\"date\": \"15/2/18\"," +
            "\"items\": [\"辣椒\",\"酵母\", \"胡椒\",\"咖啡\",\"茶\", \"香草\", \"蜂蜜\", \"生姜\"]," +
            "\"values\": [0, 0, 0, 0, 0, 1, 2, 3]" +
            "}" +
            "}," +
            "{" +
            "\"subReportName\": \"AllergyAnalysisTable\"," +
            "\"data\": {" +
            "\"title\": \"蔬菜类\"," +
            "\"date\": \"15/2/18\"," +
            "\"items\": [\"芦笋\",\"蘑菇\", \"番茄\",\"芋头\",\"四季豆\", \"菠菜\"]," +
            "\"values\": [0, 0, 0, 2, 0, 1]" +
            "}" +
            "}," +
            "{" +
            "\"subReportName\": \"AllergyAnalysisTable\"," +
            "\"data\": {" +
            "\"title\": \"蔬菜类\"," +
            "\"date\": \"15/2/18\"," +
            "\"items\": [\"芦笋\",\"蘑菇\", \"番茄\",\"芋头\",\"四季豆\", \"菠菜\"]," +
            "\"values\": [0, 0, 0, 2, 0, 1]" +
            "}" +
            "}" +
            "]" +
            "}" +
            "]" +
            "}";
    
    public static void main(String[] args){
        //解析Mainreport
        Object subReports = JsonUtil.mainReport(JsonUtil.json,new MainReport());
        //解析子report
        //subReport(subReports);
        if (!StringUtils.isEmpty( subReports )){
            JSONArray array = JSON.parseArray(subReports.toString());
            for (Object subObject : array) {
                Map<String,Object> map = (Map<String, Object>) subObject;
                System.out.println("map:"+map.get("subReportName"));
                System.out.println("data"+((Map<String,Object>)map.get("data")).get("title"));
                if (!StringUtils.isEmpty(map.get("subReports"))){
//                    System.out.println(map.get("subReports").toString());
                    List<Map<String,Object>> slist = (List<Map<String, Object>>) map.get("subReports");
                    for (Map<String,Object> smap :slist){
                        System.out.println("subReportName:"+smap.get("subReportName"));
                        System.out.println("data"+((Map<String,Object>)smap.get("data")).get("title"));
                        List<String> items = (List<String>) ((Map<String,Object>)smap.get("data")).get("items");
                        List<Integer> values = (List<Integer>) ((Map<String,Object>)smap.get("data")).get("values");
                        for (int i = 0;i < items.size();i++){
                            System.out.println("items:"+items.get(i)+"-"+"values"+values.get(i));
                        }
                    }
                }
            }
        }
    }

    public static Object  mainReport(String json,MainReport mainReport){
        JSONObject object    = JSONObject.parseObject(json);
        Object reportName    = object.get("reportName");
        Object data          = object.get("data");
        JSONObject dataObject    = JSONObject.parseObject(data.toString());
        Object customerID = dataObject.get("customerID");
        Object customerBirthDate = dataObject.get("customerBirthDate");
        Object subReports    = object.get("subReports");
        System.out.println(reportName+":"+customerID+"&"+ customerBirthDate);
        if (!StringUtils.isEmpty( reportName) ){
            mainReport.setReportName(reportName.toString()) ;
        }
        if (!StringUtils.isEmpty( customerID) ){
            mainReport.setCustomerID(customerID.toString());
        }
        if (!StringUtils.isEmpty( customerBirthDate) ){
            mainReport.setCustomerBirthDate(customerBirthDate.toString());
        }
        return subReports;
    }

    public static Object  subReport(String fatReportName,Object subReports){
        JSONArray reportArray =  JSON.parseArray(subReports.toString());
        for (Object object : reportArray){
            JSONObject  subReport = (JSONObject) object;
            Object subReportName = subReport.get("subReportName");
            System.out.print(subReportName+"");
            Object data          = subReport.get("data");
            JsonUtil.subReportData(subReportName.toString(),data.toString(),new String[]{"title"});
            subReports          = subReport.get("subReports");
            if (!StringUtils.isEmpty(subReports)){
                //subReport(subReports.toString());
            }

        }

//        return subReports;
        return null;
    }

    //MainReport

    public static void subReportData(String subReportName,String data,String[] names){
        JSONObject object    = JSONObject.parseObject(data);
        if (!StringUtils.isEmpty(subReportName)){
            if (subReportName.equals("AllergyAnalysisTable")){
                Object title =  object.get("title");
                Object date =  object.get("title");
                Object items =  object.get("title");
                Object values =  object.get("title");
                System.out.println(title+" & "+date +" & "+ items + " & "+ values);
            }else {
                for (String name : names){
                    Object subReportNames = object.get(name);
                    System.out.println(subReportNames.toString()+"-");
                }
            }


        }

//        return subReports;"title": "奶蛋类",
//        "date": "15/2/18",
//                "items": ["牛奶","羊奶", "切达奶酪","酸奶","蛋白", "蛋黄"],
//        "values": [0, 0, 0, 1, 0, 0]
    }




}
