package com.guidance.service.crawler.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 事项概要信息
 *
 * @author liuyu
 */
@Data
@AllArgsConstructor
public class SummaryInfo {
    /**
     * 事项 标题
     */
    private String title;
    /**
     * 事项 详情 URL
     */
    private String url;

    public static SummaryInfo of(String title, String url) {
        return new SummaryInfo(title,url);
    }
}
