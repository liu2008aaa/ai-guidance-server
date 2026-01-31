package com.guidance.utils;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.SegToken;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class RegionExtractorByHuaban {

    // 定义匹配地区后缀的正则表达式
    private static final String REGION_PATTERN = ".*(省|自治区|市|区|县|镇|乡|村|街道|社区)$";
    private static final Pattern pattern = Pattern.compile(REGION_PATTERN);

    public static void main(String[] args) {
        String text = "我老家在山东省德州市平原县恩城镇的一个小村子，但我现在住在上海市浦东新区张江高科技园区。";

        List<String> regions = extractRegions(text);

        System.out.println("提取到的地区名称：");
        regions.forEach(System.out::println);
    }

    /**
     * 从文本中提取地区名称
     */
    public static List<String> extractRegions(String text) {
        JiebaSegmenter segmenter = new JiebaSegmenter();
        // 使用 INDEX 模式进行分词，适合提取关键词
        List<SegToken> tokens = segmenter.process(text, JiebaSegmenter.SegMode.INDEX);
        
        List<String> results = new ArrayList<>();

        for (SegToken token : tokens) {
            String word = token.word;
            // 过滤长度大于 1 且符合地区特征的词
            if (word.length() > 1 && pattern.matcher(word).matches()) {
                results.add(word);
            }
        }
        return results;
    }
}