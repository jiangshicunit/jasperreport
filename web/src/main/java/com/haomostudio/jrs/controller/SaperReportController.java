package com.haomostudio.jrs.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.haomostudio.jrs.*;
import com.haomostudio.jrs.common.PropertyConfigurer;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.util.LocalJasperReportsContext;
import net.sf.jasperreports.engine.util.SimpleFileResolver;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;



import net.sf.jasperreports.engine.JRAbstractExporter;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterParameter;

@Controller
public class SaperReportController {

    @Autowired
    PropertyConfigurer propertyConfigurer;

    @RequestMapping(value = "/yd",
            method = {RequestMethod.POST},
            produces = "application/json;carset=UTF-8")
    @ResponseBody
    public Object testStatic(HttpServletResponse response,HttpServletRequest request,
                             @RequestParam(value = "name1",required = false)String name,
                             @RequestParam(value = "path",required = false)String path){
        Map<String,Object> resultMap = new HashMap<String,Object>();
        String  realPath = request.getSession().getServletContext().getRealPath("");
        //创建文件夹
        File reportsDir = new File(realPath);
        LocalJasperReportsContext ctx = new LocalJasperReportsContext(DefaultJasperReportsContext.getInstance());
        ctx.setClassLoader(getClass().getClassLoader());
        ctx.setFileResolver(new SimpleFileResolver(reportsDir));//注意，设置JasperReport的相对路径，很重要
        JRDataSource jrDataSource = new JRBeanCollectionDataSource(null);
        JasperFillManager fillManager = JasperFillManager.getInstance(ctx);
        JasperExportManager exportManager = JasperExportManager.getInstance(ctx);
        resultMap.put("名称：",name);
        resultMap.put("路径：",path);
        resultMap.put("服务器相对路径",realPath);
        Map parameters=new HashMap();
        ByteArrayOutputStream outPut=new ByteArrayOutputStream();
        FileOutputStream outputStream=null;
        String reportModelFile=realPath+"/jrxml/MyReports/test1.jasper";
        File outPdfFile =  new File(realPath+"ydpdf/");
        if(!outPdfFile.exists()){
            outPdfFile.mkdirs();
        }
        try {

            JasperPrint jasperPrint=fillManager.fillReport(reportModelFile,
                    parameters,new JREmptyDataSource());
            FileOutputStream outs = new FileOutputStream(realPath+"ydpdf/"+name+".pdf");
            exportManager.exportToPdfStream(jasperPrint, outs);
        } catch (JRException e) {
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                outPut.flush();
                outPut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return resultMap;
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

        String  realPath = request.getSession().getServletContext().getRealPath("");
        //XML文件保存的路径   以及  生成的pdf存放的路径
        String xml_save_path = propertyConfigurer.getProperty("xml_save_path");
        String pdf_export_path =  propertyConfigurer.getProperty("pdf_export_path");
        if (StringUtils.isEmpty(xml_save_path)){
            xml_save_path = realPath + "/jrxml/MyReports/";
        }
        if (StringUtils.isEmpty(pdf_export_path)){
            pdf_export_path = realPath + "pdf";
        }
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
                        if (submap!=null && submap.get("subReportName").toString().trim().equals("AllergyAnalysis")){
                            AllergyAnalysis( xml_save_path,submap,mainList);
                        }else if (submap!=null && !StringUtils.isEmpty(submap.get("subReportName"))&& submap.get("subReportName").toString().trim().equals("LaboratoryTest")){
                            LaboratoryTest(xml_save_path,submap,mainList);
                        }else if (submap!=null && !StringUtils.isEmpty(submap.get("subReportName"))&& submap.get("subReportName").toString().trim().equals("PredefinedContentConvergence")){
                            PredefinedContentConvergence(xml_save_path,submap,mainList);
                        }else if (submap!=null && !StringUtils.isEmpty(submap.get("subReportName"))&& submap.get("subReportName").toString().trim().equals("AbnormalMeasurementValueEvaluation")){
                            AbnormalMeasurementValueEvaluation(xml_save_path,submap,mainList);
                        }

                    }else {
                        if (submap!=null && submap.get("subReportName").toString().trim().equals("SectionHeader")){
                            SectionHeader(xml_save_path,submap,mainList);
                        }else if (submap!=null && submap.get("subReportName").toString().trim().equals("DiseaseGeneticRiskTable")){
                            DiseaseGeneticRiskTable(xml_save_path,submap,mainList);

                        }else if (submap!=null && (submap.get("subReportName").toString().trim().equals("SuggestionReport")
                                        || submap.get("subReportName").equals("ExamSuggestionReport") )){
                            ExamSuggestionReport(xml_save_path,submap,mainList);

                        } else if (submap!=null && submap.get("subReportName")!=null
                                && ( submap.get("subReportName").toString().trim().equals("BodyAntiCancerCellAbilityEvaluation") || submap.get("subReportName").toString().trim().equals("FoodFamilyAndPossilableSource")
                                     || submap.get("subReportName").toString().trim().equals("microRNACancerObservation") || submap.get("subReportName").toString().trim().equals("NutritionAndToxicityElementalAnalysis")
                                     || submap.get("subReportName").toString().trim().equals("HypothalamicPituitaryOvarianEndocrineRegulation") || submap.get("subReportName").toString().trim().equals("HarvardCancerRiskEvaluation")
                                     || submap.get("subReportName").toString().trim().equals("BodyAntiCancerCellAbilityEvaluation") || submap.get("subReportName").toString().trim().equals("BodyCancerMarkerScreen")
                                     || submap.get("subReportName").toString().trim().equals("CancerInheritanceRiskEvaluation") || submap.get("subReportName").toString().trim().equals("FoodAllergySourceAnalysis")
                                     || submap.get("subReportName").toString().trim().equals("AdrenalStressAnalysisReport")||submap.get("subReportName").toString().trim().equals("LowerVisionPituitaryTesticularEndocrineRegulation")||
                                     submap.get("subReportName").toString().trim().equals("ExternalAndInternalReportExplanation")||
                                    submap.get("subReportName").toString().trim().equals("AdrenalStressAnalysisReport") ||
                                    submap.get("subReportName").toString().trim().equals("MenstrualCycleCalculation")   ||
                                    submap.get("subReportName").toString().trim().equals("FemaleHormonalBalanceAdviceAndFunctionalMedicineTest")  ||
                                    submap.get("subReportName").toString().trim().equals("MaleHormonalBalanceAdviceAndFunctionalMedicineTest")

                        )){
                            jDesign = JRXmlLoader.load(new File(xml_save_path+"/"+submap.get("subReportName")+".jrxml"));
                            jReport = JasperCompileManager.compileReport(jDesign);
                            mainList.add(new MainReports(jReport, jReport ,
                                    new JRBeanCollectionDataSource(Arrays.asList(""))));
                        }else if (submap!=null && (submap.get("subReportName").toString().trim().equals("IndicatorRiskLevel"))){
                            IndicatorRiskLevel(xml_save_path,submap,mainList);

                        }else if (submap!=null && (submap.get("subReportName").toString().trim().equals("BasicInformation"))){
                            basicInformation(xml_save_path,submap,mainList);
                        }else if(submap!=null && (submap.get("subReportName").toString().trim().equals("HealthExpectation"))){
                            healthExpectation(xml_save_path,submap,mainList);
                        }else if(submap!=null && (submap.get("subReportName").toString().trim().equals("PersonalPastHistory"))){
                            personalPastHistory(xml_save_path,submap,mainList);
                        }else if (submap!=null && submap.get("subReportName")!=null){
                            otherList.add(submap.get("subReportName").toString());
                        }

                    }
                }
            }
            if (otherList.size()>0){
//                resultMap.put("status",404);
                response.setStatus(404);
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
//            resultMap.put("status",200);
            resultMap.put("pdfName",pdfName);
            resultMap.put("fileFolderPath",pdf_export_path);
            return resultMap;
        }catch (Exception e){
            e.printStackTrace();
//            resultMap.put("status",400);
            response.setStatus(400);
            resultMap.put("error",e.getMessage());
            return resultMap;
        }finally {
            outs.close();
        }
    }


    //针对异常检测值评估：AbnormalMeasurementValueEvaluation进行处理
    private void AbnormalMeasurementValueEvaluation(String xml_save_path,Map<String,Object> submap,List<MainReports> mainList) throws JRException {
        //主模板
        JasperDesign jDesign = JRXmlLoader.load(new File(xml_save_path + "/AbnormalMeasurementValueEvaluation.jrxml"));
        JasperReport jReport = JasperCompileManager.compileReport(jDesign);
        //子模板
        JasperDesign jDesignde = JRXmlLoader.load(new File(xml_save_path + "/AbnormalMeasurementValueTable.jrxml"));
        JasperReport jReportde = JasperCompileManager.compileReport(jDesignde);

        List<AbnormalMeasurementValueTable> detailList = new ArrayList<>();
        List<AbnormalMeasurementValueEvaluation> reportList = new ArrayList<>();
        //title "data" : {"title" : "以下是您的男性荷尔蒙健康评估报告"}
        String detailTitle = "";
        if (!StringUtils.isEmpty(submap.get("data"))) {
            //获取主模板中的标题
            JSONObject titleObject = JSONObject.parseObject(submap.get("data").toString());
            detailTitle = StringUtils.isEmpty(titleObject.get("title")) ? "" : titleObject.get("title").toString();
        }
        if (!StringUtils.isEmpty(submap.get("subReports"))) {
            JSONArray subArray = JSON.parseArray(submap.get("subReports").toString());
            int mainNum = 1;
            String dataTitle = "";
            for (Object detailObject : subArray) {
                detailList = new ArrayList<>();
                Map<String, Object> detailMap = (Map<String, Object>) detailObject;
                if (!StringUtils.isEmpty(detailMap.get("subReportName")) &&
                        detailMap.get("subReportName").toString().trim().equals("AbnormalMeasurementValueTable")) {

                    if (!StringUtils.isEmpty(detailMap.get("data"))) {
                        JSONObject dataObject = JSONObject.parseObject(detailMap.get("data").toString());
                        dataTitle = dataObject.get("title") == null ? "" : dataObject.get("title").toString();
                        if (!StringUtils.isEmpty(dataObject.get("content"))) {
                            JSONArray contentArray = JSON.parseArray(dataObject.get("content").toString());
                            int subNum = 1;
                            for (Object contentObject : contentArray) {
                                Map<String, String> contentMap = (Map<String, String>) contentObject;
                                String measurementTitle = contentMap.get("measurementTitle");
                                String value = contentMap.get("value");
                                String standardMinValue = contentMap.get("standardMinValue");
                                String standardMaxValue = contentMap.get("standardMaxValue");
                                String pathologicalChanges = contentMap.get("pathologicalChanges");
                                String unit = contentMap.get("unit");
                                String isTop = "false";
                                String isBottom = "true";
                                if (subNum == contentArray.size()) {
                                    isBottom = "false";
                                }
                                if (mainNum > 1 && subNum == 1) {
                                    isTop = "true";
                                }
                                AbnormalMeasurementValueTable valueTable =
                                        new AbnormalMeasurementValueTable(detailTitle, measurementTitle, value, standardMinValue,
                                                standardMaxValue, pathologicalChanges, unit, isTop, isBottom);
                                detailList.add(valueTable);
                                subNum++;
                            }
                        }

                    }
                    AbnormalMeasurementValueEvaluation report = new AbnormalMeasurementValueEvaluation(dataTitle,new JRBeanCollectionDataSource(detailList), jReportde);
                    reportList.add(report);
                }
                mainNum++;
            }
        }

        mainList.add(new MainReports(jReport, jReport,
                new JRBeanCollectionDataSource(reportList)));
    }

    //针对预定义内容汇聚：PredefinedContentConvergence进行处理
    private void PredefinedContentConvergence(String xml_save_path,Map<String,Object> submap,List<MainReports> mainList) throws JRException {
        //主模板
        JasperDesign jDesign = JRXmlLoader.load(new File(xml_save_path+"/PredefinedContentConvergence.jrxml"));
        JasperReport jReport = JasperCompileManager.compileReport(jDesign);
        //子模板
        JasperDesign jDesignde ;
        JasperReport jReportde ;

        List<PredefinedContentConvergence> reportList  = new ArrayList<>();
        //title "data" : {"title" : "以下是您的男性荷尔蒙健康评估报告"}
        String detailTitle = "";
        if (!StringUtils.isEmpty(submap.get("data"))){
            JSONObject titleObject = JSONObject.parseObject(submap.get("data").toString());
            detailTitle = StringUtils.isEmpty(titleObject.get("title"))?"":titleObject.get("title").toString();
        }
        if (!StringUtils.isEmpty(submap.get("subReports"))){
            JSONArray subArray = JSON.parseArray(submap.get("subReports").toString());
            for (Object detailObject : subArray){
                Map<String,Object> detailMap = (Map<String, Object>) detailObject;
                if (!StringUtils.isEmpty( detailMap.get("subReportName") ) &&
                        detailMap.get("subReportName").toString().trim().equals("EvaluationDetail") ){
                    evaluationDetail(xml_save_path,detailMap,detailTitle,reportList);
                }else if (!StringUtils.isEmpty( detailMap.get("subReportName") ) &&
                        detailMap.get("subReportName").toString().trim().equals("FoodSourceExplanation") ){
                    foodSourceExplanation(xml_save_path,detailMap,detailTitle,reportList);
                }else if (!StringUtils.isEmpty( detailMap.get("subReportName") ) &&
                        detailMap.get("subReportName").toString().trim().equals("LabTestItemExplanation")){
                    labTestItemExplanation(xml_save_path,detailMap,detailTitle,reportList);
                }else if (!StringUtils.isEmpty( detailMap.get("subReportName") ) &&
                        (
                           detailMap.get("subReportName").toString().trim().equals("AdrenalStressAnalysisReport") ||
                           detailMap.get("subReportName").toString().trim().equals("MenstrualCycleCalculation")   ||
                           detailMap.get("subReportName").toString().trim().equals("FemaleHormonalBalanceAdviceAndFunctionalMedicineTest")  ||
                           detailMap.get("subReportName").toString().trim().equals("MaleHormonalBalanceAdviceAndFunctionalMedicineTest")||
                           detailMap.get("subReportName").toString().trim().equals("test")

                        ) ){
                    JasperDesign jDesignstatic = JRXmlLoader.load(new File(xml_save_path+"/"+detailMap.get("subReportName").toString().trim()+".jrxml"));
                    JasperReport jReportstatic = JasperCompileManager.compileReport(jDesignstatic);
                    PredefinedContentConvergence report = new PredefinedContentConvergence(detailTitle,new JRBeanCollectionDataSource(Arrays.asList(new AllergyAnalysis())),jReportstatic);
                    reportList.add(report);
                }
            }
        }

        mainList.add(new MainReports(jReport, jReport ,
                new JRBeanCollectionDataSource(reportList)));
    }

    //针对评估报告内容预定义：evaluationDetail进行处理
    private  void  evaluationDetail(String xml_save_path,Map<String,Object> detailMap,
                                    String detailTitle,List<PredefinedContentConvergence> reportList) throws JRException {
        //子模板
        JasperDesign jDesignde = JRXmlLoader.load(new File(xml_save_path+"/EvaluationDetail.jrxml"));
        JasperReport jReportde = JasperCompileManager.compileReport(jDesignde);
        List<EvaluationDetail> detailList = new ArrayList<>();
        if (!StringUtils.isEmpty( detailMap.get("data") ) ){
            JSONObject dataObject = JSONObject.parseObject( detailMap.get("data").toString() );
            String dataTitle = dataObject.get("title")==null?"":dataObject.get("title").toString();
            if (!StringUtils.isEmpty( dataObject.get("content") ) ){
                JSONArray contentArray = JSON.parseArray(dataObject.get("content").toString());
                for (Object contentObject : contentArray){
                    Map<String,String> contentMap = (Map<String,String>) contentObject;
                    String contentTitle = contentMap.get("title");
                    String contentContent = contentMap.get("content");
                    EvaluationDetail evaluationDetail = new EvaluationDetail(dataTitle,contentTitle,contentContent);
                    detailList.add(evaluationDetail);
                }
            }

        }
        PredefinedContentConvergence report = new PredefinedContentConvergence(detailTitle,new JRBeanCollectionDataSource(detailList),jReportde);
        reportList.add(report);
    }

    //针对评估报告内容预定义：evaluationDetail进行处理
    private  void  foodSourceExplanation(String xml_save_path,Map<String,Object> detailMap,
                                    String detailTitle,List<PredefinedContentConvergence> reportList) throws JRException {
        //子模板
        JasperDesign jDesignde = JRXmlLoader.load(new File(xml_save_path+"/FoodSourceExplanation.jrxml"));
        JasperReport jReportde = JasperCompileManager.compileReport(jDesignde);
        List<FoodSourceExplanation> detailList = new ArrayList<>();
        if (!StringUtils.isEmpty( detailMap.get("data") ) ){
            JSONObject dataObject = JSONObject.parseObject( detailMap.get("data").toString() );
            String dataTitle = dataObject.get("title")==null?"":dataObject.get("title").toString();
            if (!StringUtils.isEmpty( dataObject.get("content") ) ){
                JSONArray contentArray = JSON.parseArray(dataObject.get("content").toString());
                for (Object contentObject : contentArray){
                    Map<String,String> contentMap = (Map<String,String>) contentObject;
                    String contentTitle = contentMap.get("title");
                    String contentContent = contentMap.get("content");
                    FoodSourceExplanation evaluationDetail = new FoodSourceExplanation(dataTitle,contentTitle,contentContent);
                    detailList.add(evaluationDetail);
                }
            }

        }
        PredefinedContentConvergence report = new PredefinedContentConvergence(detailTitle,new JRBeanCollectionDataSource(detailList),jReportde);
        reportList.add(report);
    }

    //针对评估报告内容预定义：evaluationDetail进行处理
    private  void  labTestItemExplanation(String xml_save_path,Map<String,Object> detailMap,
                                         String detailTitle,List<PredefinedContentConvergence> reportList) throws JRException {
        //子模板
        JasperDesign jDesignde = JRXmlLoader.load(new File(xml_save_path+"/LabTestItemExplanation.jrxml"));
        JasperReport jReportde = JasperCompileManager.compileReport(jDesignde);
        List<LabTestItemExplanation> detailList = new ArrayList<>();
        if (!StringUtils.isEmpty( detailMap.get("data") ) ){
            JSONObject dataObject = JSONObject.parseObject( detailMap.get("data").toString() );
            String dataTitle = dataObject.get("title")==null?"":dataObject.get("title").toString();
            String dataContent = dataObject.get("content")==null?"":dataObject.get("content").toString();
            if (!StringUtils.isEmpty( dataObject.get("items") ) ){
                JSONArray contentArray = JSON.parseArray(dataObject.get("items").toString());
                int num = 1;
                for (Object contentObject : contentArray){
                    Map<String,String> contentMap = (Map<String,String>) contentObject;
                    String contentTitle = num+"."+contentMap.get("title");
                    String contentContent = contentMap.get("content");
                    LabTestItemExplanation labTestItemExplanation = new LabTestItemExplanation(dataTitle,dataContent,contentTitle,contentContent);
                    detailList.add(labTestItemExplanation);
                    num++;
                }
            }

        }
        PredefinedContentConvergence report = new PredefinedContentConvergence(detailTitle,new JRBeanCollectionDataSource(detailList),jReportde);
        reportList.add(report);
    }



    private  void  AllergyAnalysis(String xml_save_path,Map<String,Object> submap,List<MainReports> mainList) throws JRException, FileNotFoundException {
        List<AllergyAnalysis> data = new ArrayList();
        JasperDesign jDesign =  JRXmlLoader.load(new File(xml_save_path+"/AllergyAnalysis.jrxml"));
        JasperReport jReport = JasperCompileManager.compileReport(jDesign);

        JasperDesign jDesign2 = JRXmlLoader.load(new File(xml_save_path+"/AllergyAnalysisTable.jrxml"));
        JasperReport jReport2 = JasperCompileManager.compileReport(jDesign2);
        List<Map<String,Object>> slist = (List<Map<String, Object>>) submap.get("subReports");
        int num = 0 ;
        List<List<DataBean>>  sunBeanList = new ArrayList<>();
        for (Map<String,Object> smap :slist){
            List<DataBean> dataBeanList = new ArrayList();
            if(smap!=null && smap.get("subReportName").toString().trim().equals("AllergyAnalysisTable")){

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
        JasperDesign jasperDesign1 = JRXmlLoader.load(new FileInputStream(xml_save_path+"/AllergyAnalysisTable1.jrxml"));
        JasperReport jasperReport1 = JasperCompileManager.compileReport(jasperDesign1);
        JasperDesign jasperDesign2 = JRXmlLoader.load(new FileInputStream(xml_save_path+"/AllergyAnalysisTable2.jrxml"));
        JasperReport jasperReport2 = JasperCompileManager.compileReport(jasperDesign2);
        JasperDesign jasperDesign3 = JRXmlLoader.load(new FileInputStream(xml_save_path+"/AllergyAnalysisTable.jrxml"));
        JasperReport jasperReport3 = JasperCompileManager.compileReport(jasperDesign3);
        List<AllergyAnalysis1> county1List = new ArrayList<>();
        List<AllergyAnalysis1> county2List = new ArrayList<>();
        for (int i = 0;i<sunBeanList.size();i=i+2){
            JRBeanCollectionDataSource ds1 = new JRBeanCollectionDataSource(sunBeanList.get(i));
            JRBeanCollectionDataSource ds2 = null;
            if (i+1<sunBeanList.size()){
                ds2 = new JRBeanCollectionDataSource(sunBeanList.get(i+1));
            }
            county1List.add(new AllergyAnalysis1(ds1,jasperReport3));
            county2List.add(new AllergyAnalysis1(ds2,jasperReport3));
        }
        data.add(new AllergyAnalysis(1,((Map<String,Object>)submap.get("data")).get("title").toString(),new JRBeanCollectionDataSource(county1List),
                new JRBeanCollectionDataSource(county2List),jasperReport1,jasperReport2));

        mainList.add(new MainReports(jReport,jReport2,new JRBeanCollectionDataSource(data)));
    }

    private  void  LaboratoryTest(String xml_save_path,Map<String,Object> submap,List<MainReports> mainList) throws JRException, FileNotFoundException {
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
                    JSONObject dataJson = JSONObject.parseObject(map1.get("data").toString());
                    String title = dataJson.get("title")==null?"":dataJson.get("title").toString();
                    String date = dataJson.get("date")==null?"":dataJson.get("date").toString();
                    if (!StringUtils.isEmpty( dataJson.get("values"))){
                        JSONArray value = JSON.parseArray(dataJson.get("values").toString() );
                        for (Object objec : value){
                            Map<String,Object> data = (Map<String, Object>) objec;
                            String ename  = data.get("name")==null?"":data.get("name").toString();
                            String name = data.get("ChineseName")==null?"":data.get("ChineseName").toString();
                            String value1 = data.get("testValue")==null?"0":  data.get("testValue").toString() ;
                            double max = data.get("MaxReferValue")==null?0: Double.valueOf(  data.get("MaxReferValue").toString() );
                            String unit = data.get("unit")==null?"":data.get("unit").toString();
                            double min =  data.get("MinReferValue")==null?-1000: Double.valueOf( data.get("MinReferValue").toString());

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


    private  void  SectionHeader(String xml_save_path,Map<String,Object> submap,List<MainReports> mainList) throws JRException, FileNotFoundException {
        JasperDesign jDesign = JRXmlLoader.load(new File(xml_save_path+"/SectionHeader.jrxml"));
        JasperReport jReport = JasperCompileManager.compileReport(jDesign);
        JSONObject allergyObject = JSONObject.parseObject(submap.get("data").toString());
        String title = allergyObject.get("title")==null?"":allergyObject.get("title").toString();
        String subTitle = allergyObject.get("subTitle")==null?"":allergyObject.get("subTitle").toString();
        mainList.add(new MainReports(jReport, jReport ,
                new JRBeanCollectionDataSource(Arrays.asList(new SectionHeader(title,subTitle)))));

    }


    private  void  DiseaseGeneticRiskTable(String xml_save_path,Map<String,Object> submap,List<MainReports> mainList) throws JRException, FileNotFoundException {
        JasperDesign jDesign = JRXmlLoader.load(new File(xml_save_path+"/DiseaseGeneticRiskTable.jrxml"));
        JasperReport jReport = JasperCompileManager.compileReport(jDesign);
        JasperDesign jDesigndeSub = JRXmlLoader.load(new File(xml_save_path+"/DiseaseGeneticRiskTable-sub.jrxml"));
        JasperReport jReportdeSub = JasperCompileManager.compileReport(jDesigndeSub);
        String name = "";
        String fx = "";
        List<String> itemList = new ArrayList<>();
        List<Integer> valueList = new ArrayList<>();
        List<DiseaseGeneticRiskTableSub> disList = new ArrayList<>();
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
            DiseaseGeneticRiskTableSub table = new DiseaseGeneticRiskTableSub(itemList.get(i),valueList.get(i)+"",name,fx);
            disList.add(table);
        }
        mainList.add(new MainReports(jReport, jReport ,
                new JRBeanCollectionDataSource(
                        Arrays.asList( new DiseaseGeneticRiskTable(
                                new JRBeanCollectionDataSource(disList) ,
                                jReportdeSub )
                        )
                )));

    }


    private  void  ExamSuggestionReport(String xml_save_path,Map<String,Object> submap,List<MainReports> mainList) throws JRException, FileNotFoundException {
        JasperDesign jDesign = JRXmlLoader.load(new File(xml_save_path+"/SuggestionReport.jrxml"));
        JasperReport jReport = JasperCompileManager.compileReport(jDesign);
        JSONObject allergyObject = JSONObject.parseObject(submap.get("data").toString());
        String title = allergyObject.get("title")==null?"":allergyObject.get("title").toString();
        String text = allergyObject.get("text")==null?"":allergyObject.get("text").toString();
        mainList.add(new MainReports(jReport, jReport ,
                new JRBeanCollectionDataSource(Arrays.asList(new SuggestionReport(title,text)))));


    }



    private  void  IndicatorRiskLevel(String xml_save_path,Map<String,Object> submap,List<MainReports> mainList) throws JRException, FileNotFoundException {
        JasperDesign jDesign = JRXmlLoader.load(new File(xml_save_path+"/IndicatorRiskLevel.jrxml"));
        JasperReport jReport = JasperCompileManager.compileReport(jDesign);
        JSONArray levelArray = JSONArray.parseArray(submap.get("data").toString());
        List<IndicatorRiskLevel> levelList = new ArrayList<>();
        for (Object levelObjct:levelArray){
            Map<String,String> levelMap  = (Map<String, String>) levelObjct;
            String name = levelMap.get("indicatorName")==null?"":levelMap.get("indicatorName").toString();
            String ename = levelMap.get("indicatorEnglishName")==null?"":levelMap.get("indicatorEnglishName").toString();
            String level = levelMap.get("riskLevel")==null?"":levelMap.get("riskLevel").toString();
            IndicatorRiskLevel indicatorRiskLevel = new IndicatorRiskLevel(name,ename,level);
            levelList.add(indicatorRiskLevel);
        }

        mainList.add(new MainReports(jReport, jReport ,
                new JRBeanCollectionDataSource(levelList)));

    }

    private  void  basicInformation(String xml_save_path,Map<String,Object> submap,List<MainReports> mainList) throws JRException, FileNotFoundException {
        JasperDesign jDesign = JRXmlLoader.load(new File(xml_save_path+"/BasicInformation.jrxml"));
        JasperReport jReport = JasperCompileManager.compileReport(jDesign);
        JSONObject allergyObject = JSONObject.parseObject(submap.get("data").toString());
        BasicInformation basicInformation = new BasicInformation();
        if (allergyObject != null){
            InitUtil.init(basicInformation,allergyObject);
        }

        mainList.add(new MainReports(jReport, jReport ,
                new JRBeanCollectionDataSource(Arrays.asList(basicInformation))));


    }
    private  void  healthExpectation(String xml_save_path,Map<String,Object> submap,List<MainReports> mainList) throws JRException, FileNotFoundException {
        JasperDesign jDesign = JRXmlLoader.load(new File(xml_save_path+"/HealthExpectation.jrxml"));
        JasperReport jReport = JasperCompileManager.compileReport(jDesign);
        JSONObject allergyObject = JSONObject.parseObject(submap.get("data").toString());
        HealthExpectation healthExpectation = new HealthExpectation();
        if(allergyObject != null){
            InitUtil.init(healthExpectation,allergyObject);
        }
        mainList.add(new MainReports(jReport, jReport ,
                new JRBeanCollectionDataSource(Arrays.asList(healthExpectation))));

    }
    private  void  personalPastHistory(String xml_save_path,Map<String,Object> submap,List<MainReports> mainList) throws JRException, FileNotFoundException {
        JasperDesign jDesign = JRXmlLoader.load(new File(xml_save_path+"/PersonalPastHistory.jrxml"));
        JasperReport jReport = JasperCompileManager.compileReport(jDesign);
        JSONObject allergyObject = JSONObject.parseObject(submap.get("data").toString());
        PersonalPastHistory personalPastHistory = new PersonalPastHistory();
        if(allergyObject != null){
            InitUtil.init(personalPastHistory,allergyObject);

        }
        mainList.add(new MainReports(jReport, jReport ,
                new JRBeanCollectionDataSource(Arrays.asList(personalPastHistory))));

    }
}


