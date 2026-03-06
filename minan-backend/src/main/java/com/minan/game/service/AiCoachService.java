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
     * 调用 AI API（带重试机制）
     */
    private String callAiApi(String systemPrompt, String userPrompt) throws Exception {
        int maxRetries = 3;
        int retryDelay = 1000; // 1 秒
        int attempt = 0;
        Exception lastException = null;

        while (attempt < maxRetries) {
            try {
                attempt++;
                log.debug("AI API 调用尝试 {}/{}", attempt, maxRetries);

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
                    .temperature(Float.parseFloat(String.valueOf(aiConfigService.getCoachTemperature())))
                    .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                    .build();

                // 调用 API
                GenerationResult result = generation.call(param);

                if (result != null && result.getOutput() != null && result.getOutput().getChoices() != null) {
                    String content = result.getOutput().getChoices().get(0).getMessage().getContent();
                    if (attempt > 1) {
                        log.info("AI API 调用在第 {} 次重试成功", attempt);
                    }
                    return content;
                } else {
                    throw new Exception("AI API 调用失败：" + (result != null ? result.getMessage() : "无响应"));
                }

            } catch (Exception e) {
                lastException = e;
                log.warn("AI API 调用失败 (尝试 {}/{}): {}", attempt, maxRetries, e.getMessage());
                
                if (attempt < maxRetries) {
                    try {
                        Thread.sleep(retryDelay * attempt); // 指数退避
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new Exception("重试被中断", ie);
                    }
                }
            }
        }

        throw new Exception("AI API 调用失败，已重试 " + maxRetries + " 次", lastException);
    }

    /**
     * 解析评估结果（容错增强版）
     */
    private EvaluationResult parseEvaluationResult(String response) throws JsonProcessingException {
        log.info("AI 教练原始回复：{}", response.substring(0, Math.min(200, response.length())));

        try {
            // 尝试提取 JSON（AI 可能返回 markdown 格式）
            String jsonStr = extractJson(response);
            JsonNode root = objectMapper.readTree(jsonStr);

            EvaluationResult result = new EvaluationResult();

            // 解析总分（容错：支持整数和浮点数）
            if (root.has("totalScore")) {
                result.setTotalScore(root.get("totalScore").asDouble());
            } else if (root.has("score")) {
                // 兼容不同的字段名
                result.setTotalScore(root.get("score").asDouble());
            }

            // 解析维度得分（动态解析所有维度，而非硬编码）
            if (root.has("dimensionScores")) {
                Map<String, Integer> scores = new HashMap<>();
                JsonNode dimNode = root.get("dimensionScores");
                
                if (dimNode != null && dimNode.isObject()) {
                    // 动态遍历所有维度字段
                    dimNode.fieldNames().forEachRemaining(fieldName -> {
                        try {
                            JsonNode valueNode = dimNode.get(fieldName);
                            if (valueNode != null && valueNode.isNumber()) {
                                scores.put(fieldName, valueNode.asInt());
                            }
                        } catch (Exception e) {
                            log.warn("解析维度 {} 失败：{}", fieldName, e.getMessage());
                        }
                    });
                    
                    // 如果动态解析没有结果，使用默认维度（向后兼容）
                    if (scores.isEmpty()) {
                        log.warn("未解析到维度得分，使用默认维度");
                        scores = buildDefaultDimensionScores();
                    }
                }
                result.setDimensionScores(scores);
            }

            // 解析优点（容错：支持数组和字符串）
            if (root.has("strengths")) {
                List<String> strengths = new ArrayList<>();
                JsonNode strengthsNode = root.get("strengths");
                if (strengthsNode != null && strengthsNode.isArray()) {
                    strengthsNode.forEach(node -> {
                        if (node != null && !node.asText().isEmpty()) {
                            strengths.add(node.asText());
                        }
                    });
                } else if (strengthsNode != null && !strengthsNode.asText().isEmpty()) {
                    // 如果是字符串，按换行分割
                    String[] items = strengthsNode.asText().split("\\n");
                    for (String item : items) {
                        String trimmed = item.trim().replaceAll("^[-*•]\\s*", "");
                        if (!trimmed.isEmpty()) {
                            strengths.add(trimmed);
                        }
                    }
                }
                result.setStrengths(strengths);
            }

            // 解析建议（容错处理同上）
            if (root.has("suggestions")) {
                List<String> suggestions = new ArrayList<>();
                JsonNode suggestionsNode = root.get("suggestions");
                if (suggestionsNode != null && suggestionsNode.isArray()) {
                    suggestionsNode.forEach(node -> {
                        if (node != null && !node.asText().isEmpty()) {
                            suggestions.add(node.asText());
                        }
                    });
                } else if (suggestionsNode != null && !suggestionsNode.asText().isEmpty()) {
                    String[] items = suggestionsNode.asText().split("\\n");
                    for (String item : items) {
                        String trimmed = item.trim().replaceAll("^[-*•]\\s*", "");
                        if (!trimmed.isEmpty()) {
                            suggestions.add(trimmed);
                        }
                    }
                }
                result.setSuggestions(suggestions);
            }

            // 解析示范对话
            if (root.has("exampleDialogue")) {
                result.setExampleDialogue(root.get("exampleDialogue").asText());
            } else if (root.has("example")) {
                // 兼容不同的字段名
                result.setExampleDialogue(root.get("example").asText());
            }

            // 解析知识点推荐
            if (root.has("knowledgeRecommendations")) {
                List<String> recommendations = new ArrayList<>();
                JsonNode recNode = root.get("knowledgeRecommendations");
                if (recNode != null && recNode.isArray()) {
                    recNode.forEach(node -> {
                        if (node != null && !node.asText().isEmpty()) {
                            recommendations.add(node.asText());
                        }
                    });
                }
                result.setKnowledgeRecommendations(recommendations);
            } else if (root.has("recommendations")) {
                // 兼容不同的字段名
                List<String> recommendations = new ArrayList<>();
                root.get("recommendations").forEach(node -> {
                    if (node != null && !node.asText().isEmpty()) {
                        recommendations.add(node.asText());
                    }
                });
                result.setKnowledgeRecommendations(recommendations);
            }

            return result;

        } catch (Exception e) {
            log.error("解析评估结果失败：{}", e.getMessage(), e);
            // 返回默认评估结果
            return buildDefaultEvaluationResult();
        }
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
