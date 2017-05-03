package com.haomostudio.jrs.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.ObjectArraySerializer;
import com.haomostudio.jrs.*;
import com.haomostudio.jrs.common.PropertyConfigurer;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.LocalJasperReportsContext;
import net.sf.jasperreports.engine.util.SimpleFileResolver;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.export.ExporterInput;
import net.sf.jasperreports.export.OutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

@Controller
public class SaperReportController {

    @Autowired
    PropertyConfigurer propertyConfigurer;

    /**
     * 测试用例，根据传入的参数生成PDF
     * @param response
     * @throws JRException
     * @throws IOException
     */
    @RequestMapping(value = "/testPdf",
            method = { RequestMethod.GET, RequestMethod.POST },
            produces = "application/json;charset=UTF-8")
    @ResponseBody
    public void printPdf(HttpServletResponse response,HttpServletRequest request) throws JRException, IOException {
        String  realPath = request.getSession().getServletContext().getRealPath("");
        //project的web module所在路径

        //获取jrxml并编译/Users/liuranran/work/test1.jrxml
//        InputStream inputStream = new FileInputStream(filePath + "/src/main/resources/jrxml/MyReports/MainReports.jrxml");
//        InputStream inputStream = new FileInputStream(filePath + "/webapps/jrs/WEB-INF/classes/jrxml/MyReports/test.jrxml");

        InputStream inputStream = new FileInputStream(realPath+"/jrxml/MyReports/LaboratotyTestTable.jrxml");
        JasperDesign jasperDesign = JRXmlLoader.load(inputStream);
        JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);

        //设置参数
        Map<String,Object> params = new HashMap<>();
        params.put("description","111111");
        params.put("title","1sssss");
        File reportsDir = new File(realPath + "/jrxml/MyReports/");
        params.put(JRParameter.REPORT_FILE_RESOLVER, new SimpleFileResolver(reportsDir)); //deprecated
        LocalJasperReportsContext ctx = new LocalJasperReportsContext(DefaultJasperReportsContext.getInstance());
        ctx.setClassLoader(getClass().getClassLoader());
        ctx.setFileResolver(new SimpleFileResolver(reportsDir));//注意，设置JasperReport的相对路径，很重要
        JRDataSource jrDataSource = new JRBeanCollectionDataSource(null);
        JasperFillManager fillManager = JasperFillManager.getInstance(ctx);
        JasperExportManager exportManager = JasperExportManager.getInstance(ctx);

        //
        List<List<DataBean>> dataList = getDataBeanList();
        List<Country> data = new ArrayList();
        JasperDesign jasperDesign1 = JRXmlLoader.load(new FileInputStream(realPath+"/jrxml/MyReports/AllergyAnalysisTable1.jrxml"));
        JasperReport jasperReport1 = JasperCompileManager.compileReport(jasperDesign1);
        JasperDesign jasperDesign2 = JRXmlLoader.load(new FileInputStream(realPath+"/jrxml/MyReports/AllergyAnalysisTable2.jrxml"));
        JasperReport jasperReport2 = JasperCompileManager.compileReport(jasperDesign2);
        JasperDesign jasperDesign3 = JRXmlLoader.load(new FileInputStream(realPath+"/jrxml/MyReports/AllergyAnalysisTable.jrxml"));
        JasperReport jasperReport3 = JasperCompileManager.compileReport(jasperDesign3);
        List<County1> county1List = new ArrayList<>();
        List<County1> county2List = new ArrayList<>();
        for (int i = 0;i<dataList.size();i=i+2){
            JRBeanCollectionDataSource ds1 = new JRBeanCollectionDataSource(dataList.get(i));
            JRBeanCollectionDataSource ds2 = null;
            if (i+1<dataList.size()){
                ds2 = new JRBeanCollectionDataSource(dataList.get(i+1));
            }
            county1List.add(new County1(ds1,jasperReport3));
            county2List.add(new County1(ds2,jasperReport3));
        }
        data.add(new Country(1,"过敏",new JRBeanCollectionDataSource(county1List),
                new JRBeanCollectionDataSource(county2List),jasperReport1,jasperReport2));
//        List<SuggestionReport> dataList = getDataBeanList();

        //获取JasperPrint
//        JasperPrint jasperPrint = fillManager.fill(jasperReport, params, new JREmptyDataSource());
        JasperPrint jasperPrint = fillManager.fillReport(jasperReport,params, new JRBeanCollectionDataSource(
                data));
        //返回输出流
        final OutputStream outStream = response.getOutputStream();
        String fileName = "test.pdf";
        response.addHeader("Content-Disposition", "filename=" + fileName);
        exportManager.exportToPdfStream(jasperPrint, outStream);
    }



    /**
     * 测试用例，生成简单的pdf
     * @param response
     * @throws JRException
     * @throws IOException
     */
    @RequestMapping(value = "/testToPage",
            method = { RequestMethod.GET, RequestMethod.POST },
            produces = "application/json;charset=UTF-8")
    @ResponseBody
    public void printPdfTopage(HttpServletResponse response) throws JRException, IOException {
        //project的web module所在路径
        String filePath = System.getProperty("search.root");
        filePath = filePath.substring(0, filePath.lastIndexOf("/"));
        filePath = filePath.substring(0, filePath.lastIndexOf("/"));
        filePath = filePath.substring(0, filePath.lastIndexOf("/"));

        //获取jrxml并编译
        InputStream inputStream = new FileInputStream("/Users/liuranran/work/LaboratotyTestTable2.jrxml");
        JasperDesign jasperDesign = JRXmlLoader.load(inputStream);
        JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);

        //设置参数
        Map<String,Object> params = new HashMap<>();
        File reportsDir = new File(filePath + "/src/main/resources/jrxml/MyReports");
        //        params.put(JRParameter.REPORT_FILE_RESOLVER, new SimpleFileResolver(reportsDir)); //deprecated
        LocalJasperReportsContext ctx = new LocalJasperReportsContext(DefaultJasperReportsContext.getInstance());
        ctx.setClassLoader(getClass().getClassLoader());
        ctx.setFileResolver(new SimpleFileResolver(reportsDir));//注意，设置JasperReport的相对路径，很重要
        JRDataSource jrDataSource = new JRBeanCollectionDataSource(null);
        JasperFillManager fillManager = JasperFillManager.getInstance(ctx);
        JasperExportManager exportManager = JasperExportManager.getInstance(ctx);

        List<Data> dataList = getDataBeanListTest();
        JRBeanCollectionDataSource beanColDataSource =
                new JRBeanCollectionDataSource(dataList);
        //获取JasperPrint
//        JasperPrint jasperPrint = fillManager.fill(jasperReport, params, new JREmptyDataSource());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,params, beanColDataSource);
        //返回输出流
        final OutputStream outStream = response.getOutputStream();
        String fileName = "test.pdf";
        response.addHeader("Content-Disposition", "filename=" + fileName);
        exportManager.exportToPdfStream(jasperPrint, outStream);
    }

    public List<Data> getDataBeanListTest() {
        ArrayList<Data> dataBeanList = new ArrayList();

        dataBeanList.add(new Data("test","test1", "test2","test3","test4","test5","test6"));
        dataBeanList.add(new Data("test","test1", "test2","test3","test4","test5","test6"));
        dataBeanList.add(new Data("test","test1", "test2","test3","test4","test5","test6"));
        dataBeanList.add(new Data("test","test1", "test2","test3","test4","test5","test6"));

        return dataBeanList;
    }


    @RequestMapping(value = "/testbh",
            method = { RequestMethod.GET, RequestMethod.POST },
            produces = "application/json;charset=UTF-8")
    @ResponseBody
    public void printPdf1(HttpServletResponse response) throws JRException, IOException {
        //project的web module所在路径
        String filePath = System.getProperty("search.root");
        filePath = filePath.substring(0, filePath.lastIndexOf("/"));
        filePath = filePath.substring(0, filePath.lastIndexOf("/"));
        filePath = filePath.substring(0, filePath.lastIndexOf("/"));

        InputStream inputStream = new FileInputStream("/Users/liuranran/work/jasperreportserver/web/src/main/resources/jrxml/MyReports/AllergyAnalysis.jrxml");
        JasperDesign jasperDesign = JRXmlLoader.load(inputStream);
        JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);

        //设置参数
        Map<String,Object> params = new HashMap<>();
        params.put("title1","标题1");
        params.put("title2","title2");
        File reportsDir = new File(filePath + "/webapps/jrs/WEB-INF/classes/jrxml/MyReports/");
        //        params.put(JRParameter.REPORT_FILE_RESOLVER, new SimpleFileResolver(reportsDir)); //deprecated
        LocalJasperReportsContext ctx = new LocalJasperReportsContext(DefaultJasperReportsContext.getInstance());
        ctx.setClassLoader(getClass().getClassLoader());
        ctx.setFileResolver(new SimpleFileResolver(reportsDir));//注意，设置JasperReport的相对路径，很重要
        JRDataSource jrDataSource = new JRBeanCollectionDataSource(null);
        JasperFillManager fillManager = JasperFillManager.getInstance(ctx);
        JasperExportManager exportManager = JasperExportManager.getInstance(ctx);

        //
//        List<DataBean> dataList = getDataBeanList();
        List<List<DataBean>> dataList = getDataBeanList();
        JRBeanCollectionDataSource beanColDataSource =
                new JRBeanCollectionDataSource(dataList);

        //获取JasperPrint
//        JasperPrint jasperPrint = fillManager.fill(jasperReport, params, new JREmptyDataSource());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,params, beanColDataSource);
        //返回输出流
        final OutputStream outStream = response.getOutputStream();
        String fileName = "test.pdf";
        response.addHeader("Content-Disposition", "filename=" + fileName);
        exportManager.exportToPdfStream(jasperPrint, outStream);
    }



    @RequestMapping(value = "/testgroup",
            method = { RequestMethod.GET, RequestMethod.POST },
            produces = "application/json;charset=UTF-8")
    @ResponseBody
    public void groupVoid1(HttpServletResponse response,HttpServletRequest request,
                           @RequestParam(value="json", required = false) String json1,
                           @RequestBody() String json) throws JRException, IOException  {

        String  realPath = request.getSession().getServletContext().getRealPath("");
        //project的web module所在路径
        String filePath = System.getProperty("search.root");
        filePath = filePath.substring(0, filePath.lastIndexOf("/"));
        filePath = filePath.substring(0, filePath.lastIndexOf("/"));
        filePath = filePath.substring(0, filePath.lastIndexOf("/"));

        //get *.jrxml
        JasperDesign jDesignMain = JRXmlLoader.load(new File(realPath+"/jrxml/MyReports/MainReports.jrxml"));
        JasperDesign jDesign = JRXmlLoader.load(new File(realPath+"/jrxml/MyReports/AllergyAnalysis.jrxml"));
        JasperDesign jDesign2 = JRXmlLoader.load(new File(realPath+"/jrxml/MyReports/AllergyAnalysisTable.jrxml"));
        JasperDesign jDesignSection = JRXmlLoader.load(new File(realPath+"/jrxml/MyReports/SectionHeader.jrxml"));

        //get *.jasper
        JasperReport jReportMain = JasperCompileManager.compileReport(jDesignMain);
        JasperReport jReport = JasperCompileManager.compileReport(jDesign);
        JasperReport jReport2 = JasperCompileManager.compileReport(jDesign2);
        JasperReport jReportSection = JasperCompileManager.compileReport(jDesignSection);


        File reportsDir = new File(realPath + "/jrxml/MyReports/");
              //deprecated
        LocalJasperReportsContext ctx = new LocalJasperReportsContext(DefaultJasperReportsContext.getInstance());
        ctx.setClassLoader(getClass().getClassLoader());
        ctx.setFileResolver(new SimpleFileResolver(reportsDir));//注意，设置JasperReport的相对路径，很重要
        JRDataSource jrDataSource = new JRBeanCollectionDataSource(null);
        JasperFillManager fillManager = JasperFillManager.getInstance(ctx);
        JasperExportManager exportManager = JasperExportManager.getInstance(ctx);

        //fill data
        List<MainReports> mainList = new ArrayList<>();
        List<Country> data = new ArrayList();

        List<List<DataBean>> list1 = getDataBeanList();
        for (List<DataBean> list2: list1) {
            JRBeanCollectionDataSource ds1 = new JRBeanCollectionDataSource(list2);
           // data.add(new Country(1,"过敏",ds1,ds1));
        }
//        List<DataBean> list2 = getDataBeanList1();
//        List<DataBean> list3 = getDataBeanList2();
////
//
//        JRBeanCollectionDataSource ds2 = new JRBeanCollectionDataSource(list2);
//        JRBeanCollectionDataSource ds3 = new JRBeanCollectionDataSource(list3);

//        JRBeanCollectionDataSource ds3 = new JRBeanCollectionDataSource(list3);
//
//        data.add(new Country(1 , "China" ,  ds1));
//        data.add(new Country(2 , "USA" , ds2));
//        data.add(new Country(3 , "UK" , ds3));
//        JRBeanCollectionDataSource dsMain = new JRBeanCollectionDataSource(data);
//        mainList.add(new MainReports(jReportSection, jReport2 ,new JRBeanCollectionDataSource(Arrays.asList(new SectionHeader("禁忌",""),new SectionHeader("血量分析","")))));
//        JasperDesign jDesignde = JRXmlLoader.load(new File(realPath+"/jrxml/MyReports/DiseaseGeneticRiskTable.jrxml"));
//
//        //get *.jasper
//        JasperReport jReportde = JasperCompileManager.compileReport(jDesignde);
//        mainList.add(new MainReports(jReportde,jReportde,new JRBeanCollectionDataSource(
//                Arrays.asList(
//                        new DiseaseGeneticRiskTable("糖尿病","糖尿病详情","口腔癌",1),
//                        new DiseaseGeneticRiskTable("糖尿病1","糖尿病详情3","白血病",0),
//                        new DiseaseGeneticRiskTable("糖尿病2","糖尿病详情4","老年痴呆",4),
//                        new DiseaseGeneticRiskTable("糖尿病3","糖尿病详情5","心脏病",5)
//                ))));

        JasperDesign jDesignde = JRXmlLoader.load(new File(realPath+"/jrxml/MyReports/EvaluationDetail.jrxml"));

//        //get *.jasper
        JasperReport jReportde = JasperCompileManager.compileReport(jDesignde);
                mainList.add(new MainReports(jReportde,jReportde,new JRBeanCollectionDataSource(
                Arrays.asList(new EvaluationDetail("脑下垂体荷尔蒙","123","3213333"),new EvaluationDetail("123","321","1234567")))));
        Map map = new HashMap();
        map.put("name","123333");
        map.put("birth","1979-02-09");
        map.put(JRParameter.REPORT_FILE_RESOLVER, new SimpleFileResolver(reportsDir));
//        JasperPrint jasperPrint = fillManager.fill(jReport, params, new JREmptyDataSource());
        JasperPrint jasperPrint = fillManager.fillReport(jReportMain, map, new JRBeanCollectionDataSource(mainList));

        //export with pdf
        //返回输出流
        final OutputStream outStream = response.getOutputStream();
        String fileName = "test.pdf";
        response.addHeader("Content-Disposition", "filename=" + fileName);
        exportManager.exportToPdfStream(jasperPrint, outStream);
    }


    public List<List<DataBean>> getDataBeanList() {
        List<List<DataBean>> lists = new ArrayList<>();

        ArrayList<DataBean> dataBeanList = new ArrayList();

        dataBeanList.add(new DataBean("牛奶", "0","奶蛋类","15/2/18"));
        dataBeanList.add(new DataBean("羊奶", "0","奶蛋类","15/2/18"));
        dataBeanList.add(new DataBean("切达奶酪", "0","奶蛋类","15/2/18"));
        dataBeanList.add(new DataBean("酸奶", "1","奶蛋类","15/2/18"));
        dataBeanList.add(new DataBean("蛋白", "0","奶蛋类","15/2/18"));
        dataBeanList.add(new DataBean("蛋黄", "1","奶蛋类","15/2/18"));
        lists.add(dataBeanList);

        dataBeanList = new ArrayList();
        dataBeanList.add(new DataBean("辣椒", "0","其他类","15/2/18"));
        dataBeanList.add(new DataBean("酵母", "0","其他类","15/2/18"));
        dataBeanList.add(new DataBean("胡椒", "0","其他类","15/2/18"));
        dataBeanList.add(new DataBean("咖啡", "0","其他类","15/2/18"));
        dataBeanList.add(new DataBean("茶", "0","其他类","15/2/18"));
        dataBeanList.add(new DataBean("香草", "1","其他类","15/2/18"));
        dataBeanList.add(new DataBean("蜂蜜", "2","其他类","15/2/18"));
        dataBeanList.add(new DataBean("生姜", "3","其他类","15/2/18"));
        lists.add(dataBeanList);

        dataBeanList = new ArrayList();
        dataBeanList.add(new DataBean("芦笋", "0","其他类","15/2/18"));
        dataBeanList.add(new DataBean("蘑菇", "0","其他类","15/2/18"));
        dataBeanList.add(new DataBean("番茄", "0","其他类","15/2/18"));
        dataBeanList.add(new DataBean("芋头", "2","其他类","15/2/18"));
        dataBeanList.add(new DataBean("四季豆", "0","其他类","15/2/18"));
        dataBeanList.add(new DataBean("菠菜", "1","其他类","15/2/18"));
        lists.add(dataBeanList);

        dataBeanList = new ArrayList();
        dataBeanList.add(new DataBean("芦笋", "0","其他类","15/2/18"));
        dataBeanList.add(new DataBean("蘑菇", "0","其他类","15/2/18"));
        dataBeanList.add(new DataBean("番茄", "0","其他类","15/2/18"));
        dataBeanList.add(new DataBean("芋头", "2","其他类","15/2/18"));
        dataBeanList.add(new DataBean("四季豆", "0","其他类","15/2/18"));
        dataBeanList.add(new DataBean("菠菜", "1","其他类","15/2/18"));
        lists.add(dataBeanList);
        return lists;
    }


    @RequestMapping(value = "/generatepdf",
            method = {  RequestMethod.POST },
            produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Object groupVoid5(HttpServletResponse response,HttpServletRequest request,
//                           @RequestBody String json,
                             @RequestParam(value = "json",required = false) String json,
                             @RequestParam(value = "pdfName",required = false) String pdfName
    ) throws JRException, IOException  {

//        String  realPath = request.getSession().getServletContext().getRealPath("");
        //XML文件保存的路径   以及  生成的pdf存放的路径
        String xml_save_path = propertyConfigurer.getProperty("xml_save_path");
        String pdf_export_path =  propertyConfigurer.getProperty("pdf_export_path");

        List<String> otherList = new ArrayList<>();
        Map<String,Object> resultMap =  new HashMap();
        if (StringUtils.isEmpty(pdfName)){
            pdfName = String.valueOf( System.currentTimeMillis() )+".pdf";
        }else if (!pdfName.contains(".pdf")){
            pdfName = pdfName+".pdf";
        }
        System.out.println("测试："+pdfName);
        FileOutputStream outs = null;
        File reportsDir = new File(xml_save_path);
        LocalJasperReportsContext ctx = new LocalJasperReportsContext(DefaultJasperReportsContext.getInstance());
        ctx.setClassLoader(getClass().getClassLoader());
        ctx.setFileResolver(new SimpleFileResolver(reportsDir));//注意，设置JasperReport的相对路径，很重要
        JRDataSource jrDataSource = new JRBeanCollectionDataSource(null);
        JasperFillManager fillManager = JasperFillManager.getInstance(ctx);
        JasperExportManager exportManager = JasperExportManager.getInstance(ctx);

        try {
            //MainReports 主表中的数据
            List<MainReports> mainList = new ArrayList<>();
            MainReport mainReport = new MainReport();
            Object subReports = JsonUtil.mainReport(json,mainReport);
            Map map = new HashMap();
            map.put("name",mainReport.getCustomerID()==null?"":mainReport.getCustomerID());
            map.put("birth",mainReport.getCustomerBirthDate()==null?"":mainReport.getCustomerBirthDate());
            map.put(JRParameter.REPORT_FILE_RESOLVER, new SimpleFileResolver(reportsDir));
            JasperDesign jDesignMain = JRXmlLoader.load(new File(xml_save_path+"/MainReports.jrxml"));
            JasperReport jReportMain = JasperCompileManager.compileReport(jDesignMain);
            //jasper数据
            JasperDesign jDesign = null;
            JasperReport jReport = null;
            //循环子列表
            if (!StringUtils.isEmpty( subReports )){
                JSONArray array = JSON.parseArray(subReports.toString());
                for (Object subObject : array) {
                    Map<String,Object> submap = (Map<String, Object>) subObject;
//                    System.out.println("map:"+submap.get("subReportName"));
//                    System.out.println("data"+((Map<String,Object>)submap.get("data")).get("title"));
                    //开始循环如果存在子模板
                    if (!StringUtils.isEmpty(submap.get("subReports"))){
//                    System.out.println(map.get("subReports").toString());
                        if (submap!=null && submap.get("subReportName").equals("AllergyAnalysis")){
                            List<Country> data = new ArrayList();
                            jDesign =  JRXmlLoader.load(new File(xml_save_path+"/AllergyAnalysis.jrxml"));
                            jReport = JasperCompileManager.compileReport(jDesign);

                            JasperDesign jDesign2 = JRXmlLoader.load(new File(xml_save_path+"/AllergyAnalysisTable.jrxml"));
                            JasperReport jReport2 = JasperCompileManager.compileReport(jDesign2);
                            List<Map<String,Object>> slist = (List<Map<String, Object>>) submap.get("subReports");
                            int num = 0 ;
                            List<List<DataBean>>  sunBeanList = new ArrayList<>();
                            for (Map<String,Object> smap :slist){
                                List<DataBean> dataBeanList = new ArrayList();
                                if(smap!=null && smap.get("subReportName").equals("AllergyAnalysisTable")){

                                    List<String> items =  (smap.get("data"))==null?new ArrayList<>():(List<String>)((Map<String,Object>)smap.get("data")).get("items");
                                    List<Integer> values = (smap.get("data"))==null?new ArrayList<>():(List<Integer>) ((Map<String,Object>)smap.get("data")).get("values");
                                    dataBeanList = new ArrayList();
                                    DataBean dataBean = new DataBean();
                                    for (int i = 0;i < items.size();i++){
                                        dataBean = new DataBean();
                                        dataBean.setName(items.get(i));
                                        dataBean.setCountry(values.get(i)+"");
                                        dataBean.setTitle(((Map<String,Object>)smap.get("data")).get("title").toString());
                                        dataBean.setDate(((Map<String,Object>)smap.get("data")).get("date").toString());
                                        dataBeanList.add(dataBean);
                                    }
                                }
                                sunBeanList.add(dataBeanList);
                            }
//                            for (int i = 0;i<sunBeanList.size();i=i+2){
//                                JRBeanCollectionDataSource ds1 = new JRBeanCollectionDataSource(sunBeanList.get(i));
//                                JRBeanCollectionDataSource ds2 = null;
//                                if (i+1<sunBeanList.size()){
//                                    ds2 = new JRBeanCollectionDataSource(sunBeanList.get(i+1));
//                                }
//                               // data.add(new Country(1,((Map<String,Object>)submap.get("data")).get("title").toString(),ds1,ds2));
//                            }
                            JasperDesign jasperDesign1 = JRXmlLoader.load(new FileInputStream(xml_save_path+"/AllergyAnalysisTable1.jrxml"));
                            JasperReport jasperReport1 = JasperCompileManager.compileReport(jasperDesign1);
                            JasperDesign jasperDesign2 = JRXmlLoader.load(new FileInputStream(xml_save_path+"/AllergyAnalysisTable2.jrxml"));
                            JasperReport jasperReport2 = JasperCompileManager.compileReport(jasperDesign2);
                            JasperDesign jasperDesign3 = JRXmlLoader.load(new FileInputStream(xml_save_path+"/AllergyAnalysisTable.jrxml"));
                            JasperReport jasperReport3 = JasperCompileManager.compileReport(jasperDesign3);
                            List<County1> county1List = new ArrayList<>();
                            List<County1> county2List = new ArrayList<>();
                            for (int i = 0;i<sunBeanList.size();i=i+2){
                                JRBeanCollectionDataSource ds1 = new JRBeanCollectionDataSource(sunBeanList.get(i));
                                JRBeanCollectionDataSource ds2 = null;
                                if (i+1<sunBeanList.size()){
                                    ds2 = new JRBeanCollectionDataSource(sunBeanList.get(i+1));
                                }
                                county1List.add(new County1(ds1,jasperReport3));
                                county2List.add(new County1(ds2,jasperReport3));
                            }
                            data.add(new Country(1,((Map<String,Object>)submap.get("data")).get("title").toString(),new JRBeanCollectionDataSource(county1List),
                                    new JRBeanCollectionDataSource(county2List),jasperReport1,jasperReport2));

                            mainList.add(new MainReports(jReport,jReport2,new JRBeanCollectionDataSource(data)));
                        }else if (submap!=null && !StringUtils.isEmpty(submap.get("subReportName"))&& submap.get("subReportName").equals("LaboratoryTest")){
                            List<LaboratoryTest> laboratoryTests = new ArrayList<>();
                            List<LaboratoryTest> laboratoryTests1 = new ArrayList<>();
                            JasperDesign labJasperDesign = JRXmlLoader.load(new FileInputStream(xml_save_path+"/LaboratotyTestTable.jrxml"));
                            JasperReport labjasperReport = JasperCompileManager.compileReport(labJasperDesign);
                            JasperDesign jasperDesign1 = JRXmlLoader.load(xml_save_path+"/LaboratoryTest.jrxml");
                            JasperReport jasperReport1 = JasperCompileManager.compileReport(jasperDesign1);

                            JasperDesign jasperDesign2 = JRXmlLoader.load(xml_save_path+"/LaboratoryTest1.jrxml");
                            JasperReport jasperReport2 = JasperCompileManager.compileReport(jasperDesign2);
                            if (!StringUtils.isEmpty(submap.get("subReports"))){
                                JSONArray array1 = JSON.parseArray(submap.get("subReports").toString());
                                for (Object  subReport : array1) {
                                    List<LaboratoryTestTable> labList = new ArrayList<>();
                                    Map<String,Object> map1 = (Map<String, Object>) subReport;
                                    if (StringUtils.isEmpty(map1.get("subReportName"))){
                                        continue;
                                    }
                                    if (!StringUtils.isEmpty(map1.get("data"))){
                                        JSONObject dataJson = JSON.parseObject(map1.get("data").toString());
                                        String title = dataJson.get("title")==null?"":dataJson.get("title").toString();
                                        String date = dataJson.get("date")==null?"":dataJson.get("date").toString();
                                        if (!StringUtils.isEmpty( dataJson.get("values"))){
                                            JSONArray value = JSON.parseArray(dataJson.get("values").toString() );
                                            for (Object objec : value){
                                                Map<String,Object> data = (Map<String, Object>) objec;
                                                String name = data.get("name")==null?"":data.get("name").toString();
                                                String ename = data.get("ChineseName")==null?"":data.get("ChineseName").toString();
                                                double value1 = data.get("testValue")==null?0: Double.valueOf( data.get("testValue").toString() );
                                                double max = data.get("MaxReferValue")==null?0: Double.valueOf(  data.get("MaxReferValue").toString() );
                                                String unit = data.get("unit")==null?"":data.get("unit").toString();
                                                double min =  data.get("MinReferValue")==null?0: Double.valueOf( data.get("MinReferValue").toString());

                                                labList.add(new LaboratoryTestTable(title,date,name,ename,max,min,value1,unit));
                                            }
                                            laboratoryTests1.add(new LaboratoryTest(new JRBeanCollectionDataSource(labList
                                                    ),labjasperReport));
                                        }
                                    }

                                }
                                laboratoryTests.add(new LaboratoryTest(new JRBeanCollectionDataSource(laboratoryTests1),jasperReport2));
                            }
                            mainList.add(new MainReports(jasperReport1, jasperReport1 ,
                                    new JRBeanCollectionDataSource(laboratoryTests)));
                        }

                    }else {
                        if (submap!=null && submap.get("subReportName").equals("SectionHeader")){
                            jDesign = JRXmlLoader.load(new File(xml_save_path+"/SectionHeader.jrxml"));
                            jReport = JasperCompileManager.compileReport(jDesign);
                            JSONObject allergyObject = JSONObject.parseObject(submap.get("data").toString());
                            String title = allergyObject.get("title")==null?"":allergyObject.get("title").toString();
                            String subTitle = allergyObject.get("subTitle")==null?"":allergyObject.get("subTitle").toString();
                            mainList.add(new MainReports(jReport, jReport ,
                                    new JRBeanCollectionDataSource(Arrays.asList(new SectionHeader(title,subTitle)))));

                        }else if (submap!=null && submap.get("subReportName").equals("DiseaseGeneticRiskTable")){
                            jDesign = JRXmlLoader.load(new File(xml_save_path+"/DiseaseGeneticRiskTable.jrxml"));
                            jReport = JasperCompileManager.compileReport(jDesign);
                            String name = "";
                            String fx = "";
                            List<String> itemList = new ArrayList<>();
                            List<Integer> valueList = new ArrayList<>();
                            List<DiseaseGeneticRiskTable> disList = new ArrayList<>();
                            if (!StringUtils.isEmpty(submap.get("column0"))){
                                JSONObject allergyObject = JSONObject.parseObject(submap.get("column0").toString());
                                name = StringUtils.isEmpty(allergyObject.get("name"))?"":allergyObject.get("name").toString();
                                if(!StringUtils.isEmpty(allergyObject.get("values"))){
                                    JSONArray diseaseArray = JSON.parseArray(allergyObject.get("values").toString());
                                    for (Object object : diseaseArray){
                                        itemList.add( StringUtils.isEmpty(object)?"":object.toString() );
                                    }
                                }
                            }
                            if (!StringUtils.isEmpty(submap.get("column1"))){
                                JSONObject allergyObject = JSONObject.parseObject(submap.get("column1").toString());
                                fx = StringUtils.isEmpty(allergyObject.get("name"))?"":allergyObject.get("name").toString();
                                if(!StringUtils.isEmpty(allergyObject.get("values"))){
                                    JSONArray diseaseArray = JSON.parseArray(allergyObject.get("values").toString());
                                    for (Object object : diseaseArray){
                                        valueList.add( StringUtils.isEmpty(object)?0: (Integer) object);
                                    }
                                }
                            }
                            for (int i = 0;i < itemList.size(); i++){
                                DiseaseGeneticRiskTable table = new DiseaseGeneticRiskTable(name,fx,itemList.get(i),valueList.get(i));
                                disList.add(table);
                            }
                            mainList.add(new MainReports(jReport, jReport ,
                                    new JRBeanCollectionDataSource(disList)));
                        }else if (submap!=null && submap.get("subReportName").equals("SuggestionReport")){
                            jDesign = JRXmlLoader.load(new File(xml_save_path+"/SuggestionReport.jrxml"));
                            jReport = JasperCompileManager.compileReport(jDesign);
                            JSONObject allergyObject = JSONObject.parseObject(submap.get("data").toString());
                            String title = allergyObject.get("title")==null?"":allergyObject.get("title").toString();
                            String text = allergyObject.get("text")==null?"":allergyObject.get("text").toString();
                            mainList.add(new MainReports(jReport, jReport ,
                                    new JRBeanCollectionDataSource(Arrays.asList(new SuggestionReport(title,text)))));

                        } else if (submap!=null && submap.get("subReportName")!=null
                                && ( submap.get("subReportName").equals("BodyAntiCancerCellAbilityEvaluation") || submap.get("subReportName").equals("FoodFamilyAndPossilableSource")
                                     || submap.get("subReportName").equals("microRNACancerObservation") || submap.get("subReportName").equals("NutritionAndToxicityElementalAnalysis")
                                     || submap.get("subReportName").equals("HypothalamicPituitaryOvarianEndocrineRegulation") || submap.get("subReportName").equals("HarvardCancerRiskEvaluation")
                                     || submap.get("subReportName").equals("BodyAntiCancerCellAbilityEvaluation") || submap.get("subReportName").equals("BodyCancerMarkerScreen")
                                     || submap.get("subReportName").equals("CancerInheritanceRiskEvaluation") || submap.get("subReportName").equals("FoodAllergySourceAnalysis")
                            )){
                            jDesign = JRXmlLoader.load(new File(xml_save_path+"/"+submap.get("subReportName")+".jrxml"));
                            jReport = JasperCompileManager.compileReport(jDesign);
                            mainList.add(new MainReports(jReport, jReport ,
                                    new JRBeanCollectionDataSource(Arrays.asList(""))));
                        }else if (submap!=null && submap.get("subReportName")!=null){
                            otherList.add(submap.get("subReportName").toString());
                        }

                    }
                }
            }
            if (otherList.size()>0){
                resultMap.put("status",404);
                resultMap.put("userMessage","cannot find those templates");
                resultMap.put("templateNames",otherList);
                return resultMap;

            }
            JasperPrint jasperPrint = fillManager.fillReport(jReportMain, map, new JRBeanCollectionDataSource(mainList));

            //export with pdf
            //返回输出流
//        final OutputStream outStream = response.getOutputStream();
//        String fileName = "test.pdf";
//        response.addHeader("Content-Disposition", "filename=" + fileName);
            outs =new FileOutputStream(pdf_export_path+"/"+pdfName);
            exportManager.exportToPdfStream(jasperPrint, outs);
            //给前台返回信息
            resultMap.put("status",200);
            resultMap.put("pdfName",pdfName);
            resultMap.put("fileFolderPath",pdf_export_path);
            return resultMap;
        }catch (Exception e){
            e.printStackTrace();
            resultMap.put("status",400);
            resultMap.put("error",e.getMessage());
            return resultMap;
        }finally {
            outs.close();
        }
    }

    @RequestMapping(value = "/testjson",
            method = { RequestMethod.GET, RequestMethod.POST },
            produces = "application/json;charset=UTF-8")
    @ResponseBody
    public void groupVoid7(HttpServletResponse response,HttpServletRequest request,
                           @RequestParam(value = "json") String json) throws JRException, IOException  {
//        JSONObject jsonObject = JSON.parseObject(json);
//        System.out.println(jsonObject.get("title"));
        String xml_save_path = propertyConfigurer.getProperty("xml_save_path");
        String pdf_export_path =  propertyConfigurer.getProperty("pdf_export_path");
        System.out.println("xml_save_path:"+xml_save_path+"\n"+
                            "pdf_export_path:"+pdf_export_path);

    }

    @RequestMapping(value = "/testMain",
            method = { RequestMethod.GET, RequestMethod.POST },
            produces = "application/json;charset=UTF-8")
    @ResponseBody
    public void groupVoid5(HttpServletResponse response,HttpServletRequest request) throws JRException, IOException  {

        String  realPath = request.getSession().getServletContext().getRealPath("");


        File reportsDir = new File(realPath + "/jrxml/MyReports/");
        LocalJasperReportsContext ctx = new LocalJasperReportsContext(DefaultJasperReportsContext.getInstance());
        ctx.setClassLoader(getClass().getClassLoader());
        ctx.setFileResolver(new SimpleFileResolver(reportsDir));//注意，设置JasperReport的相对路径，很重要
        JRDataSource jrDataSource = new JRBeanCollectionDataSource(null);
        JasperFillManager fillManager = JasperFillManager.getInstance(ctx);
        JasperExportManager exportManager = JasperExportManager.getInstance(ctx);

        //MainReports 主表中的数据
        List<MainReports> mainList = new ArrayList<>();
        MainReport mainReport = new MainReport();
        Object subReports = JsonUtil.mainReport(JsonUtil.json,mainReport);
        Map map = new HashMap();
        map.put("name",mainReport.getCustomerID());
        map.put("birth",mainReport.getCustomerBirthDate());
        map.put(JRParameter.REPORT_FILE_RESOLVER, new SimpleFileResolver(reportsDir));
        JasperDesign jDesignMain = JRXmlLoader.load(new File(realPath+"/jrxml/MyReports/MainReports.jrxml"));
        JasperReport jReportMain = JasperCompileManager.compileReport(jDesignMain);
        //jasper数据
        JasperDesign jDesign = null;
        JasperReport jReport = null;
        //循环子列表
        if (!StringUtils.isEmpty( subReports )){
            JSONArray array = JSON.parseArray(subReports.toString());
            for (Object subObject : array) {
                Map<String,Object> submap = (Map<String, Object>) subObject;
                System.out.println("map:"+submap.get("subReportName"));
                System.out.println("data"+((Map<String,Object>)submap.get("data")).get("title"));
                if (!StringUtils.isEmpty(submap.get("subReports"))){
//                    System.out.println(map.get("subReports").toString());
                    if (submap!=null && submap.get("subReportName").equals("AllergyAnalysis")){
                        List<Country> data = new ArrayList();
                        jDesign =  JRXmlLoader.load(new File(realPath+"/jrxml/MyReports/AllergyAnalysis.jrxml"));
                        jReport = JasperCompileManager.compileReport(jDesign);

                        JasperDesign jDesign2 = JRXmlLoader.load(new File(realPath+"/jrxml/MyReports/AllergyAnalysisTable.jrxml"));
                        JasperReport jReport2 = JasperCompileManager.compileReport(jDesign2);
                        List<Map<String,Object>> slist = (List<Map<String, Object>>) submap.get("subReports");
                        for (Map<String,Object> smap :slist){
                            List<DataBean> dataBeanList = new ArrayList();
                            if(smap!=null && smap.get("subReportName").equals("AllergyAnalysisTable")){

                                List<String> items =  (smap.get("data"))==null?new ArrayList<>():(List<String>)((Map<String,Object>)smap.get("data")).get("items");
                                List<Integer> values = (smap.get("data"))==null?new ArrayList<>():(List<Integer>) ((Map<String,Object>)smap.get("data")).get("values");
                                dataBeanList = new ArrayList();
                                DataBean dataBean = new DataBean();
                                for (int i = 0;i < items.size();i++){
                                    dataBean = new DataBean();
                                    dataBean.setName(items.get(i));
                                    dataBean.setCountry(values.get(i)+"");
                                    dataBean.setTitle(((Map<String,Object>)smap.get("data")).get("title").toString());
                                    dataBean.setDate(((Map<String,Object>)smap.get("data")).get("date").toString());
                                    dataBeanList.add(dataBean);
                                }
                            }
                            JRBeanCollectionDataSource ds1 = new JRBeanCollectionDataSource(dataBeanList);
                          //  data.add(new Country(1,((Map<String,Object>)submap.get("data")).get("title").toString(),ds1,ds1));

                        }

                        mainList.add(new MainReports(jReport,jReport2,new JRBeanCollectionDataSource(data)));
                    }

                }else {
                    if (submap!=null && submap.get("subReportName").equals("SectionHeader")){
                        jDesign = JRXmlLoader.load(new File(realPath+"/jrxml/MyReports/SectionHeader.jrxml"));
                        jReport = JasperCompileManager.compileReport(jDesign);
                        JSONObject allergyObject = JSONObject.parseObject(submap.get("data").toString());
                        String title = allergyObject.get("title")==null?"":allergyObject.get("title").toString();
                        String subTitle = allergyObject.get("subTitle")==null?"":allergyObject.get("subTitle").toString();
                        mainList.add(new MainReports(jReport, jReport ,
                                new JRBeanCollectionDataSource(Arrays.asList(new SectionHeader(title,subTitle)))));

                    }
                }
            }
        }
        JasperPrint jasperPrint = fillManager.fillReport(jReportMain, map, new JRBeanCollectionDataSource(mainList));

        //export with pdf
        //返回输出流
        final OutputStream outStream = response.getOutputStream();
        String fileName = "test.pdf";
        response.addHeader("Content-Disposition", "filename=" + fileName);
        exportManager.exportToPdfStream(jasperPrint, outStream);
    }


    @RequestMapping(value = "/pdf",
            method = {  RequestMethod.POST },
            produces = "application/json;charset=UTF-8")
    @ResponseBody
    public void groupVoid6(HttpServletResponse response,HttpServletRequest request,
                             @RequestParam(value = "json") String json,
                             @RequestParam(value = "pdfName",required = false) String pdfName
    ) throws JRException, IOException  {

        String  realPath = request.getSession().getServletContext().getRealPath("");
        List<String> otherList = new ArrayList<>();
        Map<String,Object> resultMap =  new HashMap();
        if (StringUtils.isEmpty(pdfName)){
            pdfName = String.valueOf( System.currentTimeMillis() )+".pdf";
        }else if (!pdfName.contains(".pdf")){
            pdfName = pdfName+".pdf";
        }

        File reportsDir = new File(realPath + "/jrxml/MyReports/");
        LocalJasperReportsContext ctx = new LocalJasperReportsContext(DefaultJasperReportsContext.getInstance());
        ctx.setClassLoader(getClass().getClassLoader());
        ctx.setFileResolver(new SimpleFileResolver(reportsDir));//注意，设置JasperReport的相对路径，很重要
        JRDataSource jrDataSource = new JRBeanCollectionDataSource(null);
        JasperFillManager fillManager = JasperFillManager.getInstance(ctx);
        JasperExportManager exportManager = JasperExportManager.getInstance(ctx);

        try {
            //MainReports 主表中的数据
            List<MainReports> mainList = new ArrayList<>();
            MainReport mainReport = new MainReport();
            Object subReports = JsonUtil.mainReport(json,mainReport);
            Map map = new HashMap();
            map.put("name",mainReport.getCustomerID());
            map.put("birth",mainReport.getCustomerBirthDate());

            map.put(JRParameter.REPORT_FILE_RESOLVER, new SimpleFileResolver(reportsDir));
            JasperDesign jDesignMain = JRXmlLoader.load(new File(realPath+"/jrxml/MyReports/MainReports.jrxml"));
            JasperReport jReportMain = JasperCompileManager.compileReport(jDesignMain);
            //jasper数据
            JasperDesign jDesign = null;
            JasperReport jReport = null;
            //循环子列表
            if (!StringUtils.isEmpty( subReports )){
                JSONArray array = JSON.parseArray(subReports.toString());
                for (Object subObject : array) {
                    Map<String,Object> submap = (Map<String, Object>) subObject;
//                    System.out.println("map:"+submap.get("subReportName"));
//                    System.out.println("data"+((Map<String,Object>)submap.get("data")).get("title"));
                    //开始循环如果存在子模板
                    if (!StringUtils.isEmpty(submap.get("subReports"))){
//                    System.out.println(map.get("subReports").toString());
                        if (submap!=null && submap.get("subReportName").equals("AllergyAnalysis")){
                            List<Country> data = new ArrayList();
                            jDesign =  JRXmlLoader.load(new File(realPath+"/jrxml/MyReports/AllergyAnalysis.jrxml"));
                            jReport = JasperCompileManager.compileReport(jDesign);

                            JasperDesign jDesign2 = JRXmlLoader.load(new File(realPath+"/jrxml/MyReports/AllergyAnalysisTable.jrxml"));
                            JasperReport jReport2 = JasperCompileManager.compileReport(jDesign2);
                            List<Map<String,Object>> slist = (List<Map<String, Object>>) submap.get("subReports");
                            int num = 0 ;
                            List<List<DataBean>>  sunBeanList = new ArrayList<>();
                            for (Map<String,Object> smap :slist){
                                List<DataBean> dataBeanList = new ArrayList();
                                if(smap!=null && smap.get("subReportName").equals("AllergyAnalysisTable")){

                                    List<String> items =  (smap.get("data"))==null?new ArrayList<>():(List<String>)((Map<String,Object>)smap.get("data")).get("items");
                                    List<Integer> values = (smap.get("data"))==null?new ArrayList<>():(List<Integer>) ((Map<String,Object>)smap.get("data")).get("values");
                                    dataBeanList = new ArrayList();
                                    DataBean dataBean = new DataBean();
                                    for (int i = 0;i < items.size();i++){
                                        dataBean = new DataBean();
                                        dataBean.setName(items.get(i));
                                        dataBean.setCountry(values.get(i)+"");
                                        dataBean.setTitle(((Map<String,Object>)smap.get("data")).get("title").toString());
                                        dataBean.setDate(((Map<String,Object>)smap.get("data")).get("date").toString());
                                        dataBeanList.add(dataBean);
                                    }
                                }
                                sunBeanList.add(dataBeanList);
                            }
                            for (int i = 0;i<sunBeanList.size();i=i+2){
                                JRBeanCollectionDataSource ds1 = new JRBeanCollectionDataSource(sunBeanList.get(i));
                                JRBeanCollectionDataSource ds2 = null;
                                if (i+1<sunBeanList.size()){
                                    ds2 = new JRBeanCollectionDataSource(sunBeanList.get(i+1));
                                }
                               // data.add(new Country(1,((Map<String,Object>)submap.get("data")).get("title").toString(),ds1,ds2));
                            }


                            mainList.add(new MainReports(jReport,jReport2,new JRBeanCollectionDataSource(data)));
                        }

                    }else {
                        if (submap!=null && submap.get("subReportName").equals("SectionHeader")){
                            jDesign = JRXmlLoader.load(new File(realPath+"/jrxml/MyReports/SectionHeader.jrxml"));
                            jReport = JasperCompileManager.compileReport(jDesign);
                            JSONObject allergyObject = JSONObject.parseObject(submap.get("data").toString());
                            String title = allergyObject.get("title")==null?"":allergyObject.get("title").toString();
                            String subTitle = allergyObject.get("subTitle")==null?"":allergyObject.get("subTitle").toString();
                            mainList.add(new MainReports(jReport, jReport ,
                                    new JRBeanCollectionDataSource(Arrays.asList(new SectionHeader(title,subTitle)))));

                        }else if (submap!=null && submap.get("subReportName").equals("DiseaseGeneticRiskTable")){
                            jDesign = JRXmlLoader.load(new File(realPath+"/jrxml/MyReports/DiseaseGeneticRiskTable.jrxml"));
                            jReport = JasperCompileManager.compileReport(jDesign);
                            String name = "";
                            String fx = "";
                            List<String> itemList = new ArrayList<>();
                            List<Integer> valueList = new ArrayList<>();
                            List<DiseaseGeneticRiskTable> disList = new ArrayList<>();
                            if (!StringUtils.isEmpty(submap.get("column0"))){
                                JSONObject allergyObject = JSONObject.parseObject(submap.get("column0").toString());
                                name = StringUtils.isEmpty(allergyObject.get("name"))?"":allergyObject.get("name").toString();
                                if(!StringUtils.isEmpty(allergyObject.get("values"))){
                                    JSONArray diseaseArray = JSON.parseArray(allergyObject.get("values").toString());
                                    for (Object object : diseaseArray){
                                        itemList.add( StringUtils.isEmpty(object)?"":object.toString() );
                                    }
                                }
                            }
                            if (!StringUtils.isEmpty(submap.get("column1"))){
                                JSONObject allergyObject = JSONObject.parseObject(submap.get("column1").toString());
                                fx = StringUtils.isEmpty(allergyObject.get("name"))?"":allergyObject.get("name").toString();
                                if(!StringUtils.isEmpty(allergyObject.get("values"))){
                                    JSONArray diseaseArray = JSON.parseArray(allergyObject.get("values").toString());
                                    for (Object object : diseaseArray){
                                        valueList.add( StringUtils.isEmpty(object)?0: (Integer) object);
                                    }
                                }
                            }
                            for (int i = 0;i < itemList.size(); i++){
                                DiseaseGeneticRiskTable table = new DiseaseGeneticRiskTable(name,fx,itemList.get(i),valueList.get(i));
                                disList.add(table);
                            }
                            mainList.add(new MainReports(jReport, jReport ,
                                    new JRBeanCollectionDataSource(disList)));
                        }else if (submap!=null && submap.get("subReportName")!=null
                                && ( submap.get("subReportName").equals("BodyAntiCancerCellAbilityEvaluation") || submap.get("subReportName").equals("FoodFamilyAndPossilableSource")
                                || submap.get("subReportName").equals("microRNACancerObservation") || submap.get("subReportName").equals("NutritionAndToxicityElementalAnalysis")
                                || submap.get("subReportName").equals("HypothalamicPituitaryOvarianEndocrineRegulation") || submap.get("subReportName").equals("HarvardCancerRiskEvaluation")
                                || submap.get("subReportName").equals("BodyAntiCancerCellAbilityEvaluation") || submap.get("subReportName").equals("BodyCancerMarkerScreen")
                                || submap.get("subReportName").equals("CancerInheritanceRiskEvaluation") || submap.get("subReportName").equals("FoodAllergySourceAnalysis")
                        )){
                            jDesign = JRXmlLoader.load(new File(realPath+"/jrxml/MyReports/"+submap.get("subReportName")+".jrxml"));
                            jReport = JasperCompileManager.compileReport(jDesign);
                            mainList.add(new MainReports(jReport, jReport ,
                                    new JRBeanCollectionDataSource(Arrays.asList(""))));
                        }else if (submap!=null && submap.get("subReportName")!=null){
                            otherList.add(submap.get("subReportName").toString());
                        }

                    }
                }
            }
            JasperPrint jasperPrint = fillManager.fillReport(jReportMain, map, new JRBeanCollectionDataSource(mainList));

            //export with pdf
            //返回输出流
        final OutputStream outStream = response.getOutputStream();
//        String fileName = "test.pdf";
        response.addHeader("Content-Disposition", "filename=" + pdfName);
//            FileOutputStream outs =new FileOutputStream(realPath+"/pdf/"+pdfName);
            exportManager.exportToPdfStream(jasperPrint, outStream);

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @RequestMapping(value = "/pdfPost",
            method = {  RequestMethod.POST },
            produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Object groupVoid7(HttpServletResponse response,HttpServletRequest request,
                             @RequestParam(value = "json") String json,
                             @RequestParam(value = "pdfName",required = false) String pdfName
    ) throws JRException, IOException  {

        String  realPath = request.getSession().getServletContext().getRealPath("");
        List<String> otherList = new ArrayList<>();
        Map<String,Object> resultMap =  new HashMap();
        if (StringUtils.isEmpty(pdfName)){
            pdfName = String.valueOf( System.currentTimeMillis() )+".pdf";
        }else if (!pdfName.contains(".pdf")){
            pdfName = pdfName+".pdf";
        }

        File reportsDir = new File(realPath + "/jrxml/MyReports/");
        LocalJasperReportsContext ctx = new LocalJasperReportsContext(DefaultJasperReportsContext.getInstance());
        ctx.setClassLoader(getClass().getClassLoader());
        ctx.setFileResolver(new SimpleFileResolver(reportsDir));//注意，设置JasperReport的相对路径，很重要
        JRDataSource jrDataSource = new JRBeanCollectionDataSource(null);
        JasperFillManager fillManager = JasperFillManager.getInstance(ctx);
        JasperExportManager exportManager = JasperExportManager.getInstance(ctx);

        try {
            //MainReports 主表中的数据
            List<MainReports> mainList = new ArrayList<>();
            MainReport mainReport = new MainReport();
            Object subReports = JsonUtil.mainReport(json,mainReport);
            Map map = new HashMap();
            map.put("name",mainReport.getCustomerID());
            map.put("birth",mainReport.getCustomerBirthDate());
            map.put(JRParameter.REPORT_FILE_RESOLVER, new SimpleFileResolver(reportsDir));
            JasperDesign jDesignMain = JRXmlLoader.load(new File(realPath+"/jrxml/MyReports/MainReports.jrxml"));
            JasperReport jReportMain = JasperCompileManager.compileReport(jDesignMain);
            //jasper数据
            JasperDesign jDesign = null;
            JasperReport jReport = null;
            //循环子列表
            if (!StringUtils.isEmpty( subReports )){
                JSONArray array = JSON.parseArray(subReports.toString());
                for (Object subObject : array) {
                    Map<String,Object> submap = (Map<String, Object>) subObject;
//                    System.out.println("map:"+submap.get("subReportName"));
//                    System.out.println("data"+((Map<String,Object>)submap.get("data")).get("title"));
                    //开始循环如果存在子模板
                    if (!StringUtils.isEmpty(submap.get("subReports"))){
//                    System.out.println(map.get("subReports").toString());
                        if (submap!=null && submap.get("subReportName").equals("AllergyAnalysis")){
                            List<Country> data = new ArrayList();
                            jDesign =  JRXmlLoader.load(new File(realPath+"/jrxml/MyReports/AllergyAnalysis.jrxml"));
                            jReport = JasperCompileManager.compileReport(jDesign);

                            JasperDesign jDesign2 = JRXmlLoader.load(new File(realPath+"/jrxml/MyReports/AllergyAnalysisTable.jrxml"));
                            JasperReport jReport2 = JasperCompileManager.compileReport(jDesign2);
                            List<Map<String,Object>> slist = (List<Map<String, Object>>) submap.get("subReports");
                            for (Map<String,Object> smap :slist){
                                List<DataBean> dataBeanList = new ArrayList();
                                if(smap!=null && smap.get("subReportName").equals("AllergyAnalysisTable")){

                                    List<String> items =  (smap.get("data"))==null?new ArrayList<>():(List<String>)((Map<String,Object>)smap.get("data")).get("items");
                                    List<Integer> values = (smap.get("data"))==null?new ArrayList<>():(List<Integer>) ((Map<String,Object>)smap.get("data")).get("values");
                                    dataBeanList = new ArrayList();
                                    DataBean dataBean = new DataBean();
                                    for (int i = 0;i < items.size();i++){
                                        dataBean = new DataBean();
                                        dataBean.setName(items.get(i));
                                        dataBean.setCountry(values.get(i)+"");
                                        dataBean.setTitle(((Map<String,Object>)smap.get("data")).get("title").toString());
                                        dataBean.setDate(((Map<String,Object>)smap.get("data")).get("date").toString());
                                        dataBeanList.add(dataBean);
                                    }
                                }
                                JRBeanCollectionDataSource ds1 = new JRBeanCollectionDataSource(dataBeanList);
                               // data.add(new Country(1,((Map<String,Object>)submap.get("data")).get("title").toString(),ds1,ds1));

                            }

                            mainList.add(new MainReports(jReport,jReport2,new JRBeanCollectionDataSource(data)));
                        }

                    }else {
                        if (submap!=null && submap.get("subReportName").equals("SectionHeader")){
                            jDesign = JRXmlLoader.load(new File(realPath+"/jrxml/MyReports/SectionHeader.jrxml"));
                            jReport = JasperCompileManager.compileReport(jDesign);
                            JSONObject allergyObject = JSONObject.parseObject(submap.get("data").toString());
                            String title = allergyObject.get("title")==null?"":allergyObject.get("title").toString();
                            String subTitle = allergyObject.get("subTitle")==null?"":allergyObject.get("subTitle").toString();
                            mainList.add(new MainReports(jReport, jReport ,
                                    new JRBeanCollectionDataSource(Arrays.asList(new SectionHeader(title,subTitle)))));

                        }else if (submap!=null && submap.get("subReportName").equals("DiseaseGeneticRiskTable")){
                            jDesign = JRXmlLoader.load(new File(realPath+"/jrxml/MyReports/DiseaseGeneticRiskTable.jrxml"));
                            jReport = JasperCompileManager.compileReport(jDesign);
                            String name = "";
                            String fx = "";
                            List<String> itemList = new ArrayList<>();
                            List<Integer> valueList = new ArrayList<>();
                            List<DiseaseGeneticRiskTable> disList = new ArrayList<>();
                            if (!StringUtils.isEmpty(submap.get("column0"))){
                                JSONObject allergyObject = JSONObject.parseObject(submap.get("column0").toString());
                                name = StringUtils.isEmpty(allergyObject.get("name"))?"":allergyObject.get("name").toString();
                                if(!StringUtils.isEmpty(allergyObject.get("values"))){
                                    JSONArray diseaseArray = JSON.parseArray(allergyObject.get("values").toString());
                                    for (Object object : diseaseArray){
                                        itemList.add( StringUtils.isEmpty(object)?"":object.toString() );
                                    }
                                }
                            }
                            if (!StringUtils.isEmpty(submap.get("column1"))){
                                JSONObject allergyObject = JSONObject.parseObject(submap.get("column1").toString());
                                fx = StringUtils.isEmpty(allergyObject.get("name"))?"":allergyObject.get("name").toString();
                                if(!StringUtils.isEmpty(allergyObject.get("values"))){
                                    JSONArray diseaseArray = JSON.parseArray(allergyObject.get("values").toString());
                                    for (Object object : diseaseArray){
                                        valueList.add( StringUtils.isEmpty(object)?0: (Integer) object);
                                    }
                                }
                            }
                            for (int i = 0;i < itemList.size(); i++){
                                DiseaseGeneticRiskTable table = new DiseaseGeneticRiskTable(name,fx,itemList.get(i),valueList.get(i));
                                disList.add(table);
                            }
                            mainList.add(new MainReports(jReport, jReport ,
                                    new JRBeanCollectionDataSource(disList)));
                        }else if (submap!=null && submap.get("subReportName")!=null
                                && ( submap.get("subReportName").equals("BodyAntiCancerCellAbilityEvaluation") || submap.get("subReportName").equals("FoodFamilyAndPossilableSource")
                                || submap.get("subReportName").equals("microRNACancerObservation") || submap.get("subReportName").equals("NutritionAndToxicityElementalAnalysis")
                                || submap.get("subReportName").equals("HypothalamicPituitaryOvarianEndocrineRegulation") || submap.get("subReportName").equals("HarvardCancerRiskEvaluation")
                                || submap.get("subReportName").equals("BodyAntiCancerCellAbilityEvaluation") || submap.get("subReportName").equals("BodyCancerMarkerScreen")
                                || submap.get("subReportName").equals("CancerInheritanceRiskEvaluation") || submap.get("subReportName").equals("FoodAllergySourceAnalysis")
                        )){
                            jDesign = JRXmlLoader.load(new File(realPath+"/jrxml/MyReports/"+submap.get("subReportName")+".jrxml"));
                            jReport = JasperCompileManager.compileReport(jDesign);
                            mainList.add(new MainReports(jReport, jReport ,
                                    new JRBeanCollectionDataSource(Arrays.asList(""))));
                        }else if (submap!=null && submap.get("subReportName")!=null){
                            otherList.add(submap.get("subReportName").toString());
                        }

                    }
                }
            }
            if (otherList.size()>0){
                resultMap.put("status",404);
                resultMap.put("userMessage","cannot find those templates");
                resultMap.put("templateNames",otherList);
                return resultMap;

            }
            JasperPrint jasperPrint = fillManager.fillReport(jReportMain, map, new JRBeanCollectionDataSource(mainList));

            //export with pdf
            //返回输出流
//        final OutputStream outStream = response.getOutputStream();
//        String fileName = "test.pdf";
//        response.addHeader("Content-Disposition", "filename=" + fileName);
            FileOutputStream outs =new FileOutputStream(realPath+"/pdf/"+pdfName);
            exportManager.exportToPdfStream(jasperPrint, outs);
            //给前台返回信息
            resultMap.put("status",200);
            resultMap.put("pdfName",pdfName);
            resultMap.put("fileFolderPath",realPath+"pdf");
            return resultMap;
        }catch (Exception e){
            e.printStackTrace();
            resultMap.put("status",400);
            resultMap.put("error",e.getMessage());
            return resultMap;
        }
    }

    @RequestMapping(value = "/doc",
            method = {  RequestMethod.GET },
            produces = "application/json;charset=UTF-8")
    @ResponseBody
    public void groupVoidDoc(HttpServletResponse response,HttpServletRequest request,
                             @RequestParam(value = "json") String json,
                             @RequestParam(value = "pdfName",required = false) String pdfName
    ) throws JRException, IOException  {

        String  realPath = request.getSession().getServletContext().getRealPath("");
        List<String> otherList = new ArrayList<>();
        Map<String,Object> resultMap =  new HashMap();
        if (StringUtils.isEmpty(pdfName)){
            pdfName = String.valueOf( System.currentTimeMillis() )+".docx";
        }else if (!pdfName.contains(".docx")){
            pdfName = pdfName+".docx";
        }

        File reportsDir = new File(realPath + "/jrxml/MyReports/");
        LocalJasperReportsContext ctx = new LocalJasperReportsContext(DefaultJasperReportsContext.getInstance());
        ctx.setClassLoader(getClass().getClassLoader());
        ctx.setFileResolver(new SimpleFileResolver(reportsDir));//注意，设置JasperReport的相对路径，很重要
        JRDataSource jrDataSource = new JRBeanCollectionDataSource(null);
        JasperFillManager fillManager = JasperFillManager.getInstance(ctx);
        JasperExportManager exportManager = JasperExportManager.getInstance(ctx);

        try {
            //MainReports 主表中的数据
            List<MainReports> mainList = new ArrayList<>();
            MainReport mainReport = new MainReport();
            Object subReports = JsonUtil.mainReport(json,mainReport);
            Map map = new HashMap();
            map.put("name",mainReport.getCustomerID());
            map.put("birth",mainReport.getCustomerBirthDate());
            map.put(JRParameter.REPORT_FILE_RESOLVER, new SimpleFileResolver(reportsDir));
            JasperDesign jDesignMain = JRXmlLoader.load(new File(realPath+"/jrxml/MyReports/MainReports.jrxml"));
            JasperReport jReportMain = JasperCompileManager.compileReport(jDesignMain);
            //jasper数据
            JasperDesign jDesign = null;
            JasperReport jReport = null;
            //循环子列表
            if (!StringUtils.isEmpty( subReports )){
                JSONArray array = JSON.parseArray(subReports.toString());
                for (Object subObject : array) {
                    Map<String,Object> submap = (Map<String, Object>) subObject;
//                    System.out.println("map:"+submap.get("subReportName"));
//                    System.out.println("data"+((Map<String,Object>)submap.get("data")).get("title"));
                    //开始循环如果存在子模板
                    if (!StringUtils.isEmpty(submap.get("subReports"))){
//                    System.out.println(map.get("subReports").toString());
                        if (submap!=null && submap.get("subReportName").equals("AllergyAnalysis")){
                            List<Country> data = new ArrayList();
                            jDesign =  JRXmlLoader.load(new File(realPath+"/jrxml/MyReports/AllergyAnalysis.jrxml"));
                            jReport = JasperCompileManager.compileReport(jDesign);

                            JasperDesign jDesign2 = JRXmlLoader.load(new File(realPath+"/jrxml/MyReports/AllergyAnalysisTable.jrxml"));
                            JasperReport jReport2 = JasperCompileManager.compileReport(jDesign2);
                            List<Map<String,Object>> slist = (List<Map<String, Object>>) submap.get("subReports");
                            for (Map<String,Object> smap :slist){
                                List<DataBean> dataBeanList = new ArrayList();
                                if(smap!=null && smap.get("subReportName").equals("AllergyAnalysisTable")){

                                    List<String> items =  (smap.get("data"))==null?new ArrayList<>():(List<String>)((Map<String,Object>)smap.get("data")).get("items");
                                    List<Integer> values = (smap.get("data"))==null?new ArrayList<>():(List<Integer>) ((Map<String,Object>)smap.get("data")).get("values");
                                    dataBeanList = new ArrayList();
                                    DataBean dataBean = new DataBean();
                                    for (int i = 0;i < items.size();i++){
                                        dataBean = new DataBean();
                                        dataBean.setName(items.get(i));
                                        dataBean.setCountry(values.get(i)+"");
                                        dataBean.setTitle(((Map<String,Object>)smap.get("data")).get("title").toString());
                                        dataBean.setDate(((Map<String,Object>)smap.get("data")).get("date").toString());
                                        dataBeanList.add(dataBean);
                                    }
                                }
                                JRBeanCollectionDataSource ds1 = new JRBeanCollectionDataSource(dataBeanList);
                               // data.add(new Country(1,((Map<String,Object>)submap.get("data")).get("title").toString(),ds1,ds1));

                            }

                            mainList.add(new MainReports(jReport,jReport2,new JRBeanCollectionDataSource(data)));
                        }

                    }else {
                        if (submap!=null && submap.get("subReportName").equals("SectionHeader")){
                            jDesign = JRXmlLoader.load(new File(realPath+"/jrxml/MyReports/SectionHeader.jrxml"));
                            jReport = JasperCompileManager.compileReport(jDesign);
                            JSONObject allergyObject = JSONObject.parseObject(submap.get("data").toString());
                            String title = allergyObject.get("title")==null?"":allergyObject.get("title").toString();
                            String subTitle = allergyObject.get("subTitle")==null?"":allergyObject.get("subTitle").toString();
                            mainList.add(new MainReports(jReport, jReport ,
                                    new JRBeanCollectionDataSource(Arrays.asList(new SectionHeader(title,subTitle)))));

                        }else if (submap!=null && submap.get("subReportName").equals("DiseaseGeneticRiskTable")){
                            jDesign = JRXmlLoader.load(new File(realPath+"/jrxml/MyReports/DiseaseGeneticRiskTable.jrxml"));
                            jReport = JasperCompileManager.compileReport(jDesign);
                            String name = "";
                            String fx = "";
                            List<String> itemList = new ArrayList<>();
                            List<Integer> valueList = new ArrayList<>();
                            List<DiseaseGeneticRiskTable> disList = new ArrayList<>();
                            if (!StringUtils.isEmpty(submap.get("column0"))){
                                JSONObject allergyObject = JSONObject.parseObject(submap.get("column0").toString());
                                name = StringUtils.isEmpty(allergyObject.get("name"))?"":allergyObject.get("name").toString();
                                if(!StringUtils.isEmpty(allergyObject.get("values"))){
                                    JSONArray diseaseArray = JSON.parseArray(allergyObject.get("values").toString());
                                    for (Object object : diseaseArray){
                                        itemList.add( StringUtils.isEmpty(object)?"":object.toString() );
                                    }
                                }
                            }
                            if (!StringUtils.isEmpty(submap.get("column1"))){
                                JSONObject allergyObject = JSONObject.parseObject(submap.get("column1").toString());
                                fx = StringUtils.isEmpty(allergyObject.get("name"))?"":allergyObject.get("name").toString();
                                if(!StringUtils.isEmpty(allergyObject.get("values"))){
                                    JSONArray diseaseArray = JSON.parseArray(allergyObject.get("values").toString());
                                    for (Object object : diseaseArray){
                                        valueList.add( StringUtils.isEmpty(object)?0: (Integer) object);
                                    }
                                }
                            }
                            for (int i = 0;i < itemList.size(); i++){
                                DiseaseGeneticRiskTable table = new DiseaseGeneticRiskTable(name,fx,itemList.get(i),valueList.get(i));
                                disList.add(table);
                            }
                            mainList.add(new MainReports(jReport, jReport ,
                                    new JRBeanCollectionDataSource(disList)));
                        }else if (submap!=null && submap.get("subReportName")!=null
                                && ( submap.get("subReportName").equals("BodyAntiCancerCellAbilityEvaluation") || submap.get("subReportName").equals("FoodFamilyAndPossilableSource")
                                || submap.get("subReportName").equals("microRNACancerObservation") || submap.get("subReportName").equals("NutritionAndToxicityElementalAnalysis")
                                || submap.get("subReportName").equals("HypothalamicPituitaryOvarianEndocrineRegulation") || submap.get("subReportName").equals("HarvardCancerRiskEvaluation")
                                || submap.get("subReportName").equals("BodyAntiCancerCellAbilityEvaluation") || submap.get("subReportName").equals("BodyCancerMarkerScreen")
                                || submap.get("subReportName").equals("CancerInheritanceRiskEvaluation") || submap.get("subReportName").equals("FoodAllergySourceAnalysis")
                        )){
                            jDesign = JRXmlLoader.load(new File(realPath+"/jrxml/MyReports/"+submap.get("subReportName")+".jrxml"));
                            jReport = JasperCompileManager.compileReport(jDesign);
                            mainList.add(new MainReports(jReport, jReport ,
                                    new JRBeanCollectionDataSource(Arrays.asList(""))));
                        }else if (submap!=null && submap.get("subReportName")!=null){
                            otherList.add(submap.get("subReportName").toString());
                        }

                    }
                }
            }
            JasperPrint jasperPrint = fillManager.fillReport(jReportMain, map, new JRBeanCollectionDataSource(mainList));

            JRDocxExporter exporter=new JRDocxExporter();
            //设置输入项
            ExporterInput exporterInput = new SimpleExporterInput(jasperPrint);
            exporter.setExporterInput(exporterInput);

            //设置输出项
//            OutputStreamExporterOutput exporterOutput = new SimpleOutputStreamExporterOutput(pdfName);
            OutputStreamExporterOutput exporterOutput = new SimpleOutputStreamExporterOutput(
                    response.getOutputStream());
            exporter.setExporterOutput(exporterOutput);
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-disposition", "attachment; filename="+pdfName);


            exporter.exportReport();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @RequestMapping(value = "/excel",
            method = {  RequestMethod.GET },
            produces = "application/json;charset=UTF-8")
    @ResponseBody
    public void groupVoidExcel(HttpServletResponse response,HttpServletRequest request,
                             @RequestParam(value = "json") String json,
                             @RequestParam(value = "pdfName",required = false) String pdfName
    ) throws JRException, IOException  {

        String  realPath = request.getSession().getServletContext().getRealPath("");
        List<String> otherList = new ArrayList<>();
        Map<String,Object> resultMap =  new HashMap();
        if (StringUtils.isEmpty(pdfName)){
            pdfName = String.valueOf( System.currentTimeMillis() )+".xlsx";
        }else if (!pdfName.contains(".xlsx")){
            pdfName = pdfName+".xlsx";
        }

        File reportsDir = new File(realPath + "/jrxml/MyReports/");
        LocalJasperReportsContext ctx = new LocalJasperReportsContext(DefaultJasperReportsContext.getInstance());
        ctx.setClassLoader(getClass().getClassLoader());
        ctx.setFileResolver(new SimpleFileResolver(reportsDir));//注意，设置JasperReport的相对路径，很重要
        JRDataSource jrDataSource = new JRBeanCollectionDataSource(null);
        JasperFillManager fillManager = JasperFillManager.getInstance(ctx);
        JasperExportManager exportManager = JasperExportManager.getInstance(ctx);

        try {
            //MainReports 主表中的数据
            List<MainReports> mainList = new ArrayList<>();
            MainReport mainReport = new MainReport();
            Object subReports = JsonUtil.mainReport(json,mainReport);
            Map map = new HashMap();
            map.put("name",mainReport.getCustomerID());
            map.put("birth",mainReport.getCustomerBirthDate());
            map.put(JRParameter.REPORT_FILE_RESOLVER, new SimpleFileResolver(reportsDir));
            JasperDesign jDesignMain = JRXmlLoader.load(new File(realPath+"/jrxml/MyReports/MainReports.jrxml"));
            JasperReport jReportMain = JasperCompileManager.compileReport(jDesignMain);
            //jasper数据
            JasperDesign jDesign = null;
            JasperReport jReport = null;
            //循环子列表
            if (!StringUtils.isEmpty( subReports )){
                JSONArray array = JSON.parseArray(subReports.toString());
                for (Object subObject : array) {
                    Map<String,Object> submap = (Map<String, Object>) subObject;
//                    System.out.println("map:"+submap.get("subReportName"));
//                    System.out.println("data"+((Map<String,Object>)submap.get("data")).get("title"));
                    //开始循环如果存在子模板
                    if (!StringUtils.isEmpty(submap.get("subReports"))){
//                    System.out.println(map.get("subReports").toString());
                        if (submap!=null && submap.get("subReportName").equals("AllergyAnalysis")){
                            List<Country> data = new ArrayList();
                            jDesign =  JRXmlLoader.load(new File(realPath+"/jrxml/MyReports/AllergyAnalysis.jrxml"));
                            jReport = JasperCompileManager.compileReport(jDesign);

                            JasperDesign jDesign2 = JRXmlLoader.load(new File(realPath+"/jrxml/MyReports/AllergyAnalysisTable.jrxml"));
                            JasperReport jReport2 = JasperCompileManager.compileReport(jDesign2);
                            List<Map<String,Object>> slist = (List<Map<String, Object>>) submap.get("subReports");
                            for (Map<String,Object> smap :slist){
                                List<DataBean> dataBeanList = new ArrayList();
                                if(smap!=null && smap.get("subReportName").equals("AllergyAnalysisTable")){

                                    List<String> items =  (smap.get("data"))==null?new ArrayList<>():(List<String>)((Map<String,Object>)smap.get("data")).get("items");
                                    List<Integer> values = (smap.get("data"))==null?new ArrayList<>():(List<Integer>) ((Map<String,Object>)smap.get("data")).get("values");
                                    dataBeanList = new ArrayList();
                                    DataBean dataBean = new DataBean();
                                    for (int i = 0;i < items.size();i++){
                                        dataBean = new DataBean();
                                        dataBean.setName(items.get(i));
                                        dataBean.setCountry(values.get(i)+"");
                                        dataBean.setTitle(((Map<String,Object>)smap.get("data")).get("title").toString());
                                        dataBean.setDate(((Map<String,Object>)smap.get("data")).get("date").toString());
                                        dataBeanList.add(dataBean);
                                    }
                                }
                                JRBeanCollectionDataSource ds1 = new JRBeanCollectionDataSource(dataBeanList);
                               // data.add(new Country(1,((Map<String,Object>)submap.get("data")).get("title").toString(),ds1,ds1));

                            }

                            mainList.add(new MainReports(jReport,jReport2,new JRBeanCollectionDataSource(data)));
                        }

                    }else {
                        if (submap!=null && submap.get("subReportName").equals("SectionHeader")){
                            jDesign = JRXmlLoader.load(new File(realPath+"/jrxml/MyReports/SectionHeader.jrxml"));
                            jReport = JasperCompileManager.compileReport(jDesign);
                            JSONObject allergyObject = JSONObject.parseObject(submap.get("data").toString());
                            String title = allergyObject.get("title")==null?"":allergyObject.get("title").toString();
                            String subTitle = allergyObject.get("subTitle")==null?"":allergyObject.get("subTitle").toString();
                            mainList.add(new MainReports(jReport, jReport ,
                                    new JRBeanCollectionDataSource(Arrays.asList(new SectionHeader(title,subTitle)))));

                        }else if (submap!=null && submap.get("subReportName").equals("DiseaseGeneticRiskTable")){
                            jDesign = JRXmlLoader.load(new File(realPath+"/jrxml/MyReports/DiseaseGeneticRiskTable.jrxml"));
                            jReport = JasperCompileManager.compileReport(jDesign);
                            String name = "";
                            String fx = "";
                            List<String> itemList = new ArrayList<>();
                            List<Integer> valueList = new ArrayList<>();
                            List<DiseaseGeneticRiskTable> disList = new ArrayList<>();
                            if (!StringUtils.isEmpty(submap.get("column0"))){
                                JSONObject allergyObject = JSONObject.parseObject(submap.get("column0").toString());
                                name = StringUtils.isEmpty(allergyObject.get("name"))?"":allergyObject.get("name").toString();
                                if(!StringUtils.isEmpty(allergyObject.get("values"))){
                                    JSONArray diseaseArray = JSON.parseArray(allergyObject.get("values").toString());
                                    for (Object object : diseaseArray){
                                        itemList.add( StringUtils.isEmpty(object)?"":object.toString() );
                                    }
                                }
                            }
                            if (!StringUtils.isEmpty(submap.get("column1"))){
                                JSONObject allergyObject = JSONObject.parseObject(submap.get("column1").toString());
                                fx = StringUtils.isEmpty(allergyObject.get("name"))?"":allergyObject.get("name").toString();
                                if(!StringUtils.isEmpty(allergyObject.get("values"))){
                                    JSONArray diseaseArray = JSON.parseArray(allergyObject.get("values").toString());
                                    for (Object object : diseaseArray){
                                        valueList.add( StringUtils.isEmpty(object)?0: (Integer) object);
                                    }
                                }
                            }
                            for (int i = 0;i < itemList.size(); i++){
                                DiseaseGeneticRiskTable table = new DiseaseGeneticRiskTable(name,fx,itemList.get(i),valueList.get(i));
                                disList.add(table);
                            }
                            mainList.add(new MainReports(jReport, jReport ,
                                    new JRBeanCollectionDataSource(disList)));
                        }else if (submap!=null && submap.get("subReportName")!=null
                                && ( submap.get("subReportName").equals("BodyAntiCancerCellAbilityEvaluation") || submap.get("subReportName").equals("FoodFamilyAndPossilableSource")
                                || submap.get("subReportName").equals("microRNACancerObservation") || submap.get("subReportName").equals("NutritionAndToxicityElementalAnalysis")
                                || submap.get("subReportName").equals("HypothalamicPituitaryOvarianEndocrineRegulation") || submap.get("subReportName").equals("HarvardCancerRiskEvaluation")
                                || submap.get("subReportName").equals("BodyAntiCancerCellAbilityEvaluation") || submap.get("subReportName").equals("BodyCancerMarkerScreen")
                                || submap.get("subReportName").equals("CancerInheritanceRiskEvaluation") || submap.get("subReportName").equals("FoodAllergySourceAnalysis")
                        )){
                            jDesign = JRXmlLoader.load(new File(realPath+"/jrxml/MyReports/"+submap.get("subReportName")+".jrxml"));
                            jReport = JasperCompileManager.compileReport(jDesign);
                            mainList.add(new MainReports(jReport, jReport ,
                                    new JRBeanCollectionDataSource(Arrays.asList(""))));
                        }else if (submap!=null && submap.get("subReportName")!=null){
                            otherList.add(submap.get("subReportName").toString());
                        }

                    }
                }
            }
            JasperPrint jasperPrint = fillManager.fillReport(jReportMain, map, new JRBeanCollectionDataSource(mainList));

            JRXlsxExporter exporter = new JRXlsxExporter();
            //设置输入项
            ExporterInput exporterInput = new SimpleExporterInput(jasperPrint);
            exporter.setExporterInput(exporterInput);

            //设置输出项
//            OutputStreamExporterOutput exporterOutput = new SimpleOutputStreamExporterOutput(pdfName);
            OutputStreamExporterOutput exporterOutput = new SimpleOutputStreamExporterOutput(
                    response.getOutputStream());
            exporter.setExporterOutput(exporterOutput);
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-disposition", "attachment; filename="+pdfName);


            exporter.exportReport();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}


