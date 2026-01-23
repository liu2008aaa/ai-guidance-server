package com.guidance.service.crawler;

import com.guidance.utils.TableBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.CollectionUtils;

import java.util.*;

public class GovGuideParser {
    
    public static void main(String[] args) {
        String html ="";
        
        GuideInfo guide = parseServiceGuide(html);
        
        // 输出提取结果
        printGuideInfo(guide);
    }
    
    /**
     * 解析办事指南
     */
    public static GuideInfo parseServiceGuide(String html) {
        GuideInfo guide = new GuideInfo();
        Document doc = Jsoup.parse(html);
        
        // 1. 提取基本信息
        guide.setBasicInfo(parseBasicInfo(doc));
        
        // 2. 提取申请材料
        guide.setMaterials(parseMaterials(doc));
        
        // 3. 提取受理条件
        guide.setAcceptanceConditions(parseAcceptanceConditions(doc));
        
        // 4. 提取办理流程
        guide.setProcessSteps(parseProcessSteps(doc));
        
        // 5. 提取收费标准
        guide.setFeeStandard(parseFeeStandard(doc));
        
        // 6. 提取设定依据
        guide.setBasisRegulations(parseBasisRegulations(doc));
        
        // 7. 提取中介服务
        guide.setIntermediaryServices(parseIntermediaryServices(doc));
        
        // 8. 提取常见问题
        guide.setFaq(parseFaq(doc));
        
        // 9. 提取其他信息
        guide.setServiceInfo(parseServiceInfo(doc));
        
        return guide;
    }
    
    /**
     * 解析基本信息
     */
    private static BasicInfo parseBasicInfo(Document doc) {
        BasicInfo basicInfo = new BasicInfo();
        Element section1 = doc.select("div.section1").first();
        
        if (section1 != null) {
            // 提取基本信息表格
            Elements rows = section1.select("table tr");
            for (Element row : rows) {
                Elements tds = row.select("td");
                if (tds.size() >= 4) {
                    String key = tds.get(0).text().trim();
                    String value = tds.get(1).text().trim();
                    String key2 = tds.get(2).text().trim();
                    String value2 = tds.get(3).text().trim();
                    
                    // 设置基本信息
                    setBasicInfoValue(basicInfo, key, value);
                    if (!key2.isEmpty()) {
                        setBasicInfoValue(basicInfo, key2, value2);
                    }
                }
            }
            
            // 提取审批结果
            Element approvalResult = section1.select("table.table22").first();
            if (approvalResult != null) {
                List<ApprovalResult> results = new ArrayList<>();
                Elements resultRows = approvalResult.select("tr");
                for (int i = 1; i < resultRows.size(); i++) { // 跳过表头
                    Element resultRow = resultRows.get(i);
                    Elements tds = resultRow.select("td");
                    if (tds.size() >= 3) {
                        ApprovalResult result = new ApprovalResult();
                        result.setType(tds.get(0).text().trim());
                        result.setName(tds.get(1).text().trim());
                        result.setSample(tds.get(2).text().trim());
                        results.add(result);
                    }
                }
                basicInfo.setApprovalResults(results);
            }
        }
        
        return basicInfo;
    }
    
    /**
     * 设置基本信息字段
     */
    private static void setBasicInfoValue(BasicInfo basicInfo, String key, String value) {
        switch (key) {
            case "服务对象":
                basicInfo.setServiceObject(value);
                break;
            case "办件类型":
                basicInfo.setCaseType(value);
                break;
            case "办理形式":
                basicInfo.setProcessingMethod(value);
                break;
            case "法定办结时限":
                basicInfo.setLegalProcessingTime(value);
                break;
            case "承诺办结时限":
                basicInfo.setPromisedProcessingTime(value);
                break;
            case "是否收费":
                basicInfo.setHasFee(value);
                break;
            case "行使方式":
                basicInfo.setExerciseMethod(value);
                break;
            case "到办事现场次数":
                basicInfo.setOnSiteTimes(value);
                break;
        }
    }
    
    /**
     * 解析申请材料
     */
    private static List<Material> parseMaterials(Document doc) {
        List<Material> materials = new ArrayList<>();
        Element section5 = doc.select("div.section5").first();
        
        if (section5 != null) {
            // 获取材料表格
            Element materialTable = section5.select("#material table").first();
            if (materialTable != null) {
                Elements rows = materialTable.select("tr");
                // 跳过表头
                for (int i = 1; i < rows.size(); i++) {
                    Element row = rows.get(i);
                    Elements tds = row.select("td");
                    if (tds.size() >= 8) {
                        Material material = new Material();
                        
                        // 序号
                        material.setIndex(tds.get(0).text().trim());
                        
                        // 材料名称（检查是否有减免标签）
                        Element nameSpan = tds.get(1).select("span").first();
                        if (nameSpan != null) {
                            material.setName(nameSpan.text().trim());
                        }
                        
                        // 材料必要性
                        material.setNecessity(tds.get(2).text().trim());
                        
                        // 材料类型
                        material.setType(tds.get(3).text().trim());
                        
                        // 材料形式
                        material.setForm(tds.get(4).text().trim());
                        
                        // 纸质材料份数
                        material.setPaperCopies(tds.get(5).text().trim());
                        
                        materials.add(material);
                    }
                }
            }
        }
        
        return materials;
    }
    
    /**
     * 解析受理条件
     */
    private static String parseAcceptanceConditions(Document doc) {
        Element section6 = doc.select("div.section6").first();
        if (section6 != null) {
            Element p = section6.select("p").first();
            if (p != null) {
                return p.text().trim();
            }
        }
        return null;
    }
    
    /**
     * 解析办理流程
     */
    private static List<ProcessStep> parseProcessSteps(Document doc) {
        List<ProcessStep> steps = new ArrayList<>();
        Element section7 = doc.select("div.section7").first();
        
        if (section7 != null) {
            Element processTable = section7.select("table").first();
            if (processTable != null) {
                Elements rows = processTable.select("tr");
                // 跳过表头
                for (int i = 1; i < rows.size(); i++) {
                    Element row = rows.get(i);
                    Elements tds = row.select("td");
                    if (tds.size() >= 3) {
                        ProcessStep step = new ProcessStep();
                        step.setStepName(tds.get(0).text().trim());
                        step.setProcessingTime(tds.get(1).text().trim());
                        step.setExternalTime(tds.get(2).text().trim());
                        steps.add(step);
                    }
                }
                
                // 提取流程说明
                if (rows.size() > 0) {
                    Element firstRow = rows.get(1);
                    Elements tds = firstRow.select("td");
                    if (tds.size() >= 4) {
                        Element processDescription = tds.get(3);
                        for (ProcessStep step : steps) {
                            step.setDescription(processDescription.text().trim());
                        }
                    }
                }
            }
        }
        
        return steps;
    }
    
    /**
     * 解析收费标准
     */
    private static String parseFeeStandard(Document doc) {
        Element section8 = doc.select("div.section8").first();
        if (section8 != null) {
            Elements p = section8.select("p");//.first();
            if (p != null) {
                StringBuilder sb = new StringBuilder();
                int i = 0;
                for(Element e : p){
                    if(i == 2){
                        break;
                    }
                    sb.append(e.text().trim() + ";");
                    i++;
                }
                return sb.toString();
            }
        }
        return null;
    }
    
    /**
     * 解析设定依据
     */
    private static List<BasisRegulation> parseBasisRegulations(Document doc) {
        List<BasisRegulation> regulations = new ArrayList<>();
        Element section9 = doc.select("div.section9").first();
        
        if (section9 != null) {
            Element basisTable = section9.select("table").first();
            if (basisTable != null) {
                Elements rows = basisTable.select("tr");
                // 跳过表头
                for (int i = 1; i < rows.size(); i++) {
                    Element row = rows.get(i);
                    Elements tds = row.select("td");
                    if (tds.size() >= 5) {
                        BasisRegulation regulation = new BasisRegulation();
                        regulation.setIndex(tds.get(0).text().trim());
                        regulation.setLawName(tds.get(1).text().trim());
                        regulation.setLawType(tds.get(2).text().trim());
                        regulation.setClause(tds.get(3).text().trim());
                        regulations.add(regulation);
                    }
                }
            }
        }
        
        return regulations;
    }
    
    /**
     * 解析中介服务
     */
    private static String parseIntermediaryServices(Document doc) {
        Element section10 = doc.select("div.section10").first();
        if (section10 != null) {
            Element p = section10.select("p").first();
            if (p != null) {
                return p.text().trim();
            }
        }
        return null;
    }
    
    /**
     * 解析常见问题
     */
    private static List<FAQ> parseFaq(Document doc) {
        List<FAQ> faqs = new ArrayList<>();
        Element section11 = doc.select("div.section11").first();
        
        if (section11 != null) {
            Elements faqRows = section11.select("table tr");
            for (Element row : faqRows) {
                Elements tds = row.select("td");
                if (tds.size() >= 2) {
                    FAQ faq = new FAQ();
                    faq.setIndex(tds.get(0).text().trim());
                    
                    Element contentTd = tds.get(1);
                    Element questionSpan = contentTd.select("span").first();
                    Element answerP = contentTd.select("p").first();
                    
                    if (questionSpan != null) {
                        faq.setQuestion(questionSpan.text().trim());
                    }
                    if (answerP != null) {
                        faq.setAnswer(answerP.text().trim());
                    }
                    
                    faqs.add(faq);
                }
            }
        }
        
        return faqs;
    }
    
    /**
     * 解析其他服务信息
     */
    private static ServiceInfo parseServiceInfo(Document doc) {
        ServiceInfo serviceInfo = new ServiceInfo();
        
        // 提取标题和部门
        Element bannertop = doc.select(".bannertop").first();
        if (bannertop != null) {
            Element titleSpan = bannertop.select("span").first();
            if (titleSpan != null) {
                serviceInfo.setTitle(titleSpan.text().trim());
            }
            
            Element deptP = bannertop.select("p").first();
            if (deptP != null) {
                serviceInfo.setDepartment(deptP.text().trim());
            }
        }
        
        // 提取办理统计信息
        Element bannermmid = doc.select(".bannermmid").first();
        if (bannermmid != null) {
            Elements stats = bannermmid.select("li");
            if (stats.size() >= 3) {
                serviceInfo.setOnSiteCount(stats.get(0).select("span").text().trim());
                serviceInfo.setLegalTimeLimit(stats.get(1).select("span").text().trim());
                serviceInfo.setPromisedTimeLimit(stats.get(2).select("span").text().trim());
            }
            
            // 满意度
            Element satisfaction = bannermmid.select(".manyi").first();
            if (satisfaction != null) {
                Element satSpan = satisfaction.select("span").first();
                if (satSpan != null) {
                    serviceInfo.setSatisfaction(satSpan.text().trim());
                }
            }
        }
        
        // 提取常规信息表格
        Element section4 = doc.select("div.section4").first();
        if (section4 != null) {
            Elements tables = section4.select("table");
            
            if (tables.size() >= 2) {
                // 第一个表格（常规信息）
                Element table1 = tables.get(0);
                Elements rows = table1.select("tr");
                for (Element row : rows) {
                    Elements tds = row.select("td");
                    if (tds.size() >= 2) {
                        String key = tds.get(0).text().trim();
                        String value = tds.get(1).text().trim();
                        
                        switch (key) {
                            case "办理时间":
                                serviceInfo.setProcessingTime(value);
                                break;
                            case "办理地点":
                                serviceInfo.setProcessingLocation(value);
                                break;
                            case "咨询方式":
                                serviceInfo.setConsultationMethod(value);
                                break;
                            case "监督投诉方式":
                                serviceInfo.setSupervisionMethod(value);
                                break;
                        }
                    }
                }
                
                // 第二个表格（其他信息）
                Element table2 = tables.get(1);
                rows = table2.select("tr");
                for (Element row : rows) {
                    Elements tds = row.select("td");
                    if (tds.size() >= 2) {
                        String key = tds.get(0).text().trim();
                        String value = tds.get(1).text().trim();
                        
                        switch (key) {
                            case "实施主体":
                                serviceInfo.setImplementingBody(value);
                                break;
                            case "事项类型":
                                serviceInfo.setMatterType(value);
                                break;
                            case "权力来源":
                                serviceInfo.setPowerSource(value);
                                break;
                            case "基本编码":
                                serviceInfo.setBasicCode(value);
                                break;
                            case "实施编码":
                                serviceInfo.setImplementationCode(value);
                                break;
                        }
                    }
                }
            }
        }
        
        return serviceInfo;
    }
    
    /**
     * 打印办事指南信息
     */
    private static void printGuideInfo(GuideInfo guide) {
        System.out.println("===================== 政务服务办事指南 =====================");
        
        // 打印服务信息
        System.out.println("\n一、服务基本信息");
        System.out.println("标题: " + guide.getServiceInfo().getTitle());
        System.out.println("实施部门: " + guide.getServiceInfo().getDepartment());
        System.out.println("实施主体: " + guide.getServiceInfo().getImplementingBody());
        System.out.println("事项类型: " + guide.getServiceInfo().getMatterType());
        System.out.println("基本编码: " + guide.getServiceInfo().getBasicCode());
        
        // 打印基本信息
        System.out.println("\n二、事项基本信息");
        BasicInfo basic = guide.getBasicInfo();
        System.out.println("服务对象: " + basic.getServiceObject());
        System.out.println("办件类型: " + basic.getCaseType());
        System.out.println("办理形式: " + basic.getProcessingMethod());
        System.out.println("法定办结时限: " + basic.getLegalProcessingTime());
        System.out.println("承诺办结时限: " + basic.getPromisedProcessingTime());
        System.out.println("是否收费: " + basic.getHasFee());
        System.out.println("到办事现场次数: " + basic.getOnSiteTimes());
        
        if (basic.getApprovalResults() != null && !basic.getApprovalResults().isEmpty()) {
            System.out.println("\n审批结果:");
            for (ApprovalResult result : basic.getApprovalResults()) {
                System.out.println("  - 类型: " + result.getType() + 
                                 ", 名称: " + result.getName() + 
                                 ", 样本: " + result.getSample());
            }
        }
        
        // 打印申请材料
        System.out.println("\n三、申请材料清单 (" + guide.getMaterials().size() + "项)");
        for (Material material : guide.getMaterials()) {
            System.out.println(String.format("%2s. %-40s %-6s %-8s %-6s %s份", 
                material.getIndex(),
                material.getName(),
                material.getNecessity(),
                material.getType(),
                material.getForm(),
                material.getPaperCopies()));
        }
        
        // 打印受理条件
        System.out.println("\n四、受理条件");
        System.out.println(guide.getAcceptanceConditions());
        
        // 打印办理流程
        System.out.println("\n五、办理流程");
        for (ProcessStep step : guide.getProcessSteps()) {
            System.out.println(String.format("%-10s %-15s %-15s", 
                step.getStepName(),
                step.getProcessingTime(),
                step.getExternalTime()));
        }
        if (!guide.getProcessSteps().isEmpty()) {
            System.out.println("\n流程说明:");
            System.out.println(guide.getProcessSteps().get(0).getDescription());
        }
        
        // 打印收费标准
        System.out.println("\n六、收费标准");
        System.out.println(guide.getFeeStandard());
        
        // 打印设定依据
        System.out.println("\n七、设定依据 (" + guide.getBasisRegulations().size() + "项)");
        for (BasisRegulation regulation : guide.getBasisRegulations()) {
            System.out.println(String.format("%s. %-30s %-8s %s", 
                regulation.getIndex(),
                regulation.getLawName(),
                regulation.getLawType(),
                regulation.getClause()));
        }
        
        // 打印中介服务
        System.out.println("\n八、中介服务");
        System.out.println(guide.getIntermediaryServices());
        
        // 打印常见问题
        System.out.println("\n九、常见问题 (" + guide.getFaq().size() + "项)");
        for (FAQ faq : guide.getFaq()) {
            System.out.println(faq.getIndex() + ". " + faq.getQuestion());
            System.out.println("答: " + faq.getAnswer());
        }
        
        // 打印服务信息
        System.out.println("\n十、服务信息");
        ServiceInfo info = guide.getServiceInfo();
        System.out.println("办理时间: " + info.getProcessingTime());
        System.out.println("办理地点: " + info.getProcessingLocation());
        System.out.println("咨询方式: " + info.getConsultationMethod());
        System.out.println("满意度: " + info.getSatisfaction());
        System.out.println("现场办理次数: " + info.getOnSiteCount());
        System.out.println("法定时限: " + info.getLegalTimeLimit());
        System.out.println("承诺时限: " + info.getPromisedTimeLimit());
    }
    
    // ==================== 数据模型类 ====================
    
    /**
     * 办事指南总类
     */
    public static class GuideInfo {
        private ServiceInfo serviceInfo;
        private BasicInfo basicInfo;
        private List<Material> materials;
        private String acceptanceConditions;
        private List<ProcessStep> processSteps;
        private String feeStandard;
        private List<BasisRegulation> basisRegulations;
        private String intermediaryServices;
        private List<FAQ> faq;
        private String title;
        private String url;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        // getters and setters
        public ServiceInfo getServiceInfo() { return serviceInfo; }
        public void setServiceInfo(ServiceInfo serviceInfo) { this.serviceInfo = serviceInfo; }
        
        public BasicInfo getBasicInfo() { return basicInfo; }
        public void setBasicInfo(BasicInfo basicInfo) { this.basicInfo = basicInfo; }
        
        public List<Material> getMaterials() { return materials; }
        public void setMaterials(List<Material> materials) { this.materials = materials; }
        
        public String getAcceptanceConditions() { return acceptanceConditions; }
        public void setAcceptanceConditions(String acceptanceConditions) { this.acceptanceConditions = acceptanceConditions; }
        
        public List<ProcessStep> getProcessSteps() { return processSteps; }
        public void setProcessSteps(List<ProcessStep> processSteps) { this.processSteps = processSteps; }
        
        public String getFeeStandard() { return feeStandard; }
        public void setFeeStandard(String feeStandard) { this.feeStandard = feeStandard; }
        
        public List<BasisRegulation> getBasisRegulations() { return basisRegulations; }
        public void setBasisRegulations(List<BasisRegulation> basisRegulations) { this.basisRegulations = basisRegulations; }
        
        public String getIntermediaryServices() { return intermediaryServices; }
        public void setIntermediaryServices(String intermediaryServices) { this.intermediaryServices = intermediaryServices; }
        
        public List<FAQ> getFaq() { return faq; }
        public void setFaq(List<FAQ> faq) { this.faq = faq; }

        /**
         * 输出材料清单数据
         * @return
         */
        public String getMaterialsText(){
            if(CollectionUtils.isEmpty(this.getMaterials())){
                return "申请材料清单";
            }
            StringBuilder sb = new StringBuilder();
            sb.append("申请材料清单");
            TableBuilder tableBuilder = new TableBuilder(5,120, 5, 10, 10)
                    .header("编号","名称", "必要性", "类型", "形式");
            sb.append("\n");
            List<Object[]> rows = new ArrayList<>();
            int i = 1;
            for (Material material : this.getMaterials()) {
                Object[] cells = new Object[]{i++,material.getName(),material.getNecessity(),material.getType(),material.getForm()};
                rows.add(cells);
            }
            tableBuilder.rows(rows);
            sb.append(tableBuilder);
            return sb.toString();
        }

        /**
         * 办理流程
         * @return
         */
        public String getProcessStepsText(){
            if(CollectionUtils.isEmpty(this.getProcessSteps())){
                return "办理流程";
            }
            StringBuilder sb = new StringBuilder();
            sb.append("办理流程");
            TableBuilder tableBuilder = new TableBuilder(5,20, 15)
                    .header("编号","步骤", "时间");
            sb.append("\n");
            List<Object[]> rows = new ArrayList<>();
            int i = 1;
            for (ProcessStep step : this.getProcessSteps()) {
                Object[] cells = new Object[]{i++,step.getStepName(),step.getProcessingTime()};
                rows.add(cells);
            }
            tableBuilder.rows(rows);
            sb.append(tableBuilder);
            sb.append("流程说明:\n");
            sb.append(this.getProcessSteps().get(0).getDescription());
            return sb.toString();
        }

        /**
         * 收费标准
         * @return
         */
        public String getFeeStandardText(){
            StringBuilder sb = new StringBuilder();
            sb.append("收费标准\n");
            sb.append(this.getFeeStandard());
            return sb.toString();
        }

        /**
         * 受理条件
         * @return
         */
        public String getAcceptanceConditionsText(){
            StringBuilder sb = new StringBuilder();
            sb.append("受理条件\n");
            sb.append(this.getAcceptanceConditions());
            return sb.toString();
        }

        /**
         * 输出常见问题
         * @return
         */
        public String getFaqText(){
            if(CollectionUtils.isEmpty(this.getFaq())){
                return "常见问题";
            }
            StringBuilder sb = new StringBuilder();
            sb.append("常见问题\n");
            for(FAQ f : this.getFaq()){
                sb.append(f.getIndex() + "." + f.getQuestion() + "\n");
                sb.append(f.getAnswer() + "\n");
            }
            return sb.toString();
        }
    }
    
    /**
     * 服务信息
     */
    public static class ServiceInfo {
        private String title;
        private String department;
        private String onSiteCount;
        private String legalTimeLimit;
        private String promisedTimeLimit;
        private String satisfaction;
        private String processingTime;
        private String processingLocation;
        private String consultationMethod;
        private String supervisionMethod;
        private String implementingBody;
        private String matterType;
        private String powerSource;
        private String basicCode;
        private String implementationCode;
        
        // getters and setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
        
        public String getOnSiteCount() { return onSiteCount; }
        public void setOnSiteCount(String onSiteCount) { this.onSiteCount = onSiteCount; }
        
        public String getLegalTimeLimit() { return legalTimeLimit; }
        public void setLegalTimeLimit(String legalTimeLimit) { this.legalTimeLimit = legalTimeLimit; }
        
        public String getPromisedTimeLimit() { return promisedTimeLimit; }
        public void setPromisedTimeLimit(String promisedTimeLimit) { this.promisedTimeLimit = promisedTimeLimit; }
        
        public String getSatisfaction() { return satisfaction; }
        public void setSatisfaction(String satisfaction) { this.satisfaction = satisfaction; }
        
        public String getProcessingTime() { return processingTime; }
        public void setProcessingTime(String processingTime) { this.processingTime = processingTime; }
        
        public String getProcessingLocation() { return processingLocation; }
        public void setProcessingLocation(String processingLocation) { this.processingLocation = processingLocation; }
        
        public String getConsultationMethod() { return consultationMethod; }
        public void setConsultationMethod(String consultationMethod) { this.consultationMethod = consultationMethod; }
        
        public String getSupervisionMethod() { return supervisionMethod; }
        public void setSupervisionMethod(String supervisionMethod) { this.supervisionMethod = supervisionMethod; }
        
        public String getImplementingBody() { return implementingBody; }
        public void setImplementingBody(String implementingBody) { this.implementingBody = implementingBody; }
        
        public String getMatterType() { return matterType; }
        public void setMatterType(String matterType) { this.matterType = matterType; }
        
        public String getPowerSource() { return powerSource; }
        public void setPowerSource(String powerSource) { this.powerSource = powerSource; }
        
        public String getBasicCode() { return basicCode; }
        public void setBasicCode(String basicCode) { this.basicCode = basicCode; }
        
        public String getImplementationCode() { return implementationCode; }
        public void setImplementationCode(String implementationCode) { this.implementationCode = implementationCode; }

        public String toText(){
            StringBuilder sb = new StringBuilder();
            sb.append("基本信息\n");
            sb.append("实施部门: " + this.getDepartment() + "\n");
            sb.append("实施主体: " + this.getImplementingBody() + "\n");
            sb.append("事项类型: " + this.getMatterType() + "\n");
            sb.append("基本编码: " + this.getBasicCode() + "\n");
            sb.append("办理时间: " + this.getProcessingTime() + "\n");
            sb.append("办理地点: " + this.getProcessingLocation() + "\n");
            sb.append("咨询方式: " + this.getConsultationMethod() + "\n");
            sb.append("监督投诉方式: " + this.getSupervisionMethod());
            return sb.toString();
        }
    }
    
    /**
     * 基本信息
     */
    public static class BasicInfo {
        private String serviceObject;
        private String caseType;
        private String processingMethod;
        private String legalProcessingTime;
        private String promisedProcessingTime;
        private String hasFee;
        private String exerciseMethod;
        private String onSiteTimes;
        private List<ApprovalResult> approvalResults;
        
        // getters and setters
        public String getServiceObject() { return serviceObject; }
        public void setServiceObject(String serviceObject) { this.serviceObject = serviceObject; }
        
        public String getCaseType() { return caseType; }
        public void setCaseType(String caseType) { this.caseType = caseType; }
        
        public String getProcessingMethod() { return processingMethod; }
        public void setProcessingMethod(String processingMethod) { this.processingMethod = processingMethod; }
        
        public String getLegalProcessingTime() { return legalProcessingTime; }
        public void setLegalProcessingTime(String legalProcessingTime) { this.legalProcessingTime = legalProcessingTime; }
        
        public String getPromisedProcessingTime() { return promisedProcessingTime; }
        public void setPromisedProcessingTime(String promisedProcessingTime) { this.promisedProcessingTime = promisedProcessingTime; }
        
        public String getHasFee() { return hasFee; }
        public void setHasFee(String hasFee) { this.hasFee = hasFee; }
        
        public String getExerciseMethod() { return exerciseMethod; }
        public void setExerciseMethod(String exerciseMethod) { this.exerciseMethod = exerciseMethod; }
        
        public String getOnSiteTimes() { return onSiteTimes; }
        public void setOnSiteTimes(String onSiteTimes) { this.onSiteTimes = onSiteTimes; }
        
        public List<ApprovalResult> getApprovalResults() { return approvalResults; }
        public void setApprovalResults(List<ApprovalResult> approvalResults) { this.approvalResults = approvalResults; }

        public String toText(){
            StringBuilder sb = new StringBuilder();
            sb.append("服务对象: " + this.getServiceObject() + "\n");
            sb.append("办件类型: " + this.getCaseType() + "\n");
            sb.append("办理形式: " + this.getProcessingMethod() + "\n");
            sb.append("法定办结时限: " + this.getLegalProcessingTime() + "\n");
            sb.append("承诺办结时限: " + this.getPromisedProcessingTime() + "\n");
            sb.append("是否收费: " + this.getHasFee() + "\n");
            sb.append("到办事现场次数: " + this.getOnSiteTimes() + "\n");
            return sb.toString();
        }
    }
    
    /**
     * 审批结果
     */
    static class ApprovalResult {
        private String type;
        private String name;
        private String sample;
        
        // getters and setters
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getSample() { return sample; }
        public void setSample(String sample) { this.sample = sample; }
    }
    
    /**
     * 申请材料
     */
    static class Material {
        private String index;
        private String name;
        private String necessity;
        private String type;
        private String form;
        private String paperCopies;
        
        // getters and setters
        public String getIndex() { return index; }
        public void setIndex(String index) { this.index = index; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getNecessity() { return necessity; }
        public void setNecessity(String necessity) { this.necessity = necessity; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public String getForm() { return form; }
        public void setForm(String form) { this.form = form; }
        
        public String getPaperCopies() { return paperCopies; }
        public void setPaperCopies(String paperCopies) { this.paperCopies = paperCopies; }
    }
    
    /**
     * 办理流程步骤
     */
    static class ProcessStep {
        private String stepName;
        private String processingTime;
        private String externalTime;
        private String description;
        
        // getters and setters
        public String getStepName() { return stepName; }
        public void setStepName(String stepName) { this.stepName = stepName; }
        
        public String getProcessingTime() { return processingTime; }
        public void setProcessingTime(String processingTime) { this.processingTime = processingTime; }
        
        public String getExternalTime() { return externalTime; }
        public void setExternalTime(String externalTime) { this.externalTime = externalTime; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
    
    /**
     * 设定依据
     */
    static class BasisRegulation {
        private String index;
        private String lawName;
        private String lawType;
        private String clause;
        
        // getters and setters
        public String getIndex() { return index; }
        public void setIndex(String index) { this.index = index; }
        
        public String getLawName() { return lawName; }
        public void setLawName(String lawName) { this.lawName = lawName; }
        
        public String getLawType() { return lawType; }
        public void setLawType(String lawType) { this.lawType = lawType; }
        
        public String getClause() { return clause; }
        public void setClause(String clause) { this.clause = clause; }
    }
    
    /**
     * 常见问题
     */
    static class FAQ {
        private String index;
        private String question;
        private String answer;
        
        // getters and setters
        public String getIndex() { return index; }
        public void setIndex(String index) { this.index = index; }
        
        public String getQuestion() { return question; }
        public void setQuestion(String question) { this.question = question; }
        
        public String getAnswer() { return answer; }
        public void setAnswer(String answer) { this.answer = answer; }
    }
}