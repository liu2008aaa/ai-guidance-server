package com.guidance.vo;

import lombok.Builder;
import lombok.Data;

/**
 * 定义区域信息
 *
 * @author liuyu
 */
@Data
@Builder
public class AreaInfo {
    /**
     * 区域名称
     */
    private String name;
    /**
     * 精简后的名称
     */
    private String shortName;
    /**
     * 区域编码
     */
    private String code;

    /**
     * 级别：0省、1市、2区、3镇/街道、4村
     * 每次递归+1
     */
    private int level = 0;

    /**
     * 存储区域名称
     *
     * 0-四川省
     * 1-成都市
     * 2-锦江区
     * 3-三圣街道
     * 4-幸福社区
     */
    private String[] names = new String[5];


}
