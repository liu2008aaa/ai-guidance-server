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

    /**
     * 精简区域名称
     *
     * @param name
     * @return
     */
    public static String shortAreaName(String name) {
        if(name == null || name.isBlank()){
            return name;
        }
        name = name.replaceAll("省","");
        name = name.replaceAll("市","");
        name = name.replaceAll("州","");
        name = name.replaceAll("社区","");
        name = name.replaceAll("区","");
        name = name.replaceAll("县委","");
        name = name.replaceAll("县","");
        name = name.replaceAll("乡","");
        name = name.replaceAll("镇","");
        name = name.replaceAll("村","");
        name = name.replaceAll("街道","");
        name = name.replaceAll("居民","");
        name = name.replaceAll("委员会","");
        name = name.replaceAll("委员","");
        name = name.replaceAll("会","");
        name = name.replaceAll("村民委员会","");
        name = name.replaceAll("村民","");
        name = name.replaceAll("管委会","");
        name = name.replaceAll("管委","");
        return name;
    }

}
