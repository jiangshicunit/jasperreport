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
import net.sf.jasperreports.engine.export.JRXmlExporterContext;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.query.JsonQueryExecuterFactory;
import net.sf.jasperreports.engine.util.JRXmlWriteHelper;
import net.sf.jasperreports.engine.util.LocalJasperReportsContext;
import net.sf.jasperreports.engine.util.SimpleFileResolver;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.engine.xml.JRXmlWriter;
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
//                                DiseaseGeneticRiskTable table = new DiseaseGeneticRiskTable(name,fx,itemList.get(i),valueList.get(i));
//                                disList.add(table);
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
//                                DiseaseGeneticRiskTable table = new DiseaseGeneticRiskTable(name,fx,itemList.get(i),valueList.get(i));
//                                disList.add(table);
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


    @RequestMapping(value = "/testgroup1",
            method = { RequestMethod.GET, RequestMethod.POST },
            produces = "application/json;charset=UTF-8")
    @ResponseBody
    public void groupVoid10(HttpServletResponse response,HttpServletRequest request
//                           @RequestParam(value="json", required = false) String json1,
//                           @RequestBody() String json
    ) throws JRException, IOException  {



        String  realPath = request.getSession().getServletContext().getRealPath("");
        //project的web module所在路径
        String filePath = System.getProperty("search.root");
        filePath = filePath.substring(0, filePath.lastIndexOf("/"));

        //get *.jrxml
        JasperDesign jDesignMain = JRXmlLoader.load(new File(realPath+"/jrxml/MyReports/MainReports.jrxml"));

        //get *.jasper
        JasperReport jReportMain = JasperCompileManager.compileReport(jDesignMain);


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

        JasperDesign jDesignde = JRXmlLoader.load(new File(realPath+"/jrxml/MyReports/EvaluationDetail.jrxml"));

//        //get *.jasper
        JasperReport jReportde = JasperCompileManager.compileReport(jDesignde);

        JasperDesign jDesignde1 = JRXmlLoader.load(new File(realPath+"/jrxml/MyReports/EvaluationReport.jrxml"));

//        //get *.jasper
        JasperReport jReportde1 = JasperCompileManager.compileReport(jDesignde1);
        mainList.add(new MainReports(jReportde1,jReportde1,new JRBeanCollectionDataSource(
                Arrays.asList(new EvaluationReport( "以下是您的男性荷尔蒙健康评估报告",
                        new JRBeanCollectionDataSource(
                                Arrays.asList(new EvaluationDetail("脑下垂体荷尔蒙","黄体刺激素(LH)","由脑下垂体分泌，并受下视丘所释放的GnRH控制，能刺激睪丸Leydig细胞制造并分泌睪固酮。组织细胞及循环血液若有充足的睪固酮量，则会对脑下垂体产生负回馈(negative--feed-back)作用，不再分泌LH刺激睪丸。反之，如果组织细胞及循环血液若有不足的睪固酮量，则会分泌更多的LH刺激睪丸产生睪固酮。"),
                                        new EvaluationDetail("脑下垂体荷尔蒙","滤泡刺激素(FSH)","由脑下垂体分泌，并受下视丘所释放的GnRH 控制，能刺激睪丸Sertoli 细胞制造精子。"),
                                        new EvaluationDetail("脑下垂体荷尔蒙","黄体刺激素(LH)","由脑下垂体分泌，并受下视丘所释放的GnRH控制，能刺激睪丸Leydig细胞制造并分泌睪固酮。组织细胞及循环血液若有充足的睪固酮量，则会对脑下垂体产生负回馈(negative--feed-back)作用，不再分泌LH刺激睪丸。反之，如果组织细胞及循环血液若有不足的睪固酮量，则会分泌更多的LH刺激睪丸产生睪固酮。"),
                                        new EvaluationDetail("脑下垂体荷尔蒙","黄体刺激素(LH)","由脑下垂体分泌，并受下视丘所释放的GnRH控制，能刺激睪丸Leydig细胞制造并分泌睪固酮。组织细胞及循环血液若有充足的睪固酮量，则会对脑下垂体产生负回馈(negative--feed-back)作用，不再分泌LH刺激睪丸。反之，如果组织细胞及循环血液若有不足的睪固酮量，则会分泌更多的LH刺激睪丸产生睪固酮。")

                                )),jReportde

                )) ) ));


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

    @RequestMapping(value = "/srm",
            method = { RequestMethod.GET, RequestMethod.POST },
            produces = "application/json;charset=UTF-8")
    @ResponseBody
    public void groupVoid11(HttpServletResponse response,HttpServletRequest request,
                            @RequestParam(value="json") String json,
                            @RequestParam(value="name") String name
//                           @RequestBody() String json
    ) throws JRException, IOException  {
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
            JasperDesign jDesignMain = JRXmlLoader.load(new File(xml_save_path+"caigouhetong.jrxml"));

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
        }
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
}


