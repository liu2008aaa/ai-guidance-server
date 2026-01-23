package com.guidance.service.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.*;

public class GovSummaryParser {
    
    public static void main(String[] args) {
        String html = "<input type=\"hidden\" id=\"zisizeDept\" value=\"663\">\n" +
                "<input type=\"hidden\" id=\"fusizeDept\" value=\"384\">\n" +
                "<input type=\"hidden\" id=\"onsizeDept\" value=\"651\">\n" +
                "<input type=\"hidden\" id=\"pageNoDept\" value=\"1\">\n" +
                "<input type=\"hidden\" id=\"pageNumDept\" value=\"26\">\n" +
                "<input type=\"hidden\" id=\"result_countDept\" value=\"663\">\n" +
                "<input type=\"hidden\" id=\"currsumpageDept\" value=\"5\">\n" +
                "<div class=\"sx_list\">\n" +
                "    <div class=\"sub_list1_main\">\n" +
                "        <span title=\"其他印刷品印刷设立审批\">\n" +
                "            <a onclick=\"ywblurl('https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0000400004-510100000000-000-11510100009172079Q-1-00&taskType=1&deptCode=3900629211605946368')\">其他印刷品印刷设立审批</a>\n" +
                "        </span>\n" +
                "        <div class=\"bs_btn one_c fr\">\n" +
                "            <a onclick=\"ywblurl('https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0000400004-510100000000-000-11510100009172079Q-1-00&taskType=1&deptCode=3900629211605946368')\" class=\"icon1\">办事指南</a>\n" +
                "            <a onclick=\"evaluationChengDu('511A0000400004-510100000000-000-11510100009172079Q-1-00','3900629211605946368','https://www.sczwfw.gov.cn/jiq/front/transition/evaluation?areaCode=510100000000&eventCode=511A0000400004-510100000000-000-11510100009172079Q-1-00');\" href=\"javascript:void(0)\" class=\"icon2\">好差评</a>\n" +
                "            <a onclick=\"collect('其他印刷品印刷设立审批', 'https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0000400004-510100000000-000-11510100009172079Q-1-00&taskType=1&deptCode=3900629211605946368');\" href=\"javascript:void(0)\" class=\"icon3\">订阅</a>\n" +
                "            <a onclick=\"checkIsOnlineChengDu('511A0000400004-510100000000-000-11510100009172079Q-1-00', '2^1', '101', 'http://zxbl.sczwfw.gov.cn/app/presonServices/netApply/4745347671228579840/0', '0', '11059136', '510100000000','3900629211605946368');\" href=\"javascript:void(0)\" class=\"icon4\">申请</a>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</div>\n" +
                "<div class=\"sx_list\">\n" +
                "    <div id=\"bm510107009006\" class=\"sub_list1_main\" onclick=\"clickzhu('bm510107009006')\">\n" +
                "        <span title=\"地面无线电台（站）设置、使用许可\">\n" +
                "            <a href=\"javascript:void(0)\">地面无线电台（站）设置、使用许可</a>\n" +
                "        </span>\n" +
                "        <span class=\"sx_btn\">4项</span>\n" +
                "    </div>\n" +
                "    <ul class=\"sub_list2_main\">\n" +
                "        <li>\n" +
                "            <a title=\"地面无线电台（站）（业余无线电台除外）设置、使用许可事项变更申请\" onclick=\"ywblurl('https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0700900014-510000000000-000-11510000699166855E-1-00&taskType=1&deptCode=3041')\" style=\"cursor:pointer\">地面无线电台（站）（业余无线电台除外）设置、使用许可事项变更申请</a>\n" +
                "            <div class=\"bs_btn fr\">\n" +
                "                <a onclick=\"ywblurl('https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0700900014-510000000000-000-11510000699166855E-1-00&taskType=1&deptCode=3041')\" class=\"icon1\" style=\"cursor:pointer\">办事指南</a>\n" +
                "                <a onclick=\"evaluationChengDu('511A0700900014-510000000000-000-11510000699166855E-1-00','3041','https://www.sczwfw.gov.cn/jiq/front/transition/evaluation?areaCode=510100000000&eventCode=511A0700900014-510000000000-000-11510000699166855E-1-00');\" href=\"javascript:void(0)\" class=\"icon2\">好差评</a>\n" +
                "                <a onclick=\"collect('地面无线电台（站）（业余无线电台除外）设置、使用许可事项变更申请', 'https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0700900014-510000000000-000-11510000699166855E-1-00&taskType=1&deptCode=3041');\" href=\"javascript:void(0)\" class=\"icon3\">订阅</a>\n" +
                "                <a onclick=\"checkIsOnlineChengDu('511A0700900014-510000000000-000-11510000699166855E-1-00', '3^4^6^2^1', '101', 'https://open.sczwfw.gov.cn/open-platform-oauth/oauth/authorize?client_id=0003&scope=user_info&code=1&response_type=code&redirect_uri=https://open.sczwfw.gov.cn/open-platform-exchange/exchange/getResource?resourceCode=ZYZC2022082600269059%26applyCode=YYSQ2022090500002466%26sysCode=0003', '1', '4389834966533177344', '510100000000', '3041');\" href=\"javascript:void(0)\" class=\"icon4\">申请</a>\n" +
                "            </div>\n" +
                "        </li>\n" +
                "        <li>\n" +
                "            <a title=\"地面无线电台（站）（业余无线电台除外）设置、使用许可有效期届满延续申请\" onclick=\"ywblurl('https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0022300006-510000000000-000-699166855-1-00&taskType=1&deptCode=3041')\" style=\"cursor:pointer\">地面无线电台（站）（业余无线电台除外）设置、使用许可有效期届满延续申请</a>\n" +
                "            <div class=\"bs_btn fr\">\n" +
                "                <a onclick=\"ywblurl('https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0022300006-510000000000-000-699166855-1-00&taskType=1&deptCode=3041')\" class=\"icon1\" style=\"cursor:pointer\">办事指南</a>\n" +
                "                <a onclick=\"evaluationChengDu('511A0022300006-510000000000-000-699166855-1-00','3041','https://www.sczwfw.gov.cn/jiq/front/transition/evaluation?areaCode=510100000000&eventCode=511A0022300006-510000000000-000-699166855-1-00');\" href=\"javascript:void(0)\" class=\"icon2\">好差评</a>\n" +
                "                <a onclick=\"collect('地面无线电台（站）（业余无线电台除外）设置、使用许可有效期届满延续申请', 'https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0022300006-510000000000-000-699166855-1-00&taskType=1&deptCode=3041');\" href=\"javascript:void(0)\" class=\"icon3\">订阅</a>\n" +
                "                <a onclick=\"checkIsOnlineChengDu('511A0022300006-510000000000-000-699166855-1-00', '3^4^9^2^1', '101', 'https://open.sczwfw.gov.cn/open-platform-oauth/oauth/authorize?client_id=0003&scope=user_info&code=1&response_type=code&redirect_uri=https://open.sczwfw.gov.cn/open-platform-exchange/exchange/getResource?resourceCode=ZYZC2022082600269057%26applyCode=YYSQ2022090500002466%26sysCode=0003', '1', '4256077738358374400', '510100000000', '3041');\" href=\"javascript:void(0)\" class=\"icon4\">申请</a>\n" +
                "            </div>\n" +
                "        </li>\n" +
                "        <li>\n" +
                "            <a title=\"地面无线电台（站）（业余无线电台除外）设置、使用许可首次申请\" onclick=\"ywblurl('https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0022300005-510000000000-000-699166855-1-00&taskType=1&deptCode=3041')\" style=\"cursor:pointer\">地面无线电台（站）（业余无线电台除外）设置、使用许可首次申请</a>\n" +
                "            <div class=\"bs_btn fr\">\n" +
                "                <a onclick=\"ywblurl('https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0022300005-510000000000-000-699166855-1-00&taskType=1&deptCode=3041')\" class=\"icon1\" style=\"cursor:pointer\">办事指南</a>\n" +
                "                <a onclick=\"evaluationChengDu('511A0022300005-510000000000-000-699166855-1-00','3041','https://www.sczwfw.gov.cn/jiq/front/transition/evaluation?areaCode=510100000000&eventCode=511A0022300005-510000000000-000-699166855-1-00');\" href=\"javascript:void(0)\" class=\"icon2\">好差评</a>\n" +
                "                <a onclick=\"collect('地面无线电台（站）（业余无线电台除外）设置、使用许可首次申请', 'https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0022300005-510000000000-000-699166855-1-00&taskType=1&deptCode=3041');\" href=\"javascript:void(0)\" class=\"icon3\">订阅</a>\n" +
                "                <a onclick=\"checkIsOnlineChengDu('511A0022300005-510000000000-000-699166855-1-00', '3^4^9^2^1', '101', 'https://open.sczwfw.gov.cn/open-platform-oauth/oauth/authorize?client_id=0003&scope=user_info&code=1&response_type=code&redirect_uri=https://open.sczwfw.gov.cn/open-platform-exchange/exchange/getResource?resourceCode=ZYZC2022082600269063%26applyCode=YYSQ2022090500002466%26sysCode=0003', '1', '4256063769174114304', '510100000000', '3041');\" href=\"javascript:void(0)\" class=\"icon4\">申请</a>\n" +
                "            </div>\n" +
                "        </li>\n" +
                "        <li>\n" +
                "            <a title=\"地面无线电台（站）（业余无线电台除外）设置、使用许可注销申请\" onclick=\"ywblurl('https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0022300007-510000000000-000-11510000699166855E-1-00&taskType=1&deptCode=3041')\" style=\"cursor:pointer\">地面无线电台（站）（业余无线电台除外）设置、使用许可注销申请</a>\n" +
                "            <div class=\"bs_btn fr\">\n" +
                "                <a onclick=\"ywblurl('https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0022300007-510000000000-000-11510000699166855E-1-00&taskType=1&deptCode=3041')\" class=\"icon1\" style=\"cursor:pointer\">办事指南</a>\n" +
                "                <a onclick=\"evaluationChengDu('511A0022300007-510000000000-000-11510000699166855E-1-00','3041','https://www.sczwfw.gov.cn/jiq/front/transition/evaluation?areaCode=510100000000&eventCode=511A0022300007-510000000000-000-11510000699166855E-1-00');\" href=\"javascript:void(0)\" class=\"icon2\">好差评</a>\n" +
                "                <a onclick=\"collect('地面无线电台（站）（业余无线电台除外）设置、使用许可注销申请', 'https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0022300007-510000000000-000-11510000699166855E-1-00&taskType=1&deptCode=3041');\" href=\"javascript:void(0)\" class=\"icon3\">订阅</a>\n" +
                "                <a onclick=\"checkIsOnlineChengDu('511A0022300007-510000000000-000-11510000699166855E-1-00', '3^4^9^2^1', '101', 'https://open.sczwfw.gov.cn/open-platform-oauth/oauth/authorize?client_id=0003&scope=user_info&code=1&response_type=code&redirect_uri=https://open.sczwfw.gov.cn/open-platform-exchange/exchange/getResource?resourceCode=ZYZC2022082600269058%26applyCode=YYSQ2022090500002466%26sysCode=0003', '1', '4256078068685111296', '510100000000', '3041');\" href=\"javascript:void(0)\" class=\"icon4\">申请</a>\n" +
                "            </div>\n" +
                "        </li>\n" +
                "    </ul>\n" +
                "</div>\n" +
                "<div class=\"sx_list\">\n" +
                "    <div class=\"sub_list1_main\">\n" +
                "        <span title=\"企业投资项目核准(技术改造类除外)\">\n" +
                "            <a onclick=\"ywblurl('https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A04001005-510100000000-000-115101000091720521-1-00&taskType=1&deptCode=1112233')\">企业投资项目核准(技术改造类除外)</a>\n" +
                "        </span>\n" +
                "        <div class=\"bs_btn one_c fr\">\n" +
                "            <a onclick=\"ywblurl('https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A04001005-510100000000-000-115101000091720521-1-00&taskType=1&deptCode=1112233')\" class=\"icon1\">办事指南</a>\n" +
                "            <a onclick=\"evaluationChengDu('511A04001005-510100000000-000-115101000091720521-1-00','1112233','https://www.sczwfw.gov.cn/jiq/front/transition/evaluation?areaCode=510100000000&eventCode=511A04001005-510100000000-000-115101000091720521-1-00');\" href=\"javascript:void(0)\" class=\"icon2\">好差评</a>\n" +
                "            <a onclick=\"collect('企业投资项目核准(技术改造类除外)', 'https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A04001005-510100000000-000-115101000091720521-1-00&taskType=1&deptCode=1112233');\" href=\"javascript:void(0)\" class=\"icon3\">订阅</a>\n" +
                "            <a onclick=\"checkIsOnlineChengDu('511A04001005-510100000000-000-115101000091720521-1-00', '3^9^2^1', '101', 'https://tzxm.sczwfw.gov.cn/', '0', '11313630', '510100000000','1112233');\" href=\"javascript:void(0)\" class=\"icon4\">申请</a>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</div>\n" +
                "<div class=\"sx_list\">\n" +
                "    <div id=\"bm510105004000\" class=\"sub_list1_main\" onclick=\"clickzhu('bm510105004000')\">\n" +
                "        <span title=\"实施专科教育的高等学校和其他高等教育机构的设立、变更和终止审批\">\n" +
                "            <a href=\"javascript:void(0)\">实施专科教育的高等学校和其他高等教育机构的设立、变更和终止审批</a>\n" +
                "        </span>\n" +
                "        <span class=\"sx_btn\">3项</span>\n" +
                "    </div>\n" +
                "    <ul class=\"sub_list2_main\">\n" +
                "        <li>\n" +
                "            <a title=\"其他高等教育机构变更审批\" onclick=\"ywblurl('https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0050100029-510100000000-000-115101000091722390-1-00&taskType=1&deptCode=115101000091722390')\" style=\"cursor:pointer\">其他高等教育机构变更审批</a>\n" +
                "            <div class=\"bs_btn fr\">\n" +
                "                <a onclick=\"ywblurl('https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0050100029-510100000000-000-115101000091722390-1-00&taskType=1&deptCode=115101000091722390')\" class=\"icon1\" style=\"cursor:pointer\">办事指南</a>\n" +
                "                <a onclick=\"evaluationChengDu('511A0050100029-510100000000-000-115101000091722390-1-00','115101000091722390','https://www.sczwfw.gov.cn/jiq/front/transition/evaluation?areaCode=510100000000&eventCode=511A0050100029-510100000000-000-115101000091722390-1-00');\" href=\"javascript:void(0)\" class=\"icon2\">好差评</a>\n" +
                "                <a onclick=\"collect('其他高等教育机构变更审批', 'https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0050100029-510100000000-000-115101000091722390-1-00&taskType=1&deptCode=115101000091722390');\" href=\"javascript:void(0)\" class=\"icon3\">订阅</a>\n" +
                "                <a onclick=\"checkIsOnlineChengDu('511A0050100029-510100000000-000-115101000091722390-1-00', '3^4^9^2^1', '101', 'http://zxbl.sczwfw.gov.cn/app/presonServices/netApply/4643162643505905664/0', '0', '10600801', '510100000000', '115101000091722390');\" href=\"javascript:void(0)\" class=\"icon4\">申请</a>\n" +
                "            </div>\n" +
                "        </li>\n" +
                "        <li>\n" +
                "            <a title=\"其他高等教育机构设立审批\" onclick=\"ywblurl('https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0050100025-510100000000-000-115101000091722390-1-00&taskType=1&deptCode=115101000091722390')\" style=\"cursor:pointer\">其他高等教育机构设立审批</a>\n" +
                "            <div class=\"bs_btn fr\">\n" +
                "                <a onclick=\"ywblurl('https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0050100025-510100000000-000-115101000091722390-1-00&taskType=1&deptCode=115101000091722390')\" class=\"icon1\" style=\"cursor:pointer\">办事指南</a>\n" +
                "                <a onclick=\"evaluationChengDu('511A0050100025-510100000000-000-115101000091722390-1-00','115101000091722390','https://www.sczwfw.gov.cn/jiq/front/transition/evaluation?areaCode=510100000000&eventCode=511A0050100025-510100000000-000-115101000091722390-1-00');\" href=\"javascript:void(0)\" class=\"icon2\">好差评</a>\n" +
                "                <a onclick=\"collect('其他高等教育机构设立审批', 'https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0050100025-510100000000-000-115101000091722390-1-00&taskType=1&deptCode=115101000091722390');\" href=\"javascript:void(0)\" class=\"icon3\">订阅</a>\n" +
                "                <a onclick=\"checkIsOnlineChengDu('511A0050100025-510100000000-000-115101000091722390-1-00', '3^4^9^2^1', '101', 'http://zxbl.sczwfw.gov.cn/app/presonServices/netApply/4643162639294824448/0', '0', '10600803', '510100000000', '115101000091722390');\" href=\"javascript:void(0)\" class=\"icon4\">申请</a>\n" +
                "            </div>\n" +
                "        </li>\n" +
                "        <li>\n" +
                "            <a title=\"其他高等教育机构筹设审批\" onclick=\"ywblurl('https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0050100023-510100000000-000-115101000091722390-1-00&taskType=1&deptCode=115101000091722390')\" style=\"cursor:pointer\">其他高等教育机构筹设审批</a>\n" +
                "            <div class=\"bs_btn fr\">\n" +
                "                <a onclick=\"ywblurl('https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0050100023-510100000000-000-115101000091722390-1-00&taskType=1&deptCode=115101000091722390')\" class=\"icon1\" style=\"cursor:pointer\">办事指南</a>\n" +
                "                <a onclick=\"evaluationChengDu('511A0050100023-510100000000-000-115101000091722390-1-00','115101000091722390','https://www.sczwfw.gov.cn/jiq/front/transition/evaluation?areaCode=510100000000&eventCode=511A0050100023-510100000000-000-115101000091722390-1-00');\" href=\"javascript:void(0)\" class=\"icon2\">好差评</a>\n" +
                "                <a onclick=\"collect('其他高等教育机构筹设审批', 'https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0050100023-510100000000-000-115101000091722390-1-00&taskType=1&deptCode=115101000091722390');\" href=\"javascript:void(0)\" class=\"icon3\">订阅</a>\n" +
                "                <a onclick=\"checkIsOnlineChengDu('511A0050100023-510100000000-000-115101000091722390-1-00', '3^4^9^2^1', '101', 'http://zxbl.sczwfw.gov.cn/app/presonServices/netApply/4643162641660411904/0', '0', '10600802', '510100000000', '115101000091722390');\" href=\"javascript:void(0)\" class=\"icon4\">申请</a>\n" +
                "            </div>\n" +
                "        </li>\n" +
                "    </ul>\n" +
                "</div>\n" +
                "<div class=\"sx_list\">\n" +
                "    <div class=\"sub_list1_main\">\n" +
                "        <span title=\"教师资格认定\">\n" +
                "            <a onclick=\"ywblurl('https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0138600000-510100000000-000-115101000091722390-1-00&taskType=1&deptCode=115101000091722390')\">教师资格认定</a>\n" +
                "        </span>\n" +
                "        <div class=\"bs_btn one_c fr\">\n" +
                "            <a onclick=\"ywblurl('https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0138600000-510100000000-000-115101000091722390-1-00&taskType=1&deptCode=115101000091722390')\" class=\"icon1\">办事指南</a>\n" +
                "            <a onclick=\"evaluationChengDu('511A0138600000-510100000000-000-115101000091722390-1-00','115101000091722390','https://www.sczwfw.gov.cn/jiq/front/transition/evaluation?areaCode=510100000000&eventCode=511A0138600000-510100000000-000-115101000091722390-1-00');\" href=\"javascript:void(0)\" class=\"icon2\">好差评</a>\n" +
                "            <a onclick=\"collect('教师资格认定', 'https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0138600000-510100000000-000-115101000091722390-1-00&taskType=1&deptCode=115101000091722390');\" href=\"javascript:void(0)\" class=\"icon3\">订阅</a>\n" +
                "            <a onclick=\"checkIsOnlineChengDu('511A0138600000-510100000000-000-115101000091722390-1-00', '1', '101', 'https://sso1.jszg.edu.cn/sso/login.html?business=1', '0', '11132956', '510100000000','115101000091722390');\" href=\"javascript:void(0)\" class=\"icon4\">申请</a>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</div>\n" +
                "<div class=\"sx_list\">\n" +
                "    <div class=\"sub_list1_main\">\n" +
                "        <span title=\"举行集会游行示威许可\">\n" +
                "            <a onclick=\"ywblurl('https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0151500000-510100000000-000-15667888-1-00&taskType=1&deptCode=15667888')\">举行集会游行示威许可</a>\n" +
                "        </span>\n" +
                "        <div class=\"bs_btn one_c fr\">\n" +
                "            <a onclick=\"ywblurl('https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0151500000-510100000000-000-15667888-1-00&taskType=1&deptCode=15667888')\" class=\"icon1\">办事指南</a>\n" +
                "            <a onclick=\"evaluationChengDu('511A0151500000-510100000000-000-15667888-1-00','15667888','https://www.sczwfw.gov.cn/jiq/front/transition/evaluation?areaCode=510100000000&eventCode=511A0151500000-510100000000-000-15667888-1-00');\" href=\"javascript:void(0)\" class=\"icon2\">好差评</a>\n" +
                "            <a onclick=\"collect('举行集会游行示威许可', 'https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0151500000-510100000000-000-15667888-1-00&taskType=1&deptCode=15667888');\" href=\"javascript:void(0)\" class=\"icon3\">订阅</a>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</div>\n" +
                "<div class=\"sx_list\">\n" +
                "    <div class=\"sub_list1_main\">\n" +
                "        <span title=\"固定资产投资项目节能审查（企业技术改造项目除外）\">\n" +
                "            <a onclick=\"ywblurl('https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0108100001-510100000000-000-1112233-1-00&taskType=1&deptCode=1112233')\">固定资产投资项目节能审查（企业技术改造项目除外）</a>\n" +
                "        </span>\n" +
                "        <div class=\"bs_btn one_c fr\">\n" +
                "            <a onclick=\"ywblurl('https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0108100001-510100000000-000-1112233-1-00&taskType=1&deptCode=1112233')\" class=\"icon1\">办事指南</a>\n" +
                "            <a onclick=\"evaluationChengDu('511A0108100001-510100000000-000-1112233-1-00','1112233','https://www.sczwfw.gov.cn/jiq/front/transition/evaluation?areaCode=510100000000&eventCode=511A0108100001-510100000000-000-1112233-1-00');\" href=\"javascript:void(0)\" class=\"icon2\">好差评</a>\n" +
                "            <a onclick=\"collect('固定资产投资项目节能审查（企业技术改造项目除外）', 'https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0108100001-510100000000-000-1112233-1-00&taskType=1&deptCode=1112233');\" href=\"javascript:void(0)\" class=\"icon3\">订阅</a>\n" +
                "            <a onclick=\"checkIsOnlineChengDu('511A0108100001-510100000000-000-1112233-1-00', '3^4^9^5^2^1', '101', 'http://tzxm.sczwfw.gov.cn/', '0', '10347971', '510100000000','1112233');\" href=\"javascript:void(0)\" class=\"icon4\">申请</a>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</div>\n" +
                "<div class=\"sx_list\">\n" +
                "    <div class=\"sub_list1_main\">\n" +
                "        <span title=\"筹备设立寺院、宫观、清真寺、教堂审批\">\n" +
                "            <a onclick=\"ywblurl('https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0033400001-510100000000-000-11510100009171789A-1-00&taskType=1&deptCode=11510100009171789A')\">筹备设立寺院、宫观、清真寺、教堂审批</a>\n" +
                "        </span>\n" +
                "        <div class=\"bs_btn one_c fr\">\n" +
                "            <a onclick=\"ywblurl('https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0033400001-510100000000-000-11510100009171789A-1-00&taskType=1&deptCode=11510100009171789A')\" class=\"icon1\">办事指南</a>\n" +
                "            <a onclick=\"evaluationChengDu('511A0033400001-510100000000-000-11510100009171789A-1-00','11510100009171789A','https://www.sczwfw.gov.cn/jiq/front/transition/evaluation?areaCode=510100000000&eventCode=511A0033400001-510100000000-000-11510100009171789A-1-00');\" href=\"javascript:void(0)\" class=\"icon2\">好差评</a>\n" +
                "            <a onclick=\"collect('筹备设立寺院、宫观、清真寺、教堂审批', 'https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0033400001-510100000000-000-11510100009171789A-1-00&taskType=1&deptCode=11510100009171789A');\" href=\"javascript:void(0)\" class=\"icon3\">订阅</a>\n" +
                "            <a onclick=\"checkIsOnlineChengDu('511A0033400001-510100000000-000-11510100009171789A-1-00', '3^4^9^2^1', '101', 'http://zxbl.sczwfw.gov.cn/app/presonServices/netApply/4745707727413084160/0', '0', '11068419', '510100000000','11510100009171789A');\" href=\"javascript:void(0)\" class=\"icon4\">申请</a>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</div>\n" +
                "<div class=\"sx_list\">\n" +
                "    <div class=\"sub_list1_main\">\n" +
                "        <span title=\"台湾居民来往大陆通行证签发\">\n" +
                "            <a onclick=\"ywblurl('https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=A-004220-4220-510100000000-000-15667888-1-00&taskType=1&deptCode=15667888')\">台湾居民来往大陆通行证签发</a>\n" +
                "        </span>\n" +
                "        <div class=\"bs_btn one_c fr\">\n" +
                "            <a onclick=\"ywblurl('https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=A-004220-4220-510100000000-000-15667888-1-00&taskType=1&deptCode=15667888')\" class=\"icon1\">办事指南</a>\n" +
                "            <a onclick=\"evaluationChengDu('A-004220-4220-510100000000-000-15667888-1-00','15667888','https://www.sczwfw.gov.cn/jiq/front/transition/evaluation?areaCode=510100000000&eventCode=A-004220-4220-510100000000-000-15667888-1-00');\" href=\"javascript:void(0)\" class=\"icon2\">好差评</a>\n" +
                "            <a onclick=\"collect('台湾居民来往大陆通行证签发', 'https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=A-004220-4220-510100000000-000-15667888-1-00&taskType=1&deptCode=15667888');\" href=\"javascript:void(0)\" class=\"icon3\">订阅</a>\n" +
                "            <a onclick=\"checkIsOnlineChengDu('A-004220-4220-510100000000-000-15667888-1-00', '1', '102', 'http://zxbl.sczwfw.gov.cn/app/presonServices/netApply/4633992296416313344/0', '0', '10570814', '510100000000','15667888');\" href=\"javascript:void(0)\" class=\"icon4\">申请</a>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</div>\n" +
                "<div class=\"sx_list\">\n" +
                "    <div id=\"bm510106001001\" class=\"sub_list1_main\" onclick=\"clickzhu('bm510106001001')\">\n" +
                "        <span title=\"外国人来华工作许可(高端、专业人才)\">\n" +
                "            <a href=\"javascript:void(0)\">外国人来华工作许可(高端、专业人才)</a>\n" +
                "        </span>\n" +
                "        <span class=\"sx_btn\">5项</span>\n" +
                "    </div>\n" +
                "    <ul class=\"sub_list2_main\">\n" +
                "        <li>\n" +
                "            <a title=\"外国人来华工作许可延期审批(高端、专业人才)\" onclick=\"ywblurl('https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0055300011-510100000000-000-11510100MB0U99044Q-1-00&taskType=1&deptCode=2824')\" style=\"cursor:pointer\">外国人来华工作许可延期审批(高端、专业人才)</a>\n" +
                "            <div class=\"bs_btn fr\">\n" +
                "                <a onclick=\"ywblurl('https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0055300011-510100000000-000-11510100MB0U99044Q-1-00&taskType=1&deptCode=2824')\" class=\"icon1\" style=\"cursor:pointer\">办事指南</a>\n" +
                "                <a onclick=\"evaluationChengDu('511A0055300011-510100000000-000-11510100MB0U99044Q-1-00','2824','https://www.sczwfw.gov.cn/jiq/front/transition/evaluation?areaCode=510100000000&eventCode=511A0055300011-510100000000-000-11510100MB0U99044Q-1-00');\" href=\"javascript:void(0)\" class=\"icon2\">好差评</a>\n" +
                "                <a onclick=\"collect('外国人来华工作许可延期审批(高端、专业人才)', 'https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0055300011-510100000000-000-11510100MB0U99044Q-1-00&taskType=1&deptCode=2824');\" href=\"javascript:void(0)\" class=\"icon3\">订阅</a>\n" +
                "                <a onclick=\"checkIsOnlineChengDu('511A0055300011-510100000000-000-11510100MB0U99044Q-1-00', '3^4^2^1', '101', 'https://fuwu.most.gov.cn/html/fwsx/wgrlhzq/', '0', '10891123', '510100000000', '2824');\" href=\"javascript:void(0)\" class=\"icon4\">申请</a>\n" +
                "            </div>\n" +
                "        </li>\n" +
                "        <li>\n" +
                "            <a title=\"外国人来华工作许可注销审批(高端、专业人才)\" onclick=\"ywblurl('https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0055300007-510100000000-000-11510100MB0U99044Q-1-00&taskType=1&deptCode=2824')\" style=\"cursor:pointer\">外国人来华工作许可注销审批(高端、专业人才)</a>\n" +
                "            <div class=\"bs_btn fr\">\n" +
                "                <a onclick=\"ywblurl('https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0055300007-510100000000-000-11510100MB0U99044Q-1-00&taskType=1&deptCode=2824')\" class=\"icon1\" style=\"cursor:pointer\">办事指南</a>\n" +
                "                <a onclick=\"evaluationChengDu('511A0055300007-510100000000-000-11510100MB0U99044Q-1-00','2824','https://www.sczwfw.gov.cn/jiq/front/transition/evaluation?areaCode=510100000000&eventCode=511A0055300007-510100000000-000-11510100MB0U99044Q-1-00');\" href=\"javascript:void(0)\" class=\"icon2\">好差评</a>\n" +
                "                <a onclick=\"collect('外国人来华工作许可注销审批(高端、专业人才)', 'https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0055300007-510100000000-000-11510100MB0U99044Q-1-00&taskType=1&deptCode=2824');\" href=\"javascript:void(0)\" class=\"icon3\">订阅</a>\n" +
                "                <a onclick=\"checkIsOnlineChengDu('511A0055300007-510100000000-000-11510100MB0U99044Q-1-00', '3^4^2^1', '101', 'https://fuwu.most.gov.cn/html/fwsx/wgrlhzq/', '0', '10891124', '510100000000', '2824');\" href=\"javascript:void(0)\" class=\"icon4\">申请</a>\n" +
                "            </div>\n" +
                "        </li>\n" +
                "        <li>\n" +
                "            <a title=\"外国人来华工作许可变更审批(高端、专业人才)\" onclick=\"ywblurl('https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0055300008-510100000000-000-11510100MB0U99044Q-1-00&taskType=1&deptCode=2824')\" style=\"cursor:pointer\">外国人来华工作许可变更审批(高端、专业人才)</a>\n" +
                "            <div class=\"bs_btn fr\">\n" +
                "                <a onclick=\"ywblurl('https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0055300008-510100000000-000-11510100MB0U99044Q-1-00&taskType=1&deptCode=2824')\" class=\"icon1\" style=\"cursor:pointer\">办事指南</a>\n" +
                "                <a onclick=\"evaluationChengDu('511A0055300008-510100000000-000-11510100MB0U99044Q-1-00','2824','https://www.sczwfw.gov.cn/jiq/front/transition/evaluation?areaCode=510100000000&eventCode=511A0055300008-510100000000-000-11510100MB0U99044Q-1-00');\" href=\"javascript:void(0)\" class=\"icon2\">好差评</a>\n" +
                "                <a onclick=\"collect('外国人来华工作许可变更审批(高端、专业人才)', 'https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0055300008-510100000000-000-11510100MB0U99044Q-1-00&taskType=1&deptCode=2824');\" href=\"javascript:void(0)\" class=\"icon3\">订阅</a>\n" +
                "                <a onclick=\"checkIsOnlineChengDu('511A0055300008-510100000000-000-11510100MB0U99044Q-1-00', '3^4^2^1', '101', 'https://fuwu.most.gov.cn/html/fwsx/wgrlhzq/', '0', '10891125', '510100000000', '2824');\" href=\"javascript:void(0)\" class=\"icon4\">申请</a>\n" +
                "            </div>\n" +
                "        </li>\n" +
                "        <li>\n" +
                "            <a title=\"境内外国人来华工作许可(高端、专业人才)\" onclick=\"ywblurl('https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0055300010-510100000000-000-11510100MB0U99044Q-1-00&taskType=1&deptCode=2824')\" style=\"cursor:pointer\">境内外国人来华工作许可(高端、专业人才)</a>\n" +
                "            <div class=\"bs_btn fr\">\n" +
                "                <a onclick=\"ywblurl('https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0055300010-510100000000-000-11510100MB0U99044Q-1-00&taskType=1&deptCode=2824')\" class=\"icon1\" style=\"cursor:pointer\">办事指南</a>\n" +
                "                <a onclick=\"evaluationChengDu('511A0055300010-510100000000-000-11510100MB0U99044Q-1-00','2824','https://www.sczwfw.gov.cn/jiq/front/transition/evaluation?areaCode=510100000000&eventCode=511A0055300010-510100000000-000-11510100MB0U99044Q-1-00');\" href=\"javascript:void(0)\" class=\"icon2\">好差评</a>\n" +
                "                <a onclick=\"collect('境内外国人来华工作许可(高端、专业人才)', 'https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0055300010-510100000000-000-11510100MB0U99044Q-1-00&taskType=1&deptCode=2824');\" href=\"javascript:void(0)\" class=\"icon3\">订阅</a>\n" +
                "                <a onclick=\"checkIsOnlineChengDu('511A0055300010-510100000000-000-11510100MB0U99044Q-1-00', '3^4^2^1', '101', 'https://fuwu.most.gov.cn/html/fwsx/wgrlhzq/', '0', '10891122', '510100000000', '2824');\" href=\"javascript:void(0)\" class=\"icon4\">申请</a>\n" +
                "            </div>\n" +
                "        </li>\n" +
                "        <li>\n" +
                "            <a title=\"境外外国人来华工作许可(高端、专业人才)\" onclick=\"ywblurl('https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0055300009-510100000000-000-11510100MB0U99044Q-1-00&taskType=1&deptCode=2824')\" style=\"cursor:pointer\">境外外国人来华工作许可(高端、专业人才)</a>\n" +
                "            <div class=\"bs_btn fr\">\n" +
                "                <a onclick=\"ywblurl('https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0055300009-510100000000-000-11510100MB0U99044Q-1-00&taskType=1&deptCode=2824')\" class=\"icon1\" style=\"cursor:pointer\">办事指南</a>\n" +
                "                <a onclick=\"evaluationChengDu('511A0055300009-510100000000-000-11510100MB0U99044Q-1-00','2824','https://www.sczwfw.gov.cn/jiq/front/transition/evaluation?areaCode=510100000000&eventCode=511A0055300009-510100000000-000-11510100MB0U99044Q-1-00');\" href=\"javascript:void(0)\" class=\"icon2\">好差评</a>\n" +
                "                <a onclick=\"collect('境外外国人来华工作许可(高端、专业人才)', 'https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0055300009-510100000000-000-11510100MB0U99044Q-1-00&taskType=1&deptCode=2824');\" href=\"javascript:void(0)\" class=\"icon3\">订阅</a>\n" +
                "                <a onclick=\"checkIsOnlineChengDu('511A0055300009-510100000000-000-11510100MB0U99044Q-1-00', '3^4^2^1', '101', 'https://fuwu.most.gov.cn/html/fwsx/wgrlhzq/', '0', '10891126', '510100000000', '2824');\" href=\"javascript:void(0)\" class=\"icon4\">申请</a>\n" +
                "            </div>\n" +
                "        </li>\n" +
                "    </ul>\n" +
                "</div>\n" +
                "<div class=\"sx_list\">\n" +
                "    <div class=\"sub_list1_main\">\n" +
                "        <span title=\"企业投资项目核准（技术改造）(市级)\">\n" +
                "            <a onclick=\"ywblurl('https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A07001005-510100000000-000-11510100562015685X-1-00&taskType=1&deptCode=3041')\">企业投资项目核准（技术改造）(市级)</a>\n" +
                "        </span>\n" +
                "        <div class=\"bs_btn one_c fr\">\n" +
                "            <a onclick=\"ywblurl('https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A07001005-510100000000-000-11510100562015685X-1-00&taskType=1&deptCode=3041')\" class=\"icon1\">办事指南</a>\n" +
                "            <a onclick=\"evaluationChengDu('511A07001005-510100000000-000-11510100562015685X-1-00','3041','https://www.sczwfw.gov.cn/jiq/front/transition/evaluation?areaCode=510100000000&eventCode=511A07001005-510100000000-000-11510100562015685X-1-00');\" href=\"javascript:void(0)\" class=\"icon2\">好差评</a>\n" +
                "            <a onclick=\"collect('企业投资项目核准（技术改造）(市级)', 'https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A07001005-510100000000-000-11510100562015685X-1-00&taskType=1&deptCode=3041');\" href=\"javascript:void(0)\" class=\"icon3\">订阅</a>\n" +
                "            <a onclick=\"checkIsOnlineChengDu('511A07001005-510100000000-000-11510100562015685X-1-00', '3^4^9^5^2^1', '101', 'http://zxbl.sczwfw.gov.cn/app/presonServices/netApply/4748239384196579328/0', '0', '11130613', '510100000000','3041');\" href=\"javascript:void(0)\" class=\"icon4\">申请</a>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</div>\n" +
                "<div class=\"sx_list\">\n" +
                "    <div id=\"bm510107009004\" class=\"sub_list1_main\" onclick=\"clickzhu('bm510107009004')\">\n" +
                "        <span title=\"无线电频率使用许可\">\n" +
                "            <a href=\"javascript:void(0)\">无线电频率使用许可</a>\n" +
                "        </span>\n" +
                "        <span class=\"sx_btn\">4项</span>\n" +
                "    </div>\n" +
                "    <ul class=\"sub_list2_main\">\n" +
                "        <li>\n" +
                "            <a title=\"地面无线电业务频率使用许可注销申请\" onclick=\"ywblurl('https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0022300008-510000000000-000-11510000699166855E-1-00&taskType=1&deptCode=3041')\" style=\"cursor:pointer\">地面无线电业务频率使用许可注销申请</a>\n" +
                "            <div class=\"bs_btn fr\">\n" +
                "                <a onclick=\"ywblurl('https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0022300008-510000000000-000-11510000699166855E-1-00&taskType=1&deptCode=3041')\" class=\"icon1\" style=\"cursor:pointer\">办事指南</a>\n" +
                "                <a onclick=\"evaluationChengDu('511A0022300008-510000000000-000-11510000699166855E-1-00','3041','https://www.sczwfw.gov.cn/jiq/front/transition/evaluation?areaCode=510100000000&eventCode=511A0022300008-510000000000-000-11510000699166855E-1-00');\" href=\"javascript:void(0)\" class=\"icon2\">好差评</a>\n" +
                "                <a onclick=\"collect('地面无线电业务频率使用许可注销申请', 'https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0022300008-510000000000-000-11510000699166855E-1-00&taskType=1&deptCode=3041');\" href=\"javascript:void(0)\" class=\"icon3\">订阅</a>\n" +
                "                <a onclick=\"checkIsOnlineChengDu('511A0022300008-510000000000-000-11510000699166855E-1-00', '3^4^9^2^1', '101', 'https://open.sczwfw.gov.cn/open-platform-oauth/oauth/authorize?client_id=0003&scope=user_info&code=1&response_type=code&redirect_uri=https://open.sczwfw.gov.cn/open-platform-exchange/exchange/getResource?resourceCode=ZYZC2022082600269065%26applyCode=YYSQ2022090500002466%26sysCode=0003', '1', '4256086175058526208', '510100000000', '3041');\" href=\"javascript:void(0)\" class=\"icon4\">申请</a>\n" +
                "            </div>\n" +
                "        </li>\n" +
                "        <li>\n" +
                "            <a title=\"地面无线电业务频率使用许可有效期届满延续申请\" onclick=\"ywblurl('https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0700900010-510000000000-000-11510000699166855E-1-00&taskType=1&deptCode=3041')\" style=\"cursor:pointer\">地面无线电业务频率使用许可有效期届满延续申请</a>\n" +
                "            <div class=\"bs_btn fr\">\n" +
                "                <a onclick=\"ywblurl('https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0700900010-510000000000-000-11510000699166855E-1-00&taskType=1&deptCode=3041')\" class=\"icon1\" style=\"cursor:pointer\">办事指南</a>\n" +
                "                <a onclick=\"evaluationChengDu('511A0700900010-510000000000-000-11510000699166855E-1-00','3041','https://www.sczwfw.gov.cn/jiq/front/transition/evaluation?areaCode=510100000000&eventCode=511A0700900010-510000000000-000-11510000699166855E-1-00');\" href=\"javascript:void(0)\" class=\"icon2\">好差评</a>\n" +
                "                <a onclick=\"collect('地面无线电业务频率使用许可有效期届满延续申请', 'https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0700900010-510000000000-000-11510000699166855E-1-00&taskType=1&deptCode=3041');\" href=\"javascript:void(0)\" class=\"icon3\">订阅</a>\n" +
                "                <a onclick=\"checkIsOnlineChengDu('511A0700900010-510000000000-000-11510000699166855E-1-00', '3^4^6^2^1', '101', 'https://open.sczwfw.gov.cn/open-platform-oauth/oauth/authorize?client_id=0003&scope=user_info&code=1&response_type=code&redirect_uri=https://open.sczwfw.gov.cn/open-platform-exchange/exchange/getResource?resourceCode=ZYZC2022082600269064%26applyCode=YYSQ2022090500002466%26sysCode=0003', '1', '4389831991614406656', '510100000000', '3041');\" href=\"javascript:void(0)\" class=\"icon4\">申请</a>\n" +
                "            </div>\n" +
                "        </li>\n" +
                "        <li>\n" +
                "            <a title=\"地面无线电业务频率使用许可首次申请\" onclick=\"ywblurl('https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0022300004-510000000000-000-699166855-1-00&taskType=1&deptCode=3041')\" style=\"cursor:pointer\">地面无线电业务频率使用许可首次申请</a>\n" +
                "            <div class=\"bs_btn fr\">\n" +
                "                <a onclick=\"ywblurl('https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0022300004-510000000000-000-699166855-1-00&taskType=1&deptCode=3041')\" class=\"icon1\" style=\"cursor:pointer\">办事指南</a>\n" +
                "                <a onclick=\"evaluationChengDu('511A0022300004-510000000000-000-699166855-1-00','3041','https://www.sczwfw.gov.cn/jiq/front/transition/evaluation?areaCode=510100000000&eventCode=511A0022300004-510000000000-000-699166855-1-00');\" href=\"javascript:void(0)\" class=\"icon2\">好差评</a>\n" +
                "                <a onclick=\"collect('地面无线电业务频率使用许可首次申请', 'https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0022300004-510000000000-000-699166855-1-00&taskType=1&deptCode=3041');\" href=\"javascript:void(0)\" class=\"icon3\">订阅</a>\n" +
                "                <a onclick=\"checkIsOnlineChengDu('511A0022300004-510000000000-000-699166855-1-00', '3^4^9^2^1', '101', 'https://open.sczwfw.gov.cn/open-platform-oauth/oauth/authorize?client_id=0003&scope=user_info&code=1&response_type=code&redirect_uri=https://open.sczwfw.gov.cn/open-platform-exchange/exchange/getResource?resourceCode=ZYZC2022082600269066%26applyCode=YYSQ2022090500002466%26sysCode=0003', '1', '4256099991079256064', '510100000000', '3041');\" href=\"javascript:void(0)\" class=\"icon4\">申请</a>\n" +
                "            </div>\n" +
                "        </li>\n" +
                "        <li>\n" +
                "            <a title=\"地面无线电业务频率使用许可事项变更申请\" onclick=\"ywblurl('https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0700900011-510000000000-000-11510000699166855E-1-00&taskType=1&deptCode=3041')\" style=\"cursor:pointer\">地面无线电业务频率使用许可事项变更申请</a>\n" +
                "            <div class=\"bs_btn fr\">\n" +
                "                <a onclick=\"ywblurl('https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0700900011-510000000000-000-11510000699166855E-1-00&taskType=1&deptCode=3041')\" class=\"icon1\" style=\"cursor:pointer\">办事指南</a>\n" +
                "                <a onclick=\"evaluationChengDu('511A0700900011-510000000000-000-11510000699166855E-1-00','3041','https://www.sczwfw.gov.cn/jiq/front/transition/evaluation?areaCode=510100000000&eventCode=511A0700900011-510000000000-000-11510000699166855E-1-00');\" href=\"javascript:void(0)\" class=\"icon2\">好差评</a>\n" +
                "                <a onclick=\"collect('地面无线电业务频率使用许可事项变更申请', 'https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0700900011-510000000000-000-11510000699166855E-1-00&taskType=1&deptCode=3041');\" href=\"javascript:void(0)\" class=\"icon3\">订阅</a>\n" +
                "                <a onclick=\"checkIsOnlineChengDu('511A0700900011-510000000000-000-11510000699166855E-1-00', '3^4^6^9^2^1', '101', 'https://open.sczwfw.gov.cn/open-platform-oauth/oauth/authorize?client_id=0003&scope=user_info&code=1&response_type=code&redirect_uri=https://open.sczwfw.gov.cn/open-platform-exchange/exchange/getResource?resourceCode=ZYZC2022082600269060%26applyCode=YYSQ2022090500002466%26sysCode=0003', '1', '4385847855840919552', '510100000000', '3041');\" href=\"javascript:void(0)\" class=\"icon4\">申请</a>\n" +
                "            </div>\n" +
                "        </li>\n" +
                "    </ul>\n" +
                "</div>\n" +
                "<div class=\"sx_list\">\n" +
                "    <div class=\"sub_list1_main\">\n" +
                "        <span title=\"内地居民前往港澳通行证、往来港澳通行证和签注签发\">\n" +
                "            <a onclick=\"ywblurl('https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0116500003-510100000000-000-15667888-1-00&taskType=1&deptCode=15667888')\">内地居民前往港澳通行证、往来港澳通行证和签注签发</a>\n" +
                "        </span>\n" +
                "        <div class=\"bs_btn one_c fr\">\n" +
                "            <a onclick=\"ywblurl('https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0116500003-510100000000-000-15667888-1-00&taskType=1&deptCode=15667888')\" class=\"icon1\">办事指南</a>\n" +
                "            <a onclick=\"evaluationChengDu('511A0116500003-510100000000-000-15667888-1-00','15667888','https://www.sczwfw.gov.cn/jiq/front/transition/evaluation?areaCode=510100000000&eventCode=511A0116500003-510100000000-000-15667888-1-00');\" href=\"javascript:void(0)\" class=\"icon2\">好差评</a>\n" +
                "            <a onclick=\"collect('内地居民前往港澳通行证、往来港澳通行证和签注签发', 'https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0116500003-510100000000-000-15667888-1-00&taskType=1&deptCode=15667888');\" href=\"javascript:void(0)\" class=\"icon3\">订阅</a>\n" +
                "            <a onclick=\"checkIsOnlineChengDu('511A0116500003-510100000000-000-15667888-1-00', '1', '102', 'http://zxbl.sczwfw.gov.cn/app/presonServices/netApply/4633992343984439296/0', '0', '10570817', '510100000000','15667888');\" href=\"javascript:void(0)\" class=\"icon4\">申请</a>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</div>\n" +
                "<div class=\"sx_list\">\n" +
                "    <div id=\"bm510107006000\" class=\"sub_list1_main\" onclick=\"clickzhu('bm510107006000')\">\n" +
                "        <span title=\"建立卫星通信网和设置卫星地球站审批\">\n" +
                "            <a href=\"javascript:void(0)\">建立卫星通信网和设置卫星地球站审批</a>\n" +
                "        </span>\n" +
                "        <span class=\"sx_btn\">4项</span>\n" +
                "    </div>\n" +
                "    <ul class=\"sub_list2_main\">\n" +
                "        <li>\n" +
                "            <a title=\"卫星地球站设置、使用许可有效期届满延续申请\" onclick=\"ywblurl('https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A07006001-510000000000-000-11510000699166855E-1-00&taskType=1&deptCode=3041')\" style=\"cursor:pointer\">卫星地球站设置、使用许可有效期届满延续申请</a>\n" +
                "            <div class=\"bs_btn fr\">\n" +
                "                <a onclick=\"ywblurl('https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A07006001-510000000000-000-11510000699166855E-1-00&taskType=1&deptCode=3041')\" class=\"icon1\" style=\"cursor:pointer\">办事指南</a>\n" +
                "                <a onclick=\"evaluationChengDu('511A07006001-510000000000-000-11510000699166855E-1-00','3041','https://www.sczwfw.gov.cn/jiq/front/transition/evaluation?areaCode=510100000000&eventCode=511A07006001-510000000000-000-11510000699166855E-1-00');\" href=\"javascript:void(0)\" class=\"icon2\">好差评</a>\n" +
                "                <a onclick=\"collect('卫星地球站设置、使用许可有效期届满延续申请', 'https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A07006001-510000000000-000-11510000699166855E-1-00&taskType=1&deptCode=3041');\" href=\"javascript:void(0)\" class=\"icon3\">订阅</a>\n" +
                "                <a onclick=\"checkIsOnlineChengDu('511A07006001-510000000000-000-11510000699166855E-1-00', '3^4^9^2^1', '101', 'https://open.sczwfw.gov.cn/open-platform-oauth/oauth/authorize?client_id=0003&scope=user_info&code=1&response_type=code&redirect_uri=https://open.sczwfw.gov.cn/open-platform-exchange/exchange/getResource?resourceCode=ZYZC2023041100281855%26applyCode=YYSQ2023042400002632%26sysCode=0003', '1', '4389838271598985216', '510100000000', '3041');\" href=\"javascript:void(0)\" class=\"icon4\">申请</a>\n" +
                "            </div>\n" +
                "        </li>\n" +
                "        <li>\n" +
                "            <a title=\"卫星地球站设置、使用许可事项变更申请\" onclick=\"ywblurl('https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A07006002-510000000000-000-11510000699166855E-1-00&taskType=1&deptCode=3041')\" style=\"cursor:pointer\">卫星地球站设置、使用许可事项变更申请</a>\n" +
                "            <div class=\"bs_btn fr\">\n" +
                "                <a onclick=\"ywblurl('https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A07006002-510000000000-000-11510000699166855E-1-00&taskType=1&deptCode=3041')\" class=\"icon1\" style=\"cursor:pointer\">办事指南</a>\n" +
                "                <a onclick=\"evaluationChengDu('511A07006002-510000000000-000-11510000699166855E-1-00','3041','https://www.sczwfw.gov.cn/jiq/front/transition/evaluation?areaCode=510100000000&eventCode=511A07006002-510000000000-000-11510000699166855E-1-00');\" href=\"javascript:void(0)\" class=\"icon2\">好差评</a>\n" +
                "                <a onclick=\"collect('卫星地球站设置、使用许可事项变更申请', 'https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A07006002-510000000000-000-11510000699166855E-1-00&taskType=1&deptCode=3041');\" href=\"javascript:void(0)\" class=\"icon3\">订阅</a>\n" +
                "                <a onclick=\"checkIsOnlineChengDu('511A07006002-510000000000-000-11510000699166855E-1-00', '3^4^6^9^2^1', '101', 'https://open.sczwfw.gov.cn/open-platform-oauth/oauth/authorize?client_id=0003&scope=user_info&code=1&response_type=code&redirect_uri=https://open.sczwfw.gov.cn/open-platform-exchange/exchange/getResource?resourceCode=ZYZC2023041100281854%26applyCode=YYSQ2023042400002632%26sysCode=0003', '1', '4389836865713766400', '510100000000', '3041');\" href=\"javascript:void(0)\" class=\"icon4\">申请</a>\n" +
                "            </div>\n" +
                "        </li>\n" +
                "        <li>\n" +
                "            <a title=\"卫星地球站设置、使用许可首次申请\" onclick=\"ywblurl('https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0078400001-510000000000-000-699166855-1-00&taskType=1&deptCode=3041')\" style=\"cursor:pointer\">卫星地球站设置、使用许可首次申请</a>\n" +
                "            <div class=\"bs_btn fr\">\n" +
                "                <a onclick=\"ywblurl('https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0078400001-510000000000-000-699166855-1-00&taskType=1&deptCode=3041')\" class=\"icon1\" style=\"cursor:pointer\">办事指南</a>\n" +
                "                <a onclick=\"evaluationChengDu('511A0078400001-510000000000-000-699166855-1-00','3041','https://www.sczwfw.gov.cn/jiq/front/transition/evaluation?areaCode=510100000000&eventCode=511A0078400001-510000000000-000-699166855-1-00');\" href=\"javascript:void(0)\" class=\"icon2\">好差评</a>\n" +
                "                <a onclick=\"collect('卫星地球站设置、使用许可首次申请', 'https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0078400001-510000000000-000-699166855-1-00&taskType=1&deptCode=3041');\" href=\"javascript:void(0)\" class=\"icon3\">订阅</a>\n" +
                "                <a onclick=\"checkIsOnlineChengDu('511A0078400001-510000000000-000-699166855-1-00', '3^4^9^2^1', '101', 'https://open.sczwfw.gov.cn/open-platform-oauth/oauth/authorize?client_id=0003&scope=user_info&code=1&response_type=code&redirect_uri=https://open.sczwfw.gov.cn/open-platform-exchange/exchange/getResource?resourceCode=ZYZC2023041100281856%26applyCode=YYSQ2023042400002632%26sysCode=0003', '1', '4256114579891585024', '510100000000', '3041');\" href=\"javascript:void(0)\" class=\"icon4\">申请</a>\n" +
                "            </div>\n" +
                "        </li>\n" +
                "        <li>\n" +
                "            <a title=\"卫星地球站设置、使用许可注销申请\" onclick=\"ywblurl('https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A07006003-510000000000-000-11510000699166855E-1-00&taskType=1&deptCode=3041')\" style=\"cursor:pointer\">卫星地球站设置、使用许可注销申请</a>\n" +
                "            <div class=\"bs_btn fr\">\n" +
                "                <a onclick=\"ywblurl('https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A07006003-510000000000-000-11510000699166855E-1-00&taskType=1&deptCode=3041')\" class=\"icon1\" style=\"cursor:pointer\">办事指南</a>\n" +
                "                <a onclick=\"evaluationChengDu('511A07006003-510000000000-000-11510000699166855E-1-00','3041','https://www.sczwfw.gov.cn/jiq/front/transition/evaluation?areaCode=510100000000&eventCode=511A07006003-510000000000-000-11510000699166855E-1-00');\" href=\"javascript:void(0)\" class=\"icon2\">好差评</a>\n" +
                "                <a onclick=\"collect('卫星地球站设置、使用许可注销申请', 'https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A07006003-510000000000-000-11510000699166855E-1-00&taskType=1&deptCode=3041');\" href=\"javascript:void(0)\" class=\"icon3\">订阅</a>\n" +
                "                <a onclick=\"checkIsOnlineChengDu('511A07006003-510000000000-000-11510000699166855E-1-00', '3^4^9^2^1', '101', 'https://open.sczwfw.gov.cn/open-platform-oauth/oauth/authorize?client_id=0003&scope=user_info&code=1&response_type=code&redirect_uri=https://open.sczwfw.gov.cn/open-platform-exchange/exchange/getResource?resourceCode=ZYZC2023041100281857%26applyCode=YYSQ2023042400002632%26sysCode=0003', '1', '4389839631161655296', '510100000000', '3041');\" href=\"javascript:void(0)\" class=\"icon4\">申请</a>\n" +
                "            </div>\n" +
                "        </li>\n" +
                "    </ul>\n" +
                "</div>\n" +
                "<div class=\"sx_list\">\n" +
                "    <div class=\"sub_list1_main\">\n" +
                "        <span title=\"出入境通行证签发\">\n" +
                "            <a onclick=\"ywblurl('https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0101600004-510100000000-000-15667888-1-00&taskType=1&deptCode=15667888')\">出入境通行证签发</a>\n" +
                "        </span>\n" +
                "        <div class=\"bs_btn one_c fr\">\n" +
                "            <a onclick=\"ywblurl('https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0101600004-510100000000-000-15667888-1-00&taskType=1&deptCode=15667888')\" class=\"icon1\">办事指南</a>\n" +
                "            <a onclick=\"evaluationChengDu('511A0101600004-510100000000-000-15667888-1-00','15667888','https://www.sczwfw.gov.cn/jiq/front/transition/evaluation?areaCode=510100000000&eventCode=511A0101600004-510100000000-000-15667888-1-00');\" href=\"javascript:void(0)\" class=\"icon2\">好差评</a>\n" +
                "            <a onclick=\"collect('出入境通行证签发', 'https://www.sczwfw.gov.cn/jiq/front/transition/ywTransToDetail?areaCode=510100000000&itemCode=511A0101600004-510100000000-000-15667888-1-00&taskType=1&deptCode=15667888');\" href=\"javascript:void(0)\" class=\"icon3\">订阅</a>\n" +
                "            <a onclick=\"checkIsOnlineChengDu('511A0101600004-510100000000-000-15667888-1-00', '1', '102', 'http://zxbl.sczwfw.gov.cn/app/presonServices/netApply/4633992330956537856/0', '0', '10570816', '510100000000','15667888');\" href=\"javascript:void(0)\" class=\"icon4\">申请</a>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</div>\n";

        html = "<input type=\"hidden\" id=\"zisizeDept\" value=\"663\">\n" +
                "<input type=\"hidden\" id=\"fusizeDept\" value=\"384\">\n" +
                "<input type=\"hidden\" id=\"onsizeDept\" value=\"651\">\n" +
                "<input type=\"hidden\" id=\"pageNoDept\" value=\"1000\">\n" +
                "<input type=\"hidden\" id=\"pageNumDept\" value=\"26\">\n" +
                "<input type=\"hidden\" id=\"result_countDept\" value=\"663\">\n" +
                "<input type=\"hidden\" id=\"currsumpageDept\" value=\"5\">\n";
        // 提取唯一服务事项
        List<GovService> services = parseGovernmentServices(html);
        
        // 输出结果
        printServices(services);
    }
    
    /**
     * 解析政务服务事项
     */
    public static List<GovService> parseGovernmentServices(String html) {
        List<GovService> services = new ArrayList<>();
        Set<String> uniqueIds = new HashSet<>();
        
        Document doc = Jsoup.parse(html);
        
        // 处理每个sx_list
        Elements sxLists = doc.select("div.sx_list");
        
        for (Element sxList : sxLists) {
            // 获取该sx_list中的所有服务项
            List<GovService> listServices = extractServicesFromSxList(sxList);
            
            // 添加到总列表（去重）
            for (GovService service : listServices) {
                String uniqueId = service.generateUniqueId();
                if (!uniqueIds.contains(uniqueId)) {
                    uniqueIds.add(uniqueId);
                    services.add(service);
                }
            }
        }
        
        return services;
    }
    
    /**
     * 从单个sx_list中提取服务
     */
    private static List<GovService> extractServicesFromSxList(Element sxList) {
        List<GovService> services = new ArrayList<>();
        
        // 情况1: 直接服务（没有子列表）
        Elements directServices = sxList.select("div.sub_list1_main > span > a[onclick*=ywblurl]");
        for (Element link : directServices) {
            GovService service = createServiceFromLink(link, "直接服务");
            if (service != null) {
                services.add(service);
            }
        }
        
        // 情况2: 子项服务
        Elements subServices = sxList.select("ul.sub_list2_main li > a[onclick*=ywblurl]");
        for (Element link : subServices) {
            GovService service = createServiceFromLink(link, "子项服务");
            if (service != null) {
                services.add(service);
            }
        }
        
        return services;
    }
    
    /**
     * 从链接创建服务对象
     */
    private static GovService createServiceFromLink(Element link, String type) {
        String onclick = link.attr("onclick");
        String url = extractYwblUrl(onclick);
        
        if (url == null || url.isEmpty()) {
            return null;
        }
        
        String title = extractServiceTitle(link);
        String itemCode = extractParamFromUrl(url, "itemCode");
        String deptCode = extractParamFromUrl(url, "deptCode");
        
        GovService service = new GovService();
        service.setTitle(title);
        service.setUrl(url);
        service.setItemCode(itemCode);
        service.setDeptCode(deptCode);
        service.setType(type);
        
        return service;
    }
    
    /**
     * 提取服务标题
     */
    private static String extractServiceTitle(Element link) {
        // 1. 优先使用父级span的title
        Element parentSpan = link.parent();
        if (parentSpan != null && "span".equals(parentSpan.tagName())) {
            String title = parentSpan.attr("title");
            if (!title.trim().isEmpty()) {
                return title.trim();
            }
        }
        
        // 2. 使用链接自身的title
        String linkTitle = link.attr("title");
        if (!linkTitle.trim().isEmpty()) {
            return linkTitle.trim();
        }
        
        // 3. 使用链接文本
        return link.text().trim();
    }
    
    /**
     * 提取ywblurl
     */
    private static String extractYwblUrl(String onclick) {
        if (!onclick.contains("ywblurl('")) {
            return null;
        }
        
        int start = onclick.indexOf("ywblurl('") + "ywblurl('".length();
        int end = onclick.indexOf("')", start);
        
        if (end > start) {
            return onclick.substring(start, end).trim();
        }
        
        return null;
    }
    
    /**
     * 从URL中提取参数
     */
    private static String extractParamFromUrl(String url, String paramName) {
        String paramPrefix = paramName + "=";
        int start = url.indexOf(paramPrefix);
        
        if (start == -1) {
            return "";
        }
        
        start += paramPrefix.length();
        int end = url.indexOf("&", start);
        
        if (end == -1) {
            end = url.length();
        }
        
        return url.substring(start, end);
    }
    
    /**
     * 打印服务列表
     */
    private static void printServices(List<GovService> services) {
        System.out.println("共计提取 " + services.size() + " 个政务服务事项：");
        System.out.println("==================================================================");
        
        for (int i = 0; i < services.size(); i++) {
            GovService service = services.get(i);
            System.out.printf("%3d. %s%n", i + 1, service.getTitle());
            System.out.printf("     URL: %s%n", service.getUrl());
            System.out.printf("     业务代码: %s | 部门代码: %s | 类型: %s%n", 
                    service.getItemCode(), service.getDeptCode(), service.getType());
            System.out.println("     -------------------------------------------------");
        }
    }
    
    /**
     * 政务服务类
     */
    static class GovService {
        private String title;
        private String url;
        private String itemCode;
        private String deptCode;
        private String type;
        
        // 生成唯一标识（基于itemCode）
        public String generateUniqueId() {
            return itemCode != null ? itemCode : url;
        }
        
        // getters and setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
        
        public String getItemCode() { return itemCode; }
        public void setItemCode(String itemCode) { this.itemCode = itemCode; }
        
        public String getDeptCode() { return deptCode; }
        public void setDeptCode(String deptCode) { this.deptCode = deptCode; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
    }
}