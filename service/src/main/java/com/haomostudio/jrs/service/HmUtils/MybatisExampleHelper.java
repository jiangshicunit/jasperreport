package com.haomostudio.jrs.service.HmUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.CaseFormat;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by hxgqh on 2016/10/7.
 */
public class MybatisExampleHelper {
    /**
     * 创建mybatis的函数setOrderByClause支持的排序格式
     * @param sortItem 格式为: 'id, name'
     * @param sortOrder 格式为: 'asc, desc'
     * @return
     */
    public static String createOrderClause(String sortItem, String sortOrder){
        List<String> items = Arrays.asList(sortItem.split(",\\s*"));
        List<String> orders = Arrays.asList(sortOrder.split(",\\s*"));
        if(items.size() != orders.size()){
            return null;
        }
        else{
            List<String> itemOrder = new ArrayList<>();
            for(int i=0;i<items.size();i++){
                itemOrder.add(items.get(i) + " " + orders.get(i));
            }
            return String.join(", ", itemOrder);
        }
    }

    /**
     * 获取mybatis generator自动生成的过滤函数的名称
     * @param column
     * @param condition
     * @return
     */
    public static String getFilterFuncName(String column, String condition){
        if(column.contains("_")){
            return "and"
                    + CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, column)
                    + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, condition);
        }
        else{
            return "and"
                    + column.substring(0,1).toUpperCase() + column.substring(1)
                    + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, condition);
        }
    }

    public static Method getMethod(Object obj, String methodName){
        for(Method m: obj.getClass().getDeclaredMethods()){
            if(m.getName().equals(methodName)){
                return m;
            }
        }

        return null;
    }

    /**
     * 给mybatis附加上某个字段的条件过滤
     * @param exampleObj
     * @param exampleObjCriteria
     * @param column
     * @param condition
     * @param value
     * @return
     */
    public static Object assignCondition(Object exampleObj, Object exampleObjCriteria,
                                         String column ,String condition, Object value){
        String funcName = getFilterFuncName(column, condition);
        Method m = MybatisExampleHelper.getMethod(exampleObjCriteria, funcName);

        switch (condition){
            case "isNull":
            case "isNotNull":
                try{
                    m.invoke(exampleObjCriteria);
                }
                catch (ReflectiveOperationException e){
                    throw new RuntimeException(e);
                }
                break;
            // 需要考虑时间和Number等字段
            case "between":
            case "notBetween":
                try{
                    if(m.getParameterTypes()[0].getName().contains("Date")){
                        m.invoke(exampleObjCriteria,
                                Tools.convertStringToDate(((List<String>) value).get(0), "yyyy-MM-dd HH:mm:ss"),
                                Tools.convertStringToDate(((List<String>) value).get(1), "yyyy-MM-dd HH:mm:ss"));
                    } else if(m.getParameterTypes()[0].getName().contains("Long")){
                        m.invoke(exampleObjCriteria,
                                ((List<Long>) value).get(0),
                                ((List<Long>) value).get(1));
                    } else{
                        m.invoke(exampleObjCriteria,
                                ((List<Integer>) value).get(0),
                                ((List<Integer>) value).get(1));
                    }
                }
                catch (ReflectiveOperationException e){
                    throw new RuntimeException(e);
                }
                break;

            default:
                try{
                    if(m.getParameterTypes()[0].getName().contains("Date")){
                        m.invoke(exampleObjCriteria,
                                Tools.convertStringToDate((String)value, "yyyy-MM-dd HH:mm:ss"));
                    } else if(m.getParameterTypes()[0].getName().contains("Long")){
                        m.invoke(exampleObjCriteria, Long.valueOf((String)value));

                    } else if(m.getParameterTypes()[0].getName().contains("Integer")){
                        m.invoke(exampleObjCriteria, Integer.valueOf((String)value));

                    } else if(m.getParameterTypes()[0].getName().contains("int")){
                        m.invoke(exampleObjCriteria, (int)value);
                    } else {
                        m.invoke(exampleObjCriteria, value);
                    }
                }
                catch (ReflectiveOperationException e){
                    throw new RuntimeException(e);
                }
                break;
        }
        return exampleObj;
    }

    /**
     * 给mybatis的Example对象附加上
     * @param exampleObj
     * @param exampleObjCriteria
     * @param filters JSON字符串,格式为
     *                {
     *                  table:
     *                  {
     *                    column1: {
     *                      like: '%abc%',
     *                      notLike: ''
     *                      between: [1, 10],
     *                      notBetween: [1, 10]
     *                      isNull: true,   // 只能为true
     *                      isNotNull: true,    // 只能为true
     *                      equalTo: "abc",
     *                      notEqualTo: "abc",
     *                      greaterThan: 10,
     *                      greaterThanOrEqualTo: 10,
     *                      lessThan: 10,
     *                      lessThanOrEqualTo: 10,
     *                      in: [],
     *                      notIn: []
     *                    }
     *                  }
     *
     *                }
     * @return
     */
    public static Object assignWhereClause (Object exampleObj,
                                           Object exampleObjCriteria,
                                           String className,
                                           String filters){
        JSONObject filterObj = JSON.parseObject(filters);
        if(!filterObj.containsKey(
                CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, className)
        )){
            return exampleObjCriteria;
        }

        JSONObject columnCondition = filterObj.getJSONObject(
                CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, className));

        Set<Map.Entry<String, Object>> entrySet = columnCondition.entrySet();

        for(Map.Entry<String, Object> entry: entrySet){
            String column = entry.getKey();
            JSONObject conditionObj = (JSONObject) entry.getValue();
            Set<Map.Entry<String, Object>> conditionObjEntrySet
                    = conditionObj.entrySet();

            for(Map.Entry<String, Object> coes: conditionObjEntrySet){
                exampleObj = assignCondition(
                        exampleObj, exampleObjCriteria,
                        column, coes.getKey(), coes.getValue());
            }
        }

        return exampleObjCriteria;
    }
}
