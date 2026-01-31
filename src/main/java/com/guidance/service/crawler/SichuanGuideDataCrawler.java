package com.guidance.service.crawler;

import com.alibaba.dashscope.utils.JsonUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.guidance.vo.AreaInfo;
import com.guidance.vo.SummaryInfo;
import com.guidance.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 四川省 办事指南数据爬虫
 * @author liuyu
 */
@Service
@Slf4j
public class SichuanGuideDataCrawler {
    /**
     * 四川省编码
     */
    public final static String AREA_CODE_SICHUAN = "510000000000";
    /**
     * 查询市、区、镇、村等名称及编码的接口
     */
    public final static String API_AREA_INFO = "https://www.sczwfw.gov.cn/jiq/interface/jitem/find-by-code?code=%s&deptType=1";

// 市
//    https://www.sczwfw.gov.cn/jiq/interface/item/tags?dxType=1002&areaCode=510000000000&theme=&deptCode=&isonline=&searchtext=&pageno=2&type=2&limitSceneNum=&taskType=
//    https://www.sczwfw.gov.cn/jiq/interface/item/tags?dxType=1004&areaCode=510000000000&theme=&deptCode=&isonline=&searchtext=&pageno=2&type=2&limitSceneNum=&taskType=

    /**
     * 市级列表-个人业务
     */
    public final static String API_CITY_LIST_0 = "https://www.sczwfw.gov.cn/jiq/interface/item/tags?dxType=1002&areaCode=%s&theme=&deptCode=&isonline=&searchtext=&pageno=%d&type=2&limitSceneNum=&taskType=";
    /**
     * 市级列表-企业业务
     */
    public final static String API_CITY_LIST_1 = "https://www.sczwfw.gov.cn/jiq/interface/item/tags?dxType=1004&areaCode=%s&theme=&deptCode=&isonline=&searchtext=&pageno=%d&type=2&limitSceneNum=&taskType=";
//区
//    https://www.sczwfw.gov.cn/jiq/interface/item/tags?dxType=2&areaCode=510115000000&theme=&deptCode=&isonline=&searchtext=&pageno=2&type=2&limitSceneNum=&taskType=
//    https://www.sczwfw.gov.cn/jiq/interface/item/tags?dxType=3&areaCode=510115000000&theme=&deptCode=&isonline=&searchtext=&pageno=2&type=2&limitSceneNum=&taskType=
    /**
     * 区级列表-个人业务
     */
    public final static String API_AREA_LIST_0 = "https://www.sczwfw.gov.cn/jiq/interface/item/tags?dxType=2&areaCode=%s&theme=&deptCode=&isonline=&searchtext=&pageno=%d&type=2&limitSceneNum=&taskType=";
    /**
     * 区级列表-企业业务
     */
    public final static String API_AREA_LIST_1 = "https://www.sczwfw.gov.cn/jiq/interface/item/tags?dxType=3&areaCode=%s&theme=&deptCode=&isonline=&searchtext=&pageno=%d&type=2&limitSceneNum=&taskType=";
//镇/街道
//    https://www.sczwfw.gov.cn/jiq/interface/item/tags?dxType=54&areaCode=510115001000&theme=&userType=&pageno=2
    /**
     * 镇/街道级列表
     */
    public final static String API_TOWN_LIST = "https://www.sczwfw.gov.cn/jiq/interface/item/tags?dxType=54&areaCode=%s&theme=&userType=&pageno=%d";
//村
//    https://www.sczwfw.gov.cn/jiq/interface/item/tags?dxType=56&areaCode=510115001777&theme=&userType=&pageno=2
    /**
     * 村级列表
     */
    public final static String API_VILLAGE_LIST = "https://www.sczwfw.gov.cn/jiq/interface/item/tags?dxType=56&areaCode=%s&theme=&userType=&pageno=%d";
    /**
     * 抓取 区域信息：名称、编码
     * @param code
     * @return
     */
    public static List<AreaInfo> fetchAreaInfo(String code){
        try {
            String res = HttpUtils.getByHttp(String.format(API_AREA_INFO, code));
            res = HttpUtils.extractJsonFromJsonp(res);
            log.debug("fetchCity.result:{}", res);
            if(ObjectUtils.isEmpty(res)){
                log.info("fetchAreaInfo result is empty areaCode:{}",code);
                return null;
            }
            JsonObject jsonObject = JsonUtils.parse(res);
            if(!"200".equals(jsonObject.get("code").getAsString()) || !jsonObject.get("success").getAsBoolean()){
                log.error("fetchAreaInfo is fail areaCode:{},returnCode:{}",code,jsonObject.get("code").getAsString());
                return null;
            }
            jsonObject = jsonObject.getAsJsonObject("data");
            jsonObject = jsonObject.getAsJsonObject("group");
            JsonArray array = jsonObject.getAsJsonArray("sonAreas");
            if(array== null || array.isEmpty()){
                log.info("fetchAreaInfo is end areaCode:{}",code);
                return null;
            }
            List<AreaInfo> areaInfoList = new ArrayList<>();
            for(int i=0;i<array.size();i++){
                JsonObject o = array.get(i).getAsJsonObject();
                AreaInfo info = AreaInfo.builder()
                                .code(o.get("areaCode").getAsString())
                                .name(o.get("shortName").getAsString())
                                .build();
                areaInfoList.add(info);
            }
            return areaInfoList;
        }catch (Exception e){
            log.error("fetchAreaInfo has error",e);
        }
        return null;
    }

    /**
     * 抓取 办事列表
     *
     * @param api
     * @param code
     * @param pageNo
     * @return
     */
    public static List<SummaryInfo> fetchSummaryInfo(String api,String code,int pageNo,List<SummaryInfo> totalList){
        try{
            String url = String.format(api,code,pageNo);
            log.info("fetchSummaryInfo.pageNo:{},url:{}",pageNo,url);
            String htmlTxt = HttpUtils.getByHttp(url);
            if(ObjectUtils.isEmpty(htmlTxt) || htmlTxt.trim().isBlank()){
                return totalList;
            }
            //解析列表
            List<GovSummaryParser.GovService> list = GovSummaryParser.parseGovernmentServices(htmlTxt);
            if(CollectionUtils.isEmpty(list)){
                return totalList;
            }
            //构造SummaryInfo
            for(GovSummaryParser.GovService g : list){
                totalList.add(SummaryInfo.of(g.getTitle(),g.getUrl()));
            }
            //2秒1页
            Thread.sleep(5 * 1000L);
            //递归下一页
            return fetchSummaryInfo(api,code,pageNo+1,totalList);
        }catch (Exception e){
            log.error("fetchSummaryInfo has error",e);
        }
        return totalList;
    }

    /**
     * 抓取 具体事项 详情
     * @param url
     * @return
     */
    public static GovGuideParser.GuideInfo fetchGuideInfo(String url){
        try{
            log.debug("fetchGuideInfo.url:{}",url);
            String htmlTxt = HttpUtils.getByHttp(url);
            return GovGuideParser.parseServiceGuide(htmlTxt);
        }catch (Exception e){
            log.error("fetchGuideInfo has error",e);
        }
        return null;
    }
    public static void main(String[] args) {
        //获取省下所有市编码
//        List<AreaInfo> infoList = SichuanGuideDataCrawler.fetchAreaInfo(AREA_CODE_SICHUAN);
//        System.out.println(infoList);

//        List<SummaryInfo> totalList = SichuanGuideDataCrawler.fetchSummaryInfo(API_AREA_LIST_0,"510100000000",1,new ArrayList<>());
//        System.out.println(totalList);

//        GovGuideParser.GuideInfo guideInfo = SichuanGuideDataCrawler.fetchGuideInfo("https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0101600004-510100000000-000-15667888-1-00&taskType=1&deptCode=15667888");
//        GovGuideParser.GuideInfo guideInfo = SichuanGuideDataCrawler.fetchGuideInfo("https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A04001005-510100000000-000-115101000091720521-1-00&taskType=1&deptCode=1112233");
        GovGuideParser.GuideInfo guideInfo = SichuanGuideDataCrawler.fetchGuideInfo("https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=511921101000&itemCode=511F99243096-511921101000-000-11513721008837024N-1-00&taskType=7&deptCode=000000061630");
        System.out.println(guideInfo.getServiceInfo().toText());
        System.out.println(guideInfo.getBasicInfo().toText());
        System.out.println(guideInfo.getMaterialsText());
        System.out.println(guideInfo.getProcessStepsText());
        System.out.println(guideInfo.getFeeStandardText());
        System.out.println(guideInfo.getAcceptanceConditionsText());
        System.out.println(guideInfo.getFaqText());
    }
















}
