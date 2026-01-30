package com.guidance.service;

import com.guidance.repository.AreaInfoStore;
import com.guidance.service.crawler.GovGuideParser;
import com.guidance.service.crawler.SichuanGuideDataCrawler;
import com.guidance.utils.StringUtils;
import com.guidance.vo.AreaInfo;
import com.guidance.vo.SummaryInfo;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.EmbeddingStore;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

@ConditionalOnProperty(name = "crawl.area.enable", havingValue = "true")
@Service
@Slf4j
public class AreaInfoService {
    @Autowired
    AreaInfoStore areaInfoStore;
    private final ExecutorService executor = Executors.newCachedThreadPool();
    public AreaInfoService() {}

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
    public void start(AreaInfo parentArea)  {
        log.info("start fetch child area:{},names:{}",parentArea.getName(),parentArea.getNames());
        //存储
        areaInfoStore.save(parentArea);
        //抓取编码下的子集区域编码信息
        List<AreaInfo> infoList = SichuanGuideDataCrawler.fetchAreaInfo(parentArea.getCode());
        if(CollectionUtils.isEmpty(infoList)){
            return;
        }
        //并行抓取
        infoList.parallelStream().forEach(areaInfo->{
            try {
                //设置为：上一级+1
                int nextLevel = parentArea.getLevel() + 1;
                areaInfo.setLevel(nextLevel);
                //设置区域名称
                String[] names = parentArea.getNames().clone();
                names[nextLevel] = areaInfo.getName();
                //重新赋值
                areaInfo.setNames(names);
                //
                Thread.sleep(2000L);
                //递归该区域的下级
                start(areaInfo);
            }catch (Exception e){
                log.error("process area info has error",e);
            }
        });
    }
}