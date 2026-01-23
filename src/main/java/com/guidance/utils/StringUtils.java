package com.guidance.utils;

import org.springframework.util.ObjectUtils;

/**
 * 字符串工具类
 * @author liuyu
 */
public class StringUtils {
    /**
     * 获取字符串
     *
     * @param v
     * @return
     */
    public static String getStringValue(String v){
        if(ObjectUtils.isEmpty(v) || v.isBlank()){
            return "";
        }
        return v;
    }
}
