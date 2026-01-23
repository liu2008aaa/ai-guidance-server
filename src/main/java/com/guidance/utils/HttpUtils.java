package com.guidance.utils;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * http 工具类
 */
@Slf4j
public class HttpUtils {
    /**
     * 超时时间
     */
    private final static int TIMEOUT = 15000;
    /**
     * http get 请求
     *
     * @param url
     * @return
     */
    public static String getByHttp(String url){
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofMillis(TIMEOUT))
                    .header("Accept", "application/json")
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (IOException e) {
            log.error("getByHttp data has error",e);
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Jsoup get 请求
     *
     * @param url
     * @return
     */
    public static String getByJsoup(String url){
        try {
            org.jsoup.nodes.Document jsoupDoc = Jsoup.connect(url)
                    .timeout(TIMEOUT)
                    .userAgent("Mozilla/5.0")
                    .get();
            return (jsoupDoc!=null && jsoupDoc.body()!=null) ? jsoupDoc.body().text() : "";
        } catch (IOException e) {
            log.error("getByJsoup has error",e);
            throw new RuntimeException(e);
        }
    }


    /**
     * 从 jsonpcallback({...}) 中提取 {...}
     */
    public static String extractJsonFromJsonp(String jsonp) {
        if(ObjectUtils.isEmpty(jsonp)){
           return null;
        }
        // 匹配 jsonpcallback(...) 或任意回调名
        Pattern pattern = Pattern.compile("^\\w+\\((.*)\\);?$", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(jsonp.trim());

        if (matcher.find()) {
            return matcher.group(1);  // 返回括号内的内容
        }
        throw new IllegalArgumentException("非 JSONP 格式: " + jsonp);
    }
}
