package com.haomostudio.jrs.controller;

import com.haomostudio.jrs.Country;
import com.haomostudio.jrs.Data;
import com.haomostudio.jrs.DataBean;
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

        InputStream inputStream = new FileInputStream("/Users/liuranran/work/jasperreportserver/web/src/main/resources/jrxml/MyReports/mainreport.jrxml");
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


    public List<DataBean> getDataBeanList() {
        ArrayList<DataBean> dataBeanList = new ArrayList();

        dataBeanList.add(new DataBean("Manisha", "0"));
        dataBeanList.add(new DataBean("Dennis Ritchie", "1"));
        dataBeanList.add(new DataBean("V.Anand", "2"));
        dataBeanList.add(new DataBean("Shrinath", "3"));

        return dataBeanList;
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
        JasperDesign jDesign = JRXmlLoader.load(new File("/Users/liuranran/work/jasperreportserver/web/src/main/resources/jrxml/MyReports/AllergyAnalysis.jrxml"));
        JasperDesign jDesign2 = JRXmlLoader.load(new File("/Users/liuranran/work/jasperreportserver/web/src/main/resources/jrxml/MyReports/AllergyAnalysisTable.jrxml"));

        //get *.jasper
        JasperReport jReport = JasperCompileManager.compileReport(jDesign);
        JasperReport jReport2 = JasperCompileManager.compileReport(jDesign2);

        File reportsDir = new File(filePath + "/webapps/jrs/WEB-INF/classes/jrxml/MyReports/");
        //        params.put(JRParameter.REPORT_FILE_RESOLVER, new SimpleFileResolver(reportsDir)); //deprecated
        LocalJasperReportsContext ctx = new LocalJasperReportsContext(DefaultJasperReportsContext.getInstance());
        ctx.setClassLoader(getClass().getClassLoader());
        ctx.setFileResolver(new SimpleFileResolver(reportsDir));//注意，设置JasperReport的相对路径，很重要
        JRDataSource jrDataSource = new JRBeanCollectionDataSource(null);
        JasperFillManager fillManager = JasperFillManager.getInstance(ctx);
        JasperExportManager exportManager = JasperExportManager.getInstance(ctx);

        //fill data
        List data = new ArrayList();

        List<DataBean> list1 = getDataBeanList();
        List<DataBean> list2 = getDataBeanList();
//        List<DataBean> list3 = getDataBeanList();
//
        JRBeanCollectionDataSource ds1 = new JRBeanCollectionDataSource(list1);
        JRBeanCollectionDataSource ds2 = new JRBeanCollectionDataSource(list2);
//        JRBeanCollectionDataSource ds3 = new JRBeanCollectionDataSource(list3);
//
        data.add(new Country(1 , "China" ,  ds1));
        data.add(new Country(2 , "USA" , ds2));
//        data.add(new Country(3 , "UK" , ds3));

        Map map = new HashMap();
        map.put("subReportObject", jReport2);//subReport put here.

        JasperPrint jasperPrint = JasperFillManager.fillReport(jReport, map, new JRBeanCollectionDataSource(data));

        //export with pdf
        //返回输出流
        final OutputStream outStream = response.getOutputStream();
        String fileName = "test.pdf";
        response.addHeader("Content-Disposition", "filename=" + fileName);
        exportManager.exportToPdfStream(jasperPrint, outStream);
    }

}


