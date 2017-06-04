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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

@Controller
public class ReportController {

    @Autowired
    PropertyConfigurer propertyConfigurer;
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


}


