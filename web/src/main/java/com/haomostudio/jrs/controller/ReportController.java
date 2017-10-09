package com.haomostudio.jrs.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.haomostudio.jrs.*;
import com.haomostudio.jrs.common.PropertyConfigurer;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.*;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;
import net.sf.jasperreports.engine.export.JRXmlExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.query.JsonQueryExecuterFactory;
import net.sf.jasperreports.engine.util.LocalJasperReportsContext;
import net.sf.jasperreports.engine.util.SimpleFileResolver;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.export.ExporterInput;
import net.sf.jasperreports.export.OutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.j2ee.servlets.ImageServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

@Controller
public class ReportController {
    private String  date = "";

    @Autowired
    PropertyConfigurer propertyConfigurer;

    @RequestMapping(value = "/injection",
            method = {RequestMethod.POST },
            produces = "application/json;charset=UTF-8")
    @ResponseBody
    public void injection(HttpServletResponse response,HttpServletRequest request,@RequestBody() String body){
        date = body;
    }

    /**
     * 测试用例，生成简单的pdf
     * @param response
     * @throws JRException
     * @throws IOException
     */
    @RequestMapping(value = "/test",
            method = { RequestMethod.GET, RequestMethod.POST },
            produces = "application/json;charset=UTF-8")
    @ResponseBody
    public void printPdf(HttpServletResponse response) throws JRException, IOException {
        //project的web module所在路径
        String filePath = System.getProperty("search.root");
        filePath = filePath.substring(0, filePath.lastIndexOf("/"));
        filePath = filePath.substring(0, filePath.lastIndexOf("/"));
        filePath = filePath.substring(0, filePath.lastIndexOf("/"));

        //获取jrxml
        InputStream inputStream = new FileInputStream(filePath + "/src/main/resources/jrxml/MyReports/MainReports.jrxml");
        JasperDesign jasperDesign = JRXmlLoader.load(inputStream);

        //在main里添加subreport
        InputStream inputStreamSub = new FileInputStream(filePath + "/src/main/resources/jrxml/MyReports/subreport_A4.jrxml");
        JasperDesign subJasperDesign = JRXmlLoader.load(inputStreamSub);
        JRDesignBand jrDesignBand = new JRDesignBand();
        JRDesignSubreport jrDesignSubreport = new JRDesignSubreport(subJasperDesign);

        //设置connectionExpression
        JRDesignExpression connectExp = new JRDesignExpression("$P{REPORT_CONNECTION}");
        jrDesignSubreport.setConnectionExpression(connectExp);

        //设置subreportExpression
        JasperCompileManager.compileReportToFile(subJasperDesign, filePath + "/src/main/resources/jrxml/MyReports/subreport_A4.jasper");//编译到本地
        JRDesignExpression subreportExp = new JRDesignExpression("\"subreport_A4.jasper\"");//添加subreportExpression
        jrDesignSubreport.setExpression(subreportExp);

        jrDesignBand.addElement(jrDesignSubreport);
        ((JRDesignSection)jasperDesign.getDetailSection()).addBand(jrDesignBand);

        //编译
        JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);

        //输出jrxml，测试用
//        JRXmlWriter.writeReport(jasperReport, filePath + "/src/main/resources/jrxml/MyReports/MainReports_gks.jrxml", "UTF-8");

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

        //获取JasperPrint
        JasperPrint jasperPrint = fillManager.fill(jasperReport, params, new JREmptyDataSource());

        //返回输出流
        final OutputStream outStream = response.getOutputStream();
        String fileName = "test.pdf";
        response.addHeader("Content-Disposition", "filename=" + fileName);
        exportManager.exportToPdfStream(jasperPrint, outStream);
    }


    @RequestMapping(value = "/jspYD",
            method = {RequestMethod.GET},
            produces = "application/json;charset=UTF-8")
    @ResponseBody
    public void groupVoid11(HttpServletResponse response,HttpServletRequest request
    ) throws JRException, IOException  {
        JSONObject object = JSON.parseObject(date);
        String json = object.getString("json");
        String name = object.getString("name");
        String nameYD = object.getString("fileName");
        try {
            String  realPath = request.getSession().getServletContext().getRealPath("");
            String pdfName ="";
            //XML文件保存的路径   以及  生成的pdf存放的路径
            String xml_save_path = propertyConfigurer.getProperty("xml_save_path");
            String pdf_export_path =  propertyConfigurer.getProperty("pdf_export_path");
            if (StringUtils.isEmpty(xml_save_path)){
                xml_save_path = realPath + "/jrxml/MyReports/";
            }
            if (StringUtils.isEmpty(pdfName)){
                pdfName = String.valueOf( System.currentTimeMillis() )+".pdf";
            }else if (!pdfName.contains(".pdf")){
                pdfName = pdfName+".pdf";
            }

            //get *.jrxml
            JasperDesign jDesignMain = JRXmlLoader.load(new File(xml_save_path+nameYD+".jrxml"));

            //get *.jasper
            JasperReport jReportMain = JasperCompileManager.compileReport(jDesignMain);


            File reportsDir = new File(xml_save_path);
            LocalJasperReportsContext ctx = new LocalJasperReportsContext(DefaultJasperReportsContext.getInstance());
            ctx.setClassLoader(getClass().getClassLoader());
            ctx.setFileResolver(new SimpleFileResolver(reportsDir));//注意，设置JasperReport的相对路径，很重要
            JRDataSource jrDataSource = new JRBeanCollectionDataSource(null);
            JasperFillManager fillManager = JasperFillManager.getInstance(ctx);
            JasperExportManager exportManager = JasperExportManager.getInstance(ctx);



            InputStream is=new ByteArrayInputStream(json.getBytes("UTF-8"));
            HashMap<String, Object> paramsMap = new HashMap<String, Object>();
            paramsMap.put("net.sf.jasperreports.json.source", json);
            paramsMap.put("JSON_INPUT_STREAM", is);
            paramsMap.put(JsonQueryExecuterFactory.JSON_LOCALE, Locale.ENGLISH);
            paramsMap.put(JRParameter.REPORT_LOCALE, Locale.US);
            // compile the report

            // fill it with our data
            JasperPrint print = fillManager.fillReport(jReportMain, paramsMap);

            // view the report with the built-in viewer


            if (!StringUtils.isEmpty( name ) && name.substring(name.lastIndexOf(".")+1).equals("docx")){

                JRDocxExporter exporter=new JRDocxExporter();
                //设置输入项
                ExporterInput exporterInput = new SimpleExporterInput(print);
                exporter.setExporterInput(exporterInput);

                //设置输出项
//            OutputStreamExporterOutput exporterOutput = new SimpleOutputStreamExporterOutput(pdfName);
                OutputStreamExporterOutput exporterOutput = new SimpleOutputStreamExporterOutput(
                        response.getOutputStream());
                exporter.setExporterOutput(exporterOutput);
//                String fileName = "test.docx";
                response.setContentType("application/vnd.ms-excel");
                response.setHeader("Content-disposition", "attachment; filename="+name);


                exporter.exportReport();
            }else if (!StringUtils.isEmpty( name )  && name.substring(name.lastIndexOf(".")+1).equals("html")){
                response.setContentType("text/html; charset=GBK");
                PrintWriter writer=response.getWriter();
                JRHtmlExporter exporter=new JRHtmlExporter();
                request.getSession().setAttribute(ImageServlet.DEFAULT_JASPER_PRINT_SESSION_ATTRIBUTE, print);
                exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
                exporter.setParameter(JRExporterParameter.OUTPUT_WRITER, writer);
                //报表边框图片设置"report/image?image="，report为你的报表及PX图片所在目录
                //exporter.setParameter(JRHtmlExporterParameter.IMAGES_URI, "report/image?image=");
                //报表边框图片设置 IS_USING_IMAGES_TO_ALIGN,Boolean.FALSE，不使用图片
                exporter.setParameter(JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN,Boolean.FALSE);
                exporter.setParameter(JRExporterParameter.CHARACTER_ENCODING, "GBK");
                //导出
                exporter.exportReport();

            }else if (!StringUtils.isEmpty( name ) && name.substring(name.lastIndexOf(".")+1).equals("xml")){
//                JasperPrint jasperPrint=new JasperPrintWithJRDataSourceNew(jrxmlFilePath,reportFilePath,params,dataSource).getJasperPrint();
                JRXmlExporter xmlExporter=new JRXmlExporter();
                xmlExporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
                xmlExporter.setParameter(JRExporterParameter.OUTPUT_STREAM, response.getOutputStream());
                xmlExporter.exportReport();
                response.setContentType("text/xml; charset=GBK");
            }else if (!StringUtils.isEmpty( name ) && name.substring(name.lastIndexOf(".")+1).equals("xlsx")){
                JRXlsxExporter exporter = new JRXlsxExporter();
                //设置输入项
                ExporterInput exporterInput = new SimpleExporterInput(print);
                exporter.setExporterInput(exporterInput);
                //设置输出项
//            OutputStreamExporterOutput exporterOutput = new SimpleOutputStreamExporterOutput(pdfName);
                OutputStreamExporterOutput exporterOutput = new SimpleOutputStreamExporterOutput(
                        response.getOutputStream());
                exporter.setExporterOutput(exporterOutput);
//                String fileName = "test.xlsx";
                response.setContentType("application/vnd.ms-excel");
                response.setHeader("Content-disposition", "attachment; filename="+name);
                exporter.exportReport();

            }else  if (!StringUtils.isEmpty( name ) && name.substring(name.lastIndexOf(".")+1).equals("pdf")){
                final OutputStream outStream = response.getOutputStream();
//                String fileName = "test.pdf";
                response.addHeader("Content-Disposition", "filename=" + name);
                exportManager.exportToPdfStream(print, outStream);
            }else {
                final OutputStream outStream = response.getOutputStream();
//                String fileName = "test.pdf";
                response.addHeader("Content-Disposition", "filename=" + pdfName);
                exportManager.exportToPdfStream(print, outStream);
            }

        } catch (JRException e) {
            e.printStackTrace();
        }finally {
            date = "";
        }
    }



}


