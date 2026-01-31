package com.guidance.utils;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 基于HanLP 的地址信息提取工具
 */
@Slf4j
public class RegionExtractorByHanLP {

    public static void main(String[] args) {
        String[] testTexts = {
            "家住浙江省杭州市余杭区闲林街道山水居社区",
            "老家是湖南省邵阳市隆回县荷香桥镇上牛村三组",
            "四川省成都市武侯区",
        };

        for (String text : testTexts) {
            System.out.println("原始文本: " + text);
            System.out.println(parseAddress(text));
            System.out.println("---");
        }
    }

    /**
     * 地址信息解析
     *
     * @param text
     * @return
     */
    public static RegionResult parseAddress(String text) {
        try {
            RegionResult result = new RegionResult();
            // 1. 初始化 HanLP 分词器，开启地名识别
            Segment segment = HanLP.newSegment().enablePlaceRecognize(true);
            List<Term> termList = segment.seg(text);
            // 2. 遍历分词结果，根据后缀和词性进行归类
            for (Term term : termList) {
                String word = term.word;
                String nature = term.nature.toString();
                // 只要是地名(ns)或者具有明显地名特征的词
                if (nature.startsWith("ns") || isLikelyLocation(word)) {
                    if (word.matches(".*(省|自治区)$")) {
                        result.province = word;
                    } else if (word.matches(".*(市|自治州)$")) {
                        result.city = word;
                    } else if (word.matches(".*(区|县|旗|市)$")) {
                        // 注意：这里的“市”可能属于县级市，逻辑上排在市/州之后
                        if (result.city.isEmpty()) result.city = word;
                        else result.district = word;
                    } else if (word.matches(".*(镇|乡|街道|苏木)$")) {
                        result.town = word;
                    } else if (word.matches(".*(村|社区|里|组|弄|巷)$")) {
                        result.village = word;
                    } else {
                        result.original = word;
                    }
                }
            }
            return result;
        }catch (Exception e){
            log.error("parseAddress has error",e);
            return null;
        }
    }

    // 辅助判断：即使 HanLP 没标出 ns，带这些后缀的通常也是地址
    private static boolean isLikelyLocation(String word) {
        return word.matches(".*(省|市|区|县|镇|乡|街道|村|社区|组)$");
    }

    /**
     * 区域信息
     */
    public static class RegionResult {
        public String province = ""; // 省/自治区
        public String city = "";     // 市/自治州
        public String district = ""; // 区/县
        public String town = "";     // 镇/乡/街道
        public String village = "";  // 村/社区/组
        public String original = "";  // 原始信息

        @Override
        public String toString() {
            return String.format("【解析结果】省:%s, 市:%s, 区:%s, 镇:%s, 村:%s",
                    province, city, district, town, village);
        }
    }
}