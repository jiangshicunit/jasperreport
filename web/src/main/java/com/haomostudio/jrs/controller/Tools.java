package com.haomostudio.jrs.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Stevie on 2015/12/20.
 */
public class Tools {

    private static SchedulerFactory schedulerFactory = new StdSchedulerFactory();

    public static final String yyyyMMdd = "yyyyMMdd";
    public static final String dateFormat = "yyyy-MM-dd";
    public static final String dateTimeFormat = "yyyy-MM-dd HH:mm:ss";
    public static final String dateTimePattern = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$";

    public static String getUUID() {
        return UUID.randomUUID().toString().trim().replaceAll("-", "");
    }

    public static int getRandomNumber(int min, int max) {
        Random random = new Random();
        int result = random.nextInt(max) % (max - min + 1) + min;
        return result;
    }

    /**
     * @param request
     * @param multipartFile
     * @param targetName    目标文件名（不含扩展名）
     * @param targetFolder  目标文件夹
     * @return
     * @throws IOException
     */
    public static File uploadFile(HttpServletRequest request, MultipartFile multipartFile, String targetName, String targetFolder) throws IOException {

        if (null == multipartFile || multipartFile.isEmpty()) {
            return null;
        }

        String dir = request.getSession().getServletContext().getRealPath(targetFolder);
        String fileName;
        //目标文件名为空则使用原文件名
        if (StringUtils.isEmpty(targetName)) {
            fileName = multipartFile.getOriginalFilename();
        } else {
            String type = multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().lastIndexOf("."));
            fileName = targetName + type;
        }

        File file = new File(dir, fileName);
        FileUtils.writeByteArrayToFile(file, multipartFile.getBytes());
        return file;
    }

    /**
     * 获取泛型的Collection Type
     *
     * @param jsonStr         json字符串
     * @param collectionClass 泛型的Collection
     * @param elementClasses  元素类型
     */
    public static <T> T readJson(String jsonStr, Class<?> collectionClass, Class<?>... elementClasses) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        JavaType javaType = mapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
        return mapper.readValue(jsonStr, javaType);

    }

    // 获取spring容器
    public static WebApplicationContext getSpringContext() {
        return ContextLoader.getCurrentWebApplicationContext();
    }

    // 获取spring容器中的bean
    public static Object getSpringBean(String beanName) {
        return getSpringContext().getBean(beanName);
    }

    //



    public static String nullObjToEmpty(Object obj) {
        if (obj == null) {
            return "";
        }
        return StringUtils.trim(obj.toString());
    }

    public static int transferToInt(String intStr) {
        try {
            return Integer.parseInt(intStr);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static Double transferToDouble(String intStr) {
        try {
            return Double.valueOf(intStr);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    /**
     * 连接字符串
     * [aaa,bbb,ccc]-->'aaa','bbb','ccc'
     * @param strs
     * @return
     */
    public static String joinStr(List<String> strs) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < strs.size() - 1; i++) {
            sb.append("'").append(strs.get(i)).append("',");
        }
        sb.append("'").append(strs.get(strs.size() - 1)).append("'");
        return sb.toString();
    }

    public static String joinStrBySp(List<String> strs,String sp) {
        if(strs == null || strs.size() == 0){
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < strs.size() - 1; i++) {
            sb.append(strs.get(i) + sp);
        }
        sb.append(strs.get(strs.size() - 1));
        return sb.toString();
    }

    public static <T extends Serializable> T clone(T obj) {
        ByteArrayOutputStream bos = null;
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        T t = null;
        try {
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
            t = (T) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                ois.close();
                oos.close();
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return t;
    }

    public static String convertDateToString(Date value, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String result = sdf.format(value);
        return result;
    }

    public static String convertToSapString(Object value) {
        SimpleDateFormat sdf = new SimpleDateFormat(yyyyMMdd);
        String result = null;
        if (value instanceof Date) {
            result = sdf.format(value);
        }
        return result;
    }

    public static Date convertStringToDate(String str, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            Date date = sdf.parse(str);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getApplicationRootPath() {
        return ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/");
    }

    public static List<Map<String, Object>> transToLower(List<Map<String,Object>> maps){
        List<Map<String,Object>> tmps= new ArrayList<>();
        for (Map<String, Object> map : maps) {
            Map<String,Object> tmp = new HashMap<>();
            for (String s : map.keySet()) {
                tmp.put(s.toLowerCase(),map.get(s));
            }
            tmps.add(tmp);
        }
        return tmps;
    }

    public static List<Map<String, Object>> addPrefix(String prefix,List<Map<String,Object>> maps){
        List<Map<String,Object>> tmps= new ArrayList<>();
        for (Map<String, Object> map : maps) {
            Map<String,Object> tmp = new HashMap<>();
            for (String s : map.keySet()) {
                tmp.put(prefix+"."+s,map.get(s));
            }
            tmps.add(tmp);
        }
        return tmps;
    }


    public static List<Map<String, Object>> mergeMap(List<Map<String,Object>> amaps,List<Map<String,Object>> bmaps){
        for (int i = 0; i < bmaps.size(); i++) {
            for (String key : bmaps.get(i).keySet()) {
                amaps.get(i).put(key,bmaps.get(i).get(key));
            }
        }
        return amaps;
    }

    public static List<Map<String, Object>> transToLowerJustID(List<Map<String,Object>> maps){
        List<Map<String,Object>> tmps= new ArrayList<>();
        for (Map<String, Object> map : maps) {
            Map<String,Object> tmp = new HashMap<>();
            for (String s : map.keySet()) {
                if(s.equals("ID")) {
                    tmp.put(s.toLowerCase(), map.get(s));
                }else{
                    tmp.put(s, map.get(s));
                }
            }
            tmps.add(tmp);
        }
        return tmps;
    }

    public static String getOrclPageSql(String sql,Integer pageNo,Integer pageSize){
        sql="select * from (select row_.*, rownum rownum_ from( " + sql +
                ")row_ where rownum <= "+(Integer)(pageNo*pageSize)+") where rownum_ >= "+(Integer)((pageNo-1)*pageSize + 1);
        return sql;
    }

    /**
     * 合同中根据账期和比例确定SAP付款条件标识
     * @param ht31 首付款账期
     * @param ht33 中期款账期
     * @param ht35 尾款账期
     *
     * 判断依据：
     * 0001 立即应付 到期净值
     * H010 10天之内 到期净值
     * H020 20天之内 到期净值
     * H030 30天之内 到期净值
     * H045 45天之内 到期净值
     * H060 60天之内 到期净值
     * H090 90天之内 到期净值
     * @return
     */
    public static String changeToSapPay(String ht31, String ht33, String ht35) {
        String checkDay = StringUtils.isNotBlank(ht35) ? ht35 : (StringUtils.isNotBlank(ht33) ? ht33 : ht31);
        if ("0".equals(checkDay)) {
            return "0001";
        }
        return "H0" + getFloorDay(Integer.parseInt(checkDay));
    }

    private static int getFloorDay(int htDay) {
        int[] days = new int[]{10, 20, 30, 45, 60, 90};
        for (int day : days) {
            if (day - htDay >= 0) {
                return day;
            }
        }
        return 0;
    }

    public static boolean containsKeyIgnoreCase(Map<String, Object> map, String key) {
        if (StringUtils.isBlank(key)) {
            return false;
        }
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (StringUtils.isBlank(entry.getKey())) {
                return false;
            }
            if (entry.getKey().equalsIgnoreCase(key)) {
                return true;
            }
        }
        return false;
    }

    private static String escapeSql(String str) {
        if (str.indexOf("%") > -1) {
            str = str.replaceAll("%", "\\\\%");
        }
        if (str.indexOf("_") > -1) {
            str = str.replaceAll("_", "\\\\_");
        }
        return str;
    }

    /**
     * (key, value) --> key='value' / key=value
     * @param key
     * @param value
     * @return
     */
    public static String joinDbKV(String key, Object value) {
        if (value == null) {
            return key + "=" + value;
        } else {
            return key + "='" + value + "'";
        }
    }

    public static String getOracleToDate(Date date) {
        return "to_date('" + convertDateToString(date, dateFormat) + "', 'yyyy-MM-dd')";
    }

    public static String getOracleToDateTime(Date date) {
        return "to_date('" + convertDateToString(date, dateTimeFormat) + "', 'yyyy-MM-dd hh24:mi:ss')";
    }

    public static Map<String, Object> convertJSONObjectToMap(JSONObject object) {
        Map<String, Object> map = new HashMap<>();
        for (Map.Entry<String, Object> entry : object.entrySet()) {
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }

    public static List<Map<String, Object>> convertJSONArrayToList(JSONArray array) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Object item : array) {
            Map<String, Object> map = convertJSONObjectToMap((JSONObject) item);
            list.add(map);
        }
        return list;
    }

}
