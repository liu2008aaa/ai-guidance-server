package com.guidance.service;

import com.guidance.service.crawler.GovGuideParser;
import com.guidance.service.crawler.SichuanGuideDataCrawler;
import com.guidance.service.crawler.vo.AreaInfo;
import com.guidance.service.crawler.vo.SummaryInfo;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.data.segment.TextSegment;
//import org.jsoup.Jsoup;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.guidance.service.crawler.SichuanGuideDataCrawler.*;

@Service
@Slf4j
public class KnowledgeService {
    private final EmbeddingStore<TextSegment> embeddingStore;
    private final EmbeddingModel embeddingModel;

    public KnowledgeService(EmbeddingStore<TextSegment> embeddingStore, 
                            EmbeddingModel embeddingModel) {
        this.embeddingStore = embeddingStore;
        this.embeddingModel = embeddingModel;
    }

    // 模拟应用启动时自动加载数据 (生产环境通常是异步任务或后台管理触发)
    public void initKnowledgeBase() {
        System.out.println(">>> 开始初始化知识库...");
        crawlAndIngest("https://www.gov.cn/zhengce/jiedu/2020-09/16/content_5543867.htm", "教师资格认定");
        // 这里可以循环读取配置文件中的 URLs
    }

    public void crawlAndIngest(String url, String serviceName) {
        try {
//            // 简单爬虫逻辑
//            org.jsoup.nodes.Document jsoupDoc = Jsoup.connect(url)
//                    .timeout(15000)
//                    .userAgent("Mozilla/5.0")
//                    .get();
//
//            String text = jsoupDoc.body().text(); // 粗糙提取，实际需根据DOM精修
//
//            Map<String,String> metaMap = new HashMap<>();
//            metaMap.put("province", "四川省");
//            metaMap.put("city", "成都市");
//            metaMap.put("district", "锦江区");
//            metaMap.put("street", "春熙路街道");
//            metaMap.put("community", "督院街社区");
//            metaMap.put("full_path", "四川省/成都市/锦江区/春熙路街道/督院街社区");
//            metaMap.put("url", "https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510104000000&itemCode=511A0138900000-510104000000-000-22221111-1-00&taskType=1&deptCode=22221111");
//            Metadata metadata = Metadata.from(metaMap);
//
//            Document document = Document.from(text,metadata);
//
//            // 数据切分与入库
//            EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
//                    .embeddingModel(embeddingModel)
//                    .embeddingStore(embeddingStore)
//                    .build();
//
//            ingestor.ingest(document);
//            System.out.println(">>> [爬虫] 已摄入: " + serviceName);
            //获取省下所有市编码
            List<AreaInfo> infoList = SichuanGuideDataCrawler.fetchAreaInfo(AREA_CODE_SICHUAN);
            if(CollectionUtils.isEmpty(infoList)){
                return;
            }
            //遍历每个 市
            for(AreaInfo areaInfo : infoList){

            }
        } catch (Exception e) {
           log.error(">>> [错误] 爬取失败: " + url,e);
        }
    }

    /**
     * 处理 区域code
     *
     * @param parentArea
     */
    public void processAreaInfo(AreaInfo parentArea) throws InterruptedException {
        //抓取编码下的子集区域编码信息
        List<AreaInfo> infoList = SichuanGuideDataCrawler.fetchAreaInfo(parentArea.getCode());
        if(CollectionUtils.isEmpty(infoList)){
            return;
        }
        //遍历每个 市
        for(AreaInfo areaInfo : infoList){
            //设置为：上一级+1
            int nextLevel = parentArea.getLevel()+1;
            areaInfo.setLevel(nextLevel);
            log.info("start process level:{},name:{}",areaInfo.getLevel(),areaInfo.getName());
            //设置区域名称
            String[] names = areaInfo.getNames();
            names[nextLevel] = areaInfo.getName();
            //重新赋值
            areaInfo.setNames(names);
            //处理
            innerProcess(areaInfo);
            //递归该区域的下级
            processAreaInfo(areaInfo);
        }
    }

    /**
     * 处理办事指南核心逻辑
     * 1、抓取列表
     *    注：每级的列表URL略有不同，市、区级需要抓企业级的
     * 2、抓取详情
     * 3、入库
     * 注：每级的mateData不同
     *
     * @param areaInfo
     */
    public void innerProcess(AreaInfo areaInfo) throws InterruptedException {
        //爬取 办事指南列表数据
        List<SummaryInfo> totalList = new ArrayList<>();
        //市、区
        if(areaInfo.getLevel() == 1 ||  areaInfo.getLevel() == 2){
            //内部递归爬取每页,内部会有sleep
            List<SummaryInfo> infoList1 = SichuanGuideDataCrawler.fetchSummaryInfo(API_AREA_LIST_0,areaInfo.getCode(),1,new ArrayList<>());
            List<SummaryInfo> infoList2 = SichuanGuideDataCrawler.fetchSummaryInfo(API_AREA_LIST_1,areaInfo.getCode(),1,new ArrayList<>());
            if(!CollectionUtils.isEmpty(infoList1)){
                totalList.addAll(infoList1);
            }
            if(!CollectionUtils.isEmpty(infoList2)){
                totalList.addAll(infoList2);
            }
        }else if(areaInfo.getLevel() == 3){//镇
            totalList = SichuanGuideDataCrawler.fetchSummaryInfo(API_TOWN_LIST,areaInfo.getCode(),1,new ArrayList<>());
        }else if(areaInfo.getLevel() == 4){//村
            totalList = SichuanGuideDataCrawler.fetchSummaryInfo(API_VILLAGE_LIST,areaInfo.getCode(),1,new ArrayList<>());
        }
        if(CollectionUtils.isEmpty(totalList)){
            return;
        }
        for(SummaryInfo summaryInfo : totalList){
            log.info("process.level:{},name:{},title:{}",areaInfo.getLevel(),areaInfo.getName(),summaryInfo.getTitle());
            //抓取详情
            GovGuideParser.GuideInfo guideInfo = SichuanGuideDataCrawler.fetchGuideInfo(summaryInfo.getUrl());
            if(guideInfo == null){
                continue;
            }
            //传递参数
            guideInfo.setTitle(summaryInfo.getTitle());
            guideInfo.setUrl(summaryInfo.getUrl());
            //组装TextSegment
            List<TextSegment> textSegmentList = makeTextSegments(areaInfo,guideInfo);
            //存储
            persist(textSegmentList);
            log.info("process.level:{},name:{},title:{},success",areaInfo.getLevel(),areaInfo.getName(),summaryInfo.getTitle());
            //sleep
            Thread.sleep(2 * 1000L);
        }
    }

    /**
     * 组装TextSegment
     *
     * @param areaInfo
     * @param guideInfo
     */
    private List<TextSegment> makeTextSegments(AreaInfo areaInfo,GovGuideParser.GuideInfo guideInfo){
        //构造meta data
        Map<String,String> metaMap = new HashMap<>();
        metaMap.put("province", areaInfo.getNames()[0]);
        metaMap.put("city", areaInfo.getNames()[1]);
        metaMap.put("district", areaInfo.getNames()[2]);
        metaMap.put("street", areaInfo.getNames()[3]);
        metaMap.put("community", areaInfo.getNames()[4]);
        metaMap.put("full_path", String.join("/", areaInfo.getNames()));
        metaMap.put("title", guideInfo.getTitle());
        metaMap.put("url", guideInfo.getUrl());
        Metadata metadata = Metadata.from(metaMap);
        //给内容定义相同的事项title
        String title = "[事项] " + guideInfo.getTitle() + "\n\n";
        //构造Segment
        List<TextSegment> textSegmentList = new ArrayList<>();
        //基本信息
        textSegmentList.add(new TextSegment(title + guideInfo.getServiceInfo().toText() + "\n" + guideInfo.getBasicInfo().toText(),metadata));
        //材料清单
        textSegmentList.add(new TextSegment(title + guideInfo.getMaterialsText(),metadata));
        //处理流程
        textSegmentList.add(new TextSegment(title + guideInfo.getProcessStepsText(),metadata));
        //收费标准
        textSegmentList.add(new TextSegment(title + guideInfo.getFeeStandardText(),metadata));
        //受理条件
        textSegmentList.add(new TextSegment(title + guideInfo.getAcceptanceConditionsText(),metadata));
        //常见问题
        textSegmentList.add(new TextSegment(title + guideInfo.getFaqText(),metadata));
        return textSegmentList;
    }
    /**
     * 存库
     *
     * @param segments
     */
    private void persist(List<TextSegment> segments){
        if(CollectionUtils.isEmpty(segments)){
            return;
        }
        Response<List<Embedding>> embeddingsResponse = embeddingModel.embedAll(segments);
        log.debug("Finished embedding {} text segments", segments.size());
        // TODO handle failures, parallelize
        log.debug("Starting to store {} text segments into the embedding store", segments.size());
        embeddingStore.addAll(embeddingsResponse.content(), segments);
    }
}