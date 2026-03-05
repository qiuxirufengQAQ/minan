package com.minan.game.service;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.minan.game.model.ConversationRecord;
import com.minan.game.model.Scene;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI 教练服务
 */
@Slf4j
@Service
public class AiCoachService {

    @Autowired
    private AiConfigService aiConfigService;

    private final Generation generation = new Generation();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 评估对话
     * 
     * @param conversationRecords 对话记录列表
     * @param scene 场景信息
     * @return 评估结果
     */
    public EvaluationResult evaluateConversation(
        List<ConversationRecord> conversationRecords,
        Scene scene
    ) {
        try {
            // 1. 构建评估 Prompt
            String systemPrompt = buildSystemPrompt();
            String userPrompt = buildUserPrompt(conversationRecords, scene);

            // 2. 调用 AI API
            String response = callAiApi(systemPrompt, userPrompt);

            // 3. 解析评估结果
            EvaluationResult result = parseEvaluationResult(response);

            // 4. 补充默认值
            if (result.getDimensionScores() == null) {
                result.setDimensionScores(buildDefaultDimensionScores());
            }
            if (result.getTotalScore() == null) {
                result.setTotalScore(calculateTotalScore(result.getDimensionScores()));
            }

            log.info("AI 教练评估完成：总分={}, 轮次={}", 
                result.getTotalScore(), conversationRecords.size());

            return result;

        } catch (Exception e) {
            log.error("AI 教练评估失败", e);
            // 返回默认评估结果
            return buildDefaultEvaluationResult();
        }
    }

    /**
     * 构建 System Prompt
     */
    private String buildSystemPrompt() {
        StringBuilder prompt = new StringBuilder();

        prompt.append("你是一位专业的恋爱沟通教练，擅长分析对话并提供建设性反馈。\n");
        prompt.append("请根据用户提供的对话记录，从以下维度进行评估：\n");
        prompt.append("1. 共情能力（30%）：是否能理解对方感受，表达关心\n");
        prompt.append("2. 沟通技巧（30%）：表达方式是否得体，是否善于倾听\n");
        prompt.append("3. 幽默感（20%）：是否适当活跃气氛，但不冒犯对方\n");
        prompt.append("4. 边界感（20%）：是否尊重对方边界，不过度追问隐私\n\n");
        prompt.append("请以 JSON 格式返回评估结果，包含以下字段：\n");
        prompt.append("- totalScore: 总分（0-100）\n");
        prompt.append("- dimensionScores: 各维度得分（1-5 分）\n");
        prompt.append("- strengths: 优点列表（至少 3 条）\n");
        prompt.append("- suggestions: 改进建议（至少 2 条）\n");
        prompt.append("- exampleDialogue: 示范对话（1 个例子）\n");
        prompt.append("- knowledgeRecommendations: 知识点推荐（1-3 个）\n\n");
        prompt.append("评估要客观公正，以鼓励为主，建议要具体可操作。");

        return prompt.toString();
    }

    /**
     * 构建 User Prompt
     */
    private String buildUserPrompt(List<ConversationRecord> records, Scene scene) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("对话记录：\n\n");

        for (int i = 0; i < records.size(); i++) {
            ConversationRecord record = records.get(i);
            prompt.append("第").append(i + 1).append("轮：\n");
            prompt.append("用户：").append(record.getUserInput()).append("\n");
            prompt.append("NPC：").append(record.getNpcResponse()).append("\n\n");
        }

        if (scene != null) {
            prompt.append("场景信息：\n");
            if (scene.getName() != null) {
                prompt.append("- 场景名称：").append(scene.getName()).append("\n");
            }
            if (scene.getTechnique() != null) {
                prompt.append("- 知识点：").append(scene.getTechnique()).append("\n");
            }
            if (scene.getBackground() != null) {
                prompt.append("- 场景背景：").append(scene.getBackground()).append("\n");
            }
        }

        prompt.append("\n请根据以上对话记录进行评估：");

        return prompt.toString();
    }

    /**
     * 调用 AI API
     */
    private String callAiApi(String systemPrompt, String userPrompt) throws Exception {
        List<Message> messages = new ArrayList<>();

        // 添加 System Message
        messages.add(Message.builder()
            .role(Role.SYSTEM.getValue())
            .content(systemPrompt)
            .build());

        // 添加 User Message
        messages.add(Message.builder()
            .role(Role.USER.getValue())
            .content(userPrompt)
            .build());

        // 构建请求参数
        GenerationParam param = GenerationParam.builder()
            .apiKey(aiConfigService.getApiKey())
            .model(aiConfigService.getCoachModel())
            .messages(messages)
            .maxTokens(aiConfigService.getCoachMaxTokens())
            .temperature(aiConfigService.getCoachTemperature())
            .resultFormat(GenerationParam.ResultFormat.MESSAGE)
            .build();

        // 调用 API
        GenerationResult result = generation.call(param);

        if (result.getStatusCode() == 200 && result.getOutput() != null) {
            return result.getOutput().getChoices().get(0).getMessage().getContent();
        } else {
            throw new Exception("AI API 调用失败：" + result.getMessage());
        }
    }

    /**
     * 解析评估结果
     */
    private EvaluationResult parseEvaluationResult(String response) throws JsonProcessingException {
        log.info("AI 教练原始回复：{}", response.substring(0, Math.min(200, response.length())));

        // 尝试提取 JSON（AI 可能返回 markdown 格式）
        String jsonStr = extractJson(response);

        JsonNode root = objectMapper.readTree(jsonStr);

        EvaluationResult result = new EvaluationResult();

        // 解析总分
        if (root.has("totalScore")) {
            result.setTotalScore(root.get("totalScore").asDouble());
        }

        // 解析维度得分
        if (root.has("dimensionScores")) {
            Map<String, Integer> scores = new HashMap<>();
            JsonNode dimNode = root.get("dimensionScores");
            if (dimNode.has("共情能力")) {
                scores.put("共情能力", dimNode.get("共情能力").asInt());
            }
            if (dimNode.has("沟通技巧")) {
                scores.put("沟通技巧", dimNode.get("沟通技巧").asInt());
            }
            if (dimNode.has("幽默感")) {
                scores.put("幽默感", dimNode.get("幽默感").asInt());
            }
            if (dimNode.has("边界感")) {
                scores.put("边界感", dimNode.get("边界感").asInt());
            }
            result.setDimensionScores(scores);
        }

        // 解析优点
        if (root.has("strengths")) {
            List<String> strengths = new ArrayList<>();
            root.get("strengths").forEach(node -> strengths.add(node.asText()));
            result.setStrengths(strengths);
        }

        // 解析建议
        if (root.has("suggestions")) {
            List<String> suggestions = new ArrayList<>();
            root.get("suggestions").forEach(node -> suggestions.add(node.asText()));
            result.setSuggestions(suggestions);
        }

        // 解析示范对话
        if (root.has("exampleDialogue")) {
            result.setExampleDialogue(root.get("exampleDialogue").asText());
        }

        // 解析知识点推荐
        if (root.has("knowledgeRecommendations")) {
            List<String> recommendations = new ArrayList<>();
            root.get("knowledgeRecommendations").forEach(node -> recommendations.add(node.asText()));
            result.setKnowledgeRecommendations(recommendations);
        }

        return result;
    }

    /**
     * 提取 JSON 字符串
     */
    private String extractJson(String response) {
        // 尝试提取 ```json 和 ``` 之间的内容
        int start = response.indexOf("```json");
        if (start == -1) {
            start = response.indexOf("```");
        }
        
        if (start != -1) {
            int end = response.indexOf("```", start + 3);
            if (end != -1) {
                return response.substring(start + 7, end).trim();
            }
        }

        // 如果没有 markdown，直接返回
        return response.trim();
    }

    /**
     * 构建默认维度得分
     */
    private Map<String, Integer> buildDefaultDimensionScores() {
        Map<String, Integer> scores = new HashMap<>();
        scores.put("共情能力", 3);
        scores.put("沟通技巧", 3);
        scores.put("幽默感", 3);
        scores.put("边界感", 4);
        return scores;
    }

    /**
     * 计算总分
     */
    private Double calculateTotalScore(Map<String, Integer> dimensionScores) {
        if (dimensionScores == null || dimensionScores.isEmpty()) {
            return 75.0;
        }

        double total = 0;
        int count = 0;

        for (Map.Entry<String, Integer> entry : dimensionScores.entrySet()) {
            total += entry.getValue() * 20; // 5 分制转 100 分制
            count++;
        }

        return count > 0 ? total / count : 75.0;
    }

    /**
     * 构建默认评估结果
     */
    private EvaluationResult buildDefaultEvaluationResult() {
        EvaluationResult result = new EvaluationResult();
        result.setTotalScore(75.0);
        result.setDimensionScores(buildDefaultDimensionScores());

        List<String> strengths = new ArrayList<>();
        strengths.add("能够主动开启对话");
        strengths.add("表达方式比较自然");
        strengths.add("态度友好");
        result.setStrengths(strengths);

        List<String> suggestions = new ArrayList<>();
        suggestions.add("可以多倾听对方，给予更多回应");
        suggestions.add("适当增加一些幽默元素会让对话更有趣");
        result.setSuggestions(suggestions);

        result.setExampleDialogue("用户：你平时喜欢做什么？\nNPC：我喜欢看书和喝咖啡～你呢？\n用户：我也喜欢看书，最近在读什么书？");

        List<String> recommendations = new ArrayList<>();
        recommendations.add("倾听的艺术");
        recommendations.add("如何找到共同话题");
        result.setKnowledgeRecommendations(recommendations);

        return result;
    }

    // ==================== 内部类 ====================

    /**
     * 评估结果
     */
    public static class EvaluationResult {
        private Double totalScore;
        private Map<String, Integer> dimensionScores;
        private List<String> strengths;
        private List<String> suggestions;
        private String exampleDialogue;
        private List<String> knowledgeRecommendations;

        // Getters and Setters
        public Double getTotalScore() { return totalScore; }
        public void setTotalScore(Double totalScore) { this.totalScore = totalScore; }
        public Map<String, Integer> getDimensionScores() { return dimensionScores; }
        public void setDimensionScores(Map<String, Integer> dimensionScores) { this.dimensionScores = dimensionScores; }
        public List<String> getStrengths() { return strengths; }
        public void setStrengths(List<String> strengths) { this.strengths = strengths; }
        public List<String> getSuggestions() { return suggestions; }
        public void setSuggestions(List<String> suggestions) { this.suggestions = suggestions; }
        public String getExampleDialogue() { return exampleDialogue; }
        public void setExampleDialogue(String exampleDialogue) { this.exampleDialogue = exampleDialogue; }
        public List<String> getKnowledgeRecommendations() { return knowledgeRecommendations; }
        public void setKnowledgeRecommendations(List<String> knowledgeRecommendations) { this.knowledgeRecommendations = knowledgeRecommendations; }
    }
}
