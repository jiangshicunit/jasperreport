package com.haomostudio.jrs.controller;

import com.haomostudio.jrs.*;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.util.LocalJasperReportsContext;
import net.sf.jasperreports.engine.util.SimpleFileResolver;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

@Controller
public class SaperReportController {

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
    public void printPdf(HttpServletResponse response) throws JRException, IOException {
        //project的web module所在路径
        String filePath = System.getProperty("search.root");
        filePath = filePath.substring(0, filePath.lastIndexOf("/"));
        filePath = filePath.substring(0, filePath.lastIndexOf("/"));
        filePath = filePath.substring(0, filePath.lastIndexOf("/"));

        //获取jrxml并编译/Users/liuranran/work/test1.jrxml
//        InputStream inputStream = new FileInputStream(filePath + "/src/main/resources/jrxml/MyReports/MainReports.jrxml");
//        InputStream inputStream = new FileInputStream(filePath + "/webapps/jrs/WEB-INF/classes/jrxml/MyReports/test.jrxml");

        InputStream inputStream = new FileInputStream("/Users/liuranran/work/jasperreportserver/web/src/main/resources/jrxml/MyReports/SectionHeader.jrxml");
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
        List<DataBean> dataList = getDataBeanList();
        JRBeanCollectionDataSource beanColDataSource =
                new JRBeanCollectionDataSource(dataList);

        //获取JasperPrint
//        JasperPrint jasperPrint = fillManager.fill(jasperReport, params, new JREmptyDataSource());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,params, new JRBeanCollectionDataSource(Arrays.asList(new SectionHeader("糖尿病","糖尿病详情"),new SectionHeader("高血压","高血压详情"))));
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
        List<DataBean> dataList = getDataBeanList();
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
    public void groupVoid1(HttpServletResponse response) throws JRException, IOException  {

        //project的web module所在路径
        String filePath = System.getProperty("search.root");
        filePath = filePath.substring(0, filePath.lastIndexOf("/"));
        filePath = filePath.substring(0, filePath.lastIndexOf("/"));
        filePath = filePath.substring(0, filePath.lastIndexOf("/"));

        //get *.jrxml
        JasperDesign jDesignMain = JRXmlLoader.load(new File("/Users/liuranran/work/jasperreportserver/web/src/main/resources/jrxml/MyReports/MainReports.jrxml"));
        JasperDesign jDesign = JRXmlLoader.load(new File("/Users/liuranran/work/jasperreportserver/web/src/main/resources/jrxml/MyReports/AllergyAnalysis.jrxml"));
        JasperDesign jDesign2 = JRXmlLoader.load(new File("/Users/liuranran/work/jasperreportserver/web/src/main/resources/jrxml/MyReports/AllergyAnalysisTable.jrxml"));
        JasperDesign jDesignSection = JRXmlLoader.load(new File("/Users/liuranran/work/jasperreportserver/web/src/main/resources/jrxml/MyReports/SectionHeader.jrxml"));

        //get *.jasper
        JasperReport jReportMain = JasperCompileManager.compileReport(jDesignMain);
        JasperReport jReport = JasperCompileManager.compileReport(jDesign);
        JasperReport jReport2 = JasperCompileManager.compileReport(jDesign2);
        JasperReport jReportSection = JasperCompileManager.compileReport(jDesignSection);


        File reportsDir = new File(filePath + "/webapps/jrs/WEB-INF/classes/jrxml/MyReports/");
        //        params.put(JRParameter.REPORT_FILE_RESOLVER, new SimpleFileResolver(reportsDir)); //deprecated
        LocalJasperReportsContext ctx = new LocalJasperReportsContext(DefaultJasperReportsContext.getInstance());
        ctx.setClassLoader(getClass().getClassLoader());
        ctx.setFileResolver(new SimpleFileResolver(reportsDir));//注意，设置JasperReport的相对路径，很重要
        JRDataSource jrDataSource = new JRBeanCollectionDataSource(null);
        JasperFillManager fillManager = JasperFillManager.getInstance(ctx);
        JasperExportManager exportManager = JasperExportManager.getInstance(ctx);

        //fill data
        List<MainReports> mainList = new ArrayList<>();
        List<Country> data = new ArrayList();

        List<DataBean> list1 = getDataBeanList();
        List<DataBean> list2 = getDataBeanList1();
        List<DataBean> list3 = getDataBeanList2();
//
        JRBeanCollectionDataSource ds1 = new JRBeanCollectionDataSource(list1);
        JRBeanCollectionDataSource ds2 = new JRBeanCollectionDataSource(list2);
        JRBeanCollectionDataSource ds3 = new JRBeanCollectionDataSource(list3);

//        JRBeanCollectionDataSource ds3 = new JRBeanCollectionDataSource(list3);
//
//        data.add(new Country(1 , "China" ,  ds1));
//        data.add(new Country(2 , "USA" , ds2));
//        data.add(new Country(3 , "UK" , ds3));
//        JRBeanCollectionDataSource dsMain = new JRBeanCollectionDataSource(data);
        mainList.add(new MainReports(jReportSection, jReport2 ,new JRBeanCollectionDataSource(Arrays.asList(new SectionHeader("禁忌","")))));
        mainList.add(new MainReports(jReport,jReport2,new JRBeanCollectionDataSource(Arrays.asList(new Country(1 , "China" ,  ds1),new Country(2 , "USA" , ds2),new Country(3 , "USA" , ds3)))));

        Map map = new HashMap();
        map.put("name","123333");
        map.put("birth","1979-02-09");
//        JasperPrint jasperPrint = fillManager.fill(jReport, params, new JREmptyDataSource());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jReportMain, map, new JRBeanCollectionDataSource(mainList));

        //export with pdf
        //返回输出流
        final OutputStream outStream = response.getOutputStream();
        String fileName = "test.pdf";
        response.addHeader("Content-Disposition", "filename=" + fileName);
        exportManager.exportToPdfStream(jasperPrint, outStream);
    }

    @RequestMapping(value = "/test2",
            method = { RequestMethod.GET, RequestMethod.POST },
            produces = "application/json;charset=UTF-8")
    @ResponseBody
    public void groupVoid2(HttpServletResponse response) throws JRException, IOException  {

        //project的web module所在路径
        String filePath = System.getProperty("search.root");
        filePath = filePath.substring(0, filePath.lastIndexOf("/"));
        filePath = filePath.substring(0, filePath.lastIndexOf("/"));
        filePath = filePath.substring(0, filePath.lastIndexOf("/"));

        //get *.jrxml
        JasperDesign jDesignMain = JRXmlLoader.load(new File("/Users/liuranran/work/jasperreportserver/web/src/main/resources/jrxml/MyReports/MainReports.jrxml"));
        JasperDesign jDesign = JRXmlLoader.load(new File("/Users/liuranran/work/jasperreportserver/web/src/main/resources/jrxml/MyReports/AllergyAnalysis.jrxml"));
        JasperDesign jDesign2 = JRXmlLoader.load(new File("/Users/liuranran/work/jasperreportserver/web/src/main/resources/jrxml/MyReports/AllergyAnalysisTable1.jrxml"));
        JasperDesign jDesignSection = JRXmlLoader.load(new File("/Users/liuranran/work/jasperreportserver/web/src/main/resources/jrxml/MyReports/SectionHeader.jrxml"));

        //get *.jasper
        JasperReport jReportMain = JasperCompileManager.compileReport(jDesignMain);
        JasperReport jReport = JasperCompileManager.compileReport(jDesign);
        JasperReport jReport2 = JasperCompileManager.compileReport(jDesign2);
        JasperReport jReportSection = JasperCompileManager.compileReport(jDesignSection);


        File reportsDir = new File(filePath + "/webapps/jrs/WEB-INF/classes/jrxml/MyReports/");
        //        params.put(JRParameter.REPORT_FILE_RESOLVER, new SimpleFileResolver(reportsDir)); //deprecated
        LocalJasperReportsContext ctx = new LocalJasperReportsContext(DefaultJasperReportsContext.getInstance());
        ctx.setClassLoader(getClass().getClassLoader());
        ctx.setFileResolver(new SimpleFileResolver(reportsDir));//注意，设置JasperReport的相对路径，很重要
        JRDataSource jrDataSource = new JRBeanCollectionDataSource(null);
        JasperFillManager fillManager = JasperFillManager.getInstance(ctx);
        JasperExportManager exportManager = JasperExportManager.getInstance(ctx);

        //fill data
        List<MainReports> mainList = new ArrayList<>();
        List<Country> data = new ArrayList();

//        List<DataBean> list1 = getDataBeanList();
//        List<DataBean> list2 = getDataBeanList();
//        List<DataBean> list3 = getDataBeanList();
//
        JRBeanCollectionDataSource ds1 = new JRBeanCollectionDataSource(Arrays.asList(
                new AllergyAnalysisTable("奶蛋类","15/2/18",Arrays.asList("牛奶","羊奶", "切达奶酪","酸奶","蛋白", "蛋黄"),Arrays.asList("0","0", "0", "1", "0", "0"))
        ));
        JRBeanCollectionDataSource ds2 = new JRBeanCollectionDataSource(Arrays.asList(
                new AllergyAnalysisTable("其他类","15/2/18",Arrays.asList("辣椒","酵母", "胡椒","咖啡","茶", "香草", "蜂蜜", "生姜"),Arrays.asList("0", "0", "0", "0", "0", "1", "2", "3"))
        ));
        JRBeanCollectionDataSource ds3 = new JRBeanCollectionDataSource(Arrays.asList(
                new AllergyAnalysisTable("蔬菜类","15/2/18",Arrays.asList("芦笋","蘑菇", "番茄","芋头","四季豆", "菠菜"),Arrays.asList("0", "0", "0", "2", "0", "1"))
        ));
        mainList.add(new MainReports(jReportSection, jReport2 ,new JRBeanCollectionDataSource(Arrays.asList(new SectionHeader("禁忌","")))));
        mainList.add(new MainReports(jReport,jReport2,new JRBeanCollectionDataSource(Arrays.asList(new Country(1 , "China" ,  ds1),new Country(1 , "China" ,  ds1),new Country(1 , "China" ,  ds1)))));

        Map map = new HashMap();
        map.put("name","123333");
        map.put("birth","1979-02-09");
//        JasperPrint jasperPrint = fillManager.fill(jReport, params, new JREmptyDataSource());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jReportMain, map, new JRBeanCollectionDataSource(mainList));

        //export with pdf
        //返回输出流
        final OutputStream outStream = response.getOutputStream();
        String fileName = "test.pdf";
        response.addHeader("Content-Disposition", "filename=" + fileName);
        exportManager.exportToPdfStream(jasperPrint, outStream);
    }


    public List<DataBean> getDataBeanList() {
        ArrayList<DataBean> dataBeanList = new ArrayList();

        dataBeanList.add(new DataBean("牛奶", "0"));
        dataBeanList.add(new DataBean("羊奶", "0"));
        dataBeanList.add(new DataBean("切达奶酪", "0"));
        dataBeanList.add(new DataBean("酸奶", "1"));
        dataBeanList.add(new DataBean("蛋白", "0"));
        dataBeanList.add(new DataBean("蛋黄", "1"));
        return dataBeanList;
    }

    public List<DataBean> getDataBeanList1() {
        ArrayList<DataBean> dataBeanList = new ArrayList();

        dataBeanList.add(new DataBean("辣椒", "0"));
        dataBeanList.add(new DataBean("酵母", "0"));
        dataBeanList.add(new DataBean("胡椒", "0"));
        dataBeanList.add(new DataBean("咖啡", "0"));
        dataBeanList.add(new DataBean("茶", "0"));
        dataBeanList.add(new DataBean("香草", "1"));
        dataBeanList.add(new DataBean("蜂蜜", "2"));
        dataBeanList.add(new DataBean("生姜", "3"));
        return dataBeanList;
    }

    public List<DataBean> getDataBeanList2() {
        ArrayList<DataBean> dataBeanList = new ArrayList();

        dataBeanList.add(new DataBean("芦笋", "0"));
        dataBeanList.add(new DataBean("蘑菇", "0"));
        dataBeanList.add(new DataBean("番茄", "0"));
        dataBeanList.add(new DataBean("芋头", "2"));
        dataBeanList.add(new DataBean("四季豆", "0"));
        dataBeanList.add(new DataBean("菠菜", "1"));
        return dataBeanList;
    }
}


