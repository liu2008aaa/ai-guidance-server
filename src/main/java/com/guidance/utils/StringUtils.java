package com.guidance.utils;

import org.springframework.util.ObjectUtils;

/**
 * 字符串工具类
 * @author liuyu
 */
public class StringUtils {
    private static final String SUFFIX = "...";
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
    /**
     * 安全截取字符串，超出部分添加省略号
     * @param str 原始字符串
     * @param maxLength 最大长度（包含省略号）
     */
    public static String truncateWithSuffix(String str, int maxLength) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        if (maxLength <= 0) {
            return "";
        }

        // 如果字符串本身就不超过限制，直接返回
        if (str.length() <= maxLength) {
            return str;
        }

        // 预留省略号位置
        int endIndex = maxLength - SUFFIX.length();
        if (endIndex < 0) {
            endIndex = maxLength;
        }

        return str.substring(0, endIndex) + SUFFIX;
    }

}
