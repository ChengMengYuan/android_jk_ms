package com.grgbanking.ct.cach;

import com.grgbanking.ct.entity.LoginUser;
import com.grgbanking.ct.entity.PdaLoginMessage;
import com.grgbanking.ct.entity.PdaLoginMsg;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class DataCach {

    public static LoginUser loginUser = new LoginUser();

    public static String netType = "";

    public static LinkedHashMap<String, HashMap<String, Object>> taskMap = new LinkedHashMap<String, HashMap<String, Object>>();
    private static PdaLoginMessage pdaLoginMessage = null;
    public static LinkedHashMap<String, HashMap<String, Object>> boxesMap = new LinkedHashMap<String, HashMap<String, Object>>();
    public static PdaLoginMsg pdaLoginMsg = null;
    public static HashMap<String,String> codeMap = new HashMap<String,String>();


    public static void setPdaLoginMsg(PdaLoginMsg pdaLoginMsg) {
        if (DataCach.pdaLoginMsg != null) {
                DataCach.pdaLoginMsg = null;
        }
        DataCach.pdaLoginMsg = pdaLoginMsg;
    }

    public static PdaLoginMsg getPdaLoginMsg() {
        return DataCach.pdaLoginMsg;
    }


    public static void setPdaLoginMessage(PdaLoginMessage pdaLoginMessage) {
        if (DataCach.pdaLoginMessage != null) {
            DataCach.pdaLoginMessage = null;
        }
        DataCach.pdaLoginMessage = pdaLoginMessage;
    }

    public static PdaLoginMessage getPdaLoginMessage() {
        return DataCach.pdaLoginMessage;
    }

    /**
     * 清空缓存
     */
    public static void clearAllDataCach() {
        netType = "";
        taskMap = null;
        taskMap = new LinkedHashMap<String, HashMap<String, Object>>();
        pdaLoginMessage = null;
        boxesMap = null;
        boxesMap = new LinkedHashMap<String, HashMap<String, Object>>();
        pdaLoginMsg = null;
    }
}
