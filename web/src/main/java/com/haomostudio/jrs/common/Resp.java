package com.haomostudio.jrs.common;

import java.util.HashMap;
import java.util.Map;

public class Resp {

    public static final int SUCCESS = 0;
    public static final int FAIL = -1;
    public static final int WARN = 1;

    public static final String CODE = "code";
    public static final String MSG = "message";
    public static final String FIELDS = "fields";

    public static Map<String, Object> succ(String message) {
        return result(SUCCESS, message, null);
    }

    public static Map<String, Object> succ(String message, Object fields) {
        return result(SUCCESS, message, fields);
    }

    public static Map<String, Object> fail(String message) {
        return result(FAIL, message, null);
    }

    public static Map<String, Object> warn(String message) {
        return result(WARN, message, null);
    }

    public static Map<String, Object> fail(int code, String message) {
        return result(code, message, null);
    }

    public static Map<String, Object> fail(String message, Object fields) {
        return result(FAIL, message, fields);
    }

    public static Map<String, Object> result(int code, String message, Object fields) {
        Map<String, Object> map = new HashMap<>();
        map.put(CODE, code);
        map.put(MSG, message);
        map.put(FIELDS, fields);
        return map;
    }
}
