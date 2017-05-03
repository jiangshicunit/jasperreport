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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

/**
 * Created by shidaizhoukan on 2017/5/1.
 */
@Controller
public class TestController {

    /**
     * 测试用例，根据传入的参数生成PDF
     * @param response
     * @throws JRException
     * @throws IOException
     */
    @RequestMapping(value = "/testPDF",
            method = { RequestMethod.GET, RequestMethod.POST },
            produces = "application/json;charset=UTF-8")
    @ResponseBody
    public void printPdf(HttpServletResponse response, HttpServletRequest request) throws JRException, IOException {
        String  realPath = request.getSession().getServletContext().getRealPath("");
        //project的web module所在路径

        //获取jrxml并编译/Users/liuranran/work/test1.jrxml
//        InputStream inputStream = new FileInputStream(filePath + "/src/main/resources/jrxml/MyReports/MainReports.jrxml");
//        InputStream inputStream = new FileInputStream(filePath + "/webapps/jrs/WEB-INF/classes/jrxml/MyReports/test.jrxml");

        InputStream inputStream = new FileInputStream(realPath+"/jrxml/MyReports/Test.jrxml");
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
//        List<DataBean> dataList = getDataBeanList();
//        List<SuggestionReport> dataList = getDataBeanList();

        //获取JasperPrint
//        JasperPrint jasperPrint = fillManager.fill(jasperReport, params, new JREmptyDataSource());
        JasperPrint jasperPrint = fillManager.fillReport(jasperReport,params, new JRBeanCollectionDataSource(
                Arrays.asList(
                        new SuggestionReport("糖尿病","糖尿病详情")
                )));
        //返回输出流
        final OutputStream outStream = response.getOutputStream();
        String fileName = "test.pdf";
        response.addHeader("Content-Disposition", "filename=" + fileName);
        exportManager.exportToPdfStream(jasperPrint, outStream);
    }


    /**
     * 测试用例，根据传入的参数生成PDF
     * @param response
     * @throws JRException
     * @throws IOException
     */
    @RequestMapping(value = "/pdfTest1",
            method = { RequestMethod.GET, RequestMethod.POST },
            produces = "application/json;charset=UTF-8")
    @ResponseBody
    public void printPdfTest1(HttpServletResponse response,HttpServletRequest request) throws JRException, IOException {
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
//        List<Country> data = new ArrayList();
//
//        data.add(new Country(1,"过敏",new JRBeanCollectionDataSource(county1List),
//                new JRBeanCollectionDataSource(county2List),jasperReport1,jasperReport2));
//        List<SuggestionReport> dataList = getDataBeanList();

        //获取JasperPrint
//        JasperPrint jasperPrint = fillManager.fill(jasperReport, params, new JREmptyDataSource());
        JasperPrint jasperPrint = fillManager.fillReport(jasperReport,params, new JRBeanCollectionDataSource(
                Arrays.asList("")));
        //返回输出流
        final OutputStream outStream = response.getOutputStream();
        String fileName = "test.pdf";
        response.addHeader("Content-Disposition", "filename=" + fileName);
        exportManager.exportToPdfStream(jasperPrint, outStream);
    }


    /**
     * 测试用例，根据传入的参数生成PDF
     * @param response
     * @throws JRException
     * @throws IOException
     */
    @RequestMapping(value = "/pdfTest",
            method = { RequestMethod.GET, RequestMethod.POST },
            produces = "application/json;charset=UTF-8")
    @ResponseBody
    public void printPdfTest(HttpServletResponse response,HttpServletRequest request) throws JRException, IOException {
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
//        List<Country> data = new ArrayList();
//
//        data.add(new Country(1,"过敏",new JRBeanCollectionDataSource(county1List),
//                new JRBeanCollectionDataSource(county2List),jasperReport1,jasperReport2));
//        List<SuggestionReport> dataList = getDataBeanList();

        //获取JasperPrint
//        JasperPrint jasperPrint = fillManager.fill(jasperReport, params, new JREmptyDataSource());
        JasperDesign jasperDesign1 = JRXmlLoader.load(realPath+"/jrxml/MyReports/LaboratoryTest.jrxml");
        JasperReport jasperReport1 = JasperCompileManager.compileReport(jasperDesign1);

        JasperDesign jasperDesign2 = JRXmlLoader.load(realPath+"/jrxml/MyReports/LaboratoryTest1.jrxml");
        JasperReport jasperReport2 = JasperCompileManager.compileReport(jasperDesign2);


//        JasperPrint jasperPrint = fillManager.fillReport(jasperReport1,params,
//                new JRBeanCollectionDataSource(Arrays.asList(new LaboratoryTest(new JRBeanCollectionDataSource(
//                        Arrays.asList(new LaboratoryTest( new JRBeanCollectionDataSource(
//                                Arrays.asList( new LaboratoryTestTable("维生素B族缺乏","15/2/28","Citrate","2-甲基马尿酸","9","6.5","7","ml"),
//                                        new LaboratoryTestTable("维生素B族缺乏","15/2/28","Citrate","2-甲基马尿酸","7","2","3","ml"),
//                                        new LaboratoryTestTable("维生素B族缺乏","15/2/28","Citrate","2-甲基马尿酸","12","6.5","8","ml"),
//                                        new LaboratoryTestTable("维生素B族缺乏","15/2/28","Citrate","2-甲基马尿酸","12.5","6.5","9","ml"),
//                                        new LaboratoryTestTable("维生素B族缺乏","15/2/28","Citrate","2-甲基马尿酸","13.5","6.5","9","ml")
//                                )),jasperReport ))
//                ),jasperReport2)))
//
//                );
//        List<LaboratoryTest> laboratoryTests = new ArrayList<>();
//        List<LaboratoryTest> laboratoryTests1 = new ArrayList<>();
//        laboratoryTests1.add(new LaboratoryTest(new JRBeanCollectionDataSource(
////                Arrays.asList( new LaboratoryTestTable("维生素B族缺乏","15/2/28","Citrate","2-甲基马尿酸","9","6.5","7","ml"),
////                        new LaboratoryTestTable("维生素B族缺乏","15/2/28","Citrate","2-甲基马尿酸","7","2","3","ml"),
////                        new LaboratoryTestTable("维生素B族缺乏","15/2/28","Citrate","2-甲基马尿酸","12","6.5","8","ml"),
////                        new LaboratoryTestTable("维生素B族缺乏","15/2/28","Citrate","2-甲基马尿酸","12.5","6.5","9","ml"),
////                        new LaboratoryTestTable("维生素B族缺乏","15/2/28","Citrate","2-甲基马尿酸","13.5","6.5","9","ml")
//                )),jasperReport));
//        laboratoryTests.add(new LaboratoryTest(new JRBeanCollectionDataSource(laboratoryTests1),jasperReport2));
//        JasperPrint jasperPrint = fillManager.fillReport(jasperReport1,params,
//                new JRBeanCollectionDataSource(laboratoryTests)
//                );
        //返回输出流

//        String fileName = "test.pdf";
//        FileOutputStream outs =new FileOutputStream(realPath+"/pdf/"+fileName);
//        exportManager.exportToPdfStream(jasperPrint, outs);
    }

}
