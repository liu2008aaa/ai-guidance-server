package com.guidance.service;

import com.guidance.constants.RegionEnum;
import com.guidance.service.crawler.GovGuideParser;
import com.guidance.service.crawler.SichuanGuideDataCrawler;
import com.guidance.vo.AreaInfo;
import com.guidance.vo.SummaryInfo;
import com.guidance.utils.StringUtils;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.EmbeddingStore;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.guidance.service.crawler.SichuanGuideDataCrawler.*;

@ConditionalOnProperty(name = "crawl.guide.enable", havingValue = "true")
@Service
@Slf4j
public class KnowledgeService {
    private final EmbeddingStore<TextSegment> embeddingStore;
    private final EmbeddingModel embeddingModel;

    private final ExecutorService executor = Executors.newCachedThreadPool();

    public KnowledgeService(EmbeddingStore<TextSegment> embeddingStore, 
                            EmbeddingModel embeddingModel) {
        this.embeddingStore = embeddingStore;
        this.embeddingModel = embeddingModel;
    }

    /**
     * 入口
     */
    @PostConstruct
    public void crawlAndIngest() {
        executor.execute(() -> {
            try {
                Thread.sleep(15000L);
                String[] names = new String[5];
                String areaName = "四川省";
                names[0] = areaName;
                AreaInfo parentArea = AreaInfo.builder()
                        .code(AREA_CODE_SICHUAN)
                        .name(areaName)
                        .level(0)
                        .names(names)
                        .build();
                start(parentArea);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * 处理 区域code
     *
     * @param parentArea
     */
    public void start(AreaInfo parentArea){
        //抓取编码下的子集区域编码信息
        List<AreaInfo> infoList = SichuanGuideDataCrawler.fetchAreaInfo(parentArea.getCode());
        if(CollectionUtils.isEmpty(infoList)){
            return;
        }
        //并行遍历每个区域
        infoList.parallelStream().forEach(areaInfo->{
            try {
                //设置为：上一级+1
                int nextLevel = parentArea.getLevel() + 1;
                areaInfo.setLevel(nextLevel);
                log.info("start process level:{},name:{}", areaInfo.getLevel(), areaInfo.getName());
                //设置区域名称
                String[] names = parentArea.getNames().clone();
                names[nextLevel] = areaInfo.getName();
                //重新赋值
                areaInfo.setNames(names);
                //sleep
                Thread.sleep(5000L);
                //处理对应的指南信息
                innerProcess(areaInfo);
                //sleep
                Thread.sleep(5000L);
                //递归该区域的下级
                start(areaInfo);
            }catch (Exception e){
                log.error("process has error",e);
            }
        });
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
        String[] names = areaInfo.getNames();
        metaMap.put(RegionEnum.province.name(), StringUtils.getStringValue(names[0]));
        metaMap.put(RegionEnum.city.name(), StringUtils.getStringValue(names[1]));
        metaMap.put(RegionEnum.district.name(), StringUtils.getStringValue(names[2]));
        metaMap.put(RegionEnum.town.name(), StringUtils.getStringValue(names[3]));
        metaMap.put(RegionEnum.village.name(), StringUtils.getStringValue(names[4]));
        metaMap.put("full_path", String.join("/", areaInfo.getNames()));
        metaMap.put("title", guideInfo.getTitle());
        metaMap.put("url", guideInfo.getUrl());
        Metadata metadata = Metadata.from(metaMap);
        //给内容定义相同的事项title
        String title = "[事项] " + guideInfo.getTitle() + "\n";
        //构造Segment
        List<TextSegment> textSegmentList = new ArrayList<>();

        //咨询方式
        textSegmentList.add(new TextSegment(title + "咨询方式: \n" + guideInfo.getServiceInfo().getConsultationMethod(),metadata));
        //受理条件
//        textSegmentList.add(new TextSegment(title + guideInfo.getAcceptanceConditionsText(),metadata));
        //基本信息
        textSegmentList.add(new TextSegment(title + "原文链接：\n" + guideInfo.getUrl(),metadata));
        textSegmentList.add(new TextSegment(title + "办理时间: \n" + guideInfo.getServiceInfo().getProcessingTime(),metadata));
        textSegmentList.add(new TextSegment(title + "办理地点: \n" + guideInfo.getServiceInfo().getProcessingLocation(),metadata));
        //所需材料
        textSegmentList.add(new TextSegment(title + guideInfo.getMaterialsText(),metadata));
        //处理流程
//        textSegmentList.add(new TextSegment(title + guideInfo.getProcessStepsText(),metadata));
        //收费标准
//        textSegmentList.add(new TextSegment(title + guideInfo.getFeeStandardText(),metadata));
        //常见问题
//        textSegmentList.add(new TextSegment(title + guideInfo.getFaqText(),metadata));
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
        try {
            Response<List<Embedding>> embeddingsResponse = embeddingModel.embedAll(segments);
            log.debug("Finished embedding {} text segments", segments.size());
            // TODO handle failures, parallelize
            log.debug("Starting to store {} text segments into the embedding store", segments.size());
            embeddingStore.addAll(embeddingsResponse.content(), segments);
        }catch (Exception e){
            log.error("persist has error",e);
        }
    }
}