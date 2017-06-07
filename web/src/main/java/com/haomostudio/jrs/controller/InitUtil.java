package com.haomostudio.jrs.controller;

import com.alibaba.fastjson.JSONObject;
import com.haomostudio.jrs.BasicInformation;
import com.haomostudio.jrs.PersonalPastHistory;
import org.apache.poi.hssf.record.formula.functions.T;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by yide on 17/6/7.
 */
public class InitUtil {
    public static String initSet(String method){
        return "set"+(method.substring(0,1).toUpperCase()+method.substring(1,method.length()));
    }

    public static void init(Object object, JSONObject jsonObject){
        Class clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for(Field field : fields){
            String methodName = field.getName();
            try {
                Method method = clazz.getMethod(initSet(methodName),String.class);
                method.invoke(object,jsonObject.get(methodName));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
