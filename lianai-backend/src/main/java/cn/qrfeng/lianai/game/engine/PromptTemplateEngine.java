package cn.qrfeng.lianai.game.engine;

import cn.qrfeng.lianai.game.entity.NpcCharacter;
import cn.qrfeng.lianai.game.entity.Scene;
import cn.qrfeng.lianai.game.entity.User;
import cn.qrfeng.lianai.game.mapper.prompt.PromptTemplateMapper;
import cn.qrfeng.lianai.game.mapper.prompt.PromptTemplateUsageMapper;
import cn.qrfeng.lianai.game.model.prompt.PromptTemplate;
import cn.qrfeng.lianai.game.model.prompt.TemplateUsageLog;
import cn.qrfeng.lianai.game.model.prompt.VariableMapping;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 提示词模板引擎
 * 支持动态变量注入、条件渲染、版本管理
 */
@Slf4j
@Component
public class PromptTemplateEngine {

    @Autowired
    private PromptTemplateMapper templateMapper;

    @Autowired
    private PromptTemplateUsageMapper usageMapper;

    @Autowired
    private ObjectMapper objectMapper;

    // 模板缓存：key = templateType:version
    private final Map<String, CachedTemplate> templateCache = new ConcurrentHashMap<>();
    private final long cacheTtlMs = 300_000; // 5 分钟

    // 条件渲染正则：{#if var}...{/if} 和 {#if var}...{#else}...{/if}
    private static final Pattern CONDITIONAL_PATTERN = 
        Pattern.compile("\\{#if\\s+(\\w+)}(.*?)(?:\\{#else}(.*?))?\\{/if}", Pattern.DOTALL);

    /**
     * 构建提示词
     *
     * @param templateType 模板类型
     * @param context      上下文数据
     * @return 构建好的提示词
     */
    public String buildPrompt(String templateType, Map<String, Object> context) {
        long startTime = System.currentTimeMillis();

        try {
            // 1. 获取模板
            PromptTemplate template = getTemplate(templateType, null);
            if (template == null) {
                throw new IllegalArgumentException("模板不存在：" + templateType);
            }

            // 2. 处理条件渲染
            String content = processConditionals(template.getContent(), context);

            // 3. 解析变量
            Map<String, Object> variables = parseVariables(template, context);

            // 4. 渲染模板
            String prompt = renderTemplate(content, variables);

            long buildTime = System.currentTimeMillis() - startTime;
            log.debug("模板 [{}] 构建完成，耗时：{}ms, Token 估算：{}", 
                     templateType, buildTime, prompt.length() / 4);

            return prompt;

        } catch (Exception e) {
            log.error("模板 [{}] 构建失败：{}", templateType, e.getMessage(), e);
            throw new RuntimeException("模板构建失败：" + e.getMessage(), e);
        }
    }

    /**
     * 获取模板
     */
    public PromptTemplate getTemplate(String templateType, Integer version) {
        String cacheKey = templateType + ":" + (version != null ? version : "latest");

        // 检查缓存
        CachedTemplate cached = templateCache.get(cacheKey);
        if (cached != null && System.currentTimeMillis() - cached.timestamp < cacheTtlMs) {
            log.debug("命中模板缓存：{}", cacheKey);
            return cached.template;
        }

        // 从数据库获取
        PromptTemplate template;
        if (version != null) {
            template = templateMapper.selectByTypeAndVersion(templateType, version);
        } else {
            template = templateMapper.selectLatest(templateType);
        }

        if (template != null) {
            templateCache.put(cacheKey, new CachedTemplate(template, System.currentTimeMillis()));
            log.debug("加载模板：{} v{}", templateType, template.getVersion());
        }

        return template;
    }

    /**
     * 处理条件渲染
     */
    private String processConditionals(String content, Map<String, Object> context) {
        Matcher matcher = CONDITIONAL_PATTERN.matcher(content);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String varName = matcher.group(1);
            String ifContent = matcher.group(2);
            String elseContent = matcher.group(3);

            Object value = getVariableValue(varName, context);
            boolean isTrue = value != null && !value.toString().isEmpty();

            String replacement = isTrue ? ifContent : (elseContent != null ? elseContent : "");
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);

        return sb.toString();
    }

    /**
     * 解析变量
     */
    private Map<String, Object> parseVariables(PromptTemplate template, Map<String, Object> context) 
            throws Exception {
        Map<String, Object> variables = new HashMap<>();
        Map<String, VariableMapping> mapping = template.getVariableMapping();

        for (Map.Entry<String, VariableMapping> entry : mapping.entrySet()) {
            String varName = entry.getKey();
            VariableMapping mappingConfig = entry.getValue();

            try {
                Object value = fetchVariableValue(mappingConfig, context);
                
                // 特殊处理：对话历史
                if ("conversation_history".equals(varName)) {
                    value = formatConversationHistory(context, mappingConfig.getMaxRounds());
                }

                // 变量转换
                value = transformVariable(value, mappingConfig);

                variables.put(varName, value);

            } catch (Exception e) {
                if (mappingConfig.isRequired()) {
                    log.error("必填变量 [{}] 获取失败：{}", varName, e.getMessage());
                    throw e;
                } else {
                    log.warn("可选变量 [{}] 获取失败，使用默认值", varName);
                    variables.put(varName, mappingConfig.getDefaultValue());
                }
            }
        }

        return variables;
    }

    /**
     * 获取变量值
     */
    private Object fetchVariableValue(VariableMapping mapping, Map<String, Object> context) {
        String source = mapping.getSource();
        String field = mapping.getField();

        switch (source) {
            case "dynamic":
                return context.get(field);

            case "npc_character":
                NpcCharacter npc = (NpcCharacter) context.get("npc_character");
                return npc != null ? getFieldValue(npc, field) : null;

            case "scene":
                Scene scene = (Scene) context.get("scene");
                return scene != null ? getFieldValue(scene, field) : null;

            case "user":
                User user = (User) context.get("user");
                return user != null ? getFieldValue(user, field) : null;

            case "computed":
                return computeVariable(mapping, context);

            default:
                return mapping.getDefaultValue();
        }
    }

    /**
     * 获取对象字段值（反射）
     */
    private Object getFieldValue(Object obj, String fieldName) {
        try {
            String methodName = "get" + fieldName.substring(0, 1).toUpperCase() 
                              + fieldName.substring(1);
            return obj.getClass().getMethod(methodName).invoke(obj);
        } catch (Exception e) {
            log.warn("获取字段 [{}] 失败：{}", fieldName, e.getMessage());
            return null;
        }
    }

    /**
     * 计算变量
     */
    private Object computeVariable(VariableMapping mapping, Map<String, Object> context) {
        String computeExpr = mapping.getComputeExpression();
        if (computeExpr == null || computeExpr.isEmpty()) {
            return mapping.getDefaultValue();
        }

        // 简单的表达式计算（支持三元运算符）
        if (computeExpr.contains("?")) {
            return evaluateTernaryExpression(computeExpr, context);
        }

        return mapping.getDefaultValue();
    }

    /**
     * 计算三元表达式
     */
    private Object evaluateTernaryExpression(String expr, Map<String, Object> context) {
        // 简化实现：gender == 'male' ? '他' : '她'
        try {
            if (expr.contains("gender")) {
                NpcCharacter npc = (NpcCharacter) context.get("npc_character");
                if (npc != null && "male".equals(npc.getGender())) {
                    return expr.contains("'他'") || expr.contains("\"他\"") ? "他" : "他";
                }
                return expr.contains("'她'") || expr.contains("\"她\"") ? "她" : "她";
            }
        } catch (Exception e) {
            log.warn("表达式计算失败：{}", expr);
        }
        return null;
    }

    /**
     * 变量转换
     */
    private Object transformVariable(Object value, VariableMapping mapping) {
        String transform = mapping.getTransform();
        if (transform == null || transform.isEmpty()) {
            return value;
        }

        switch (transform) {
            case "string":
                return value != null ? value.toString() : mapping.getDefaultValue();

            case "int":
                if (value instanceof Number) {
                    return ((Number) value).intValue();
                }
                try {
                    return Integer.parseInt(value.toString());
                } catch (NumberFormatException e) {
                    return mapping.getDefaultValue();
                }

            case "map":
                Map<String, String> mapConfig = mapping.getTransformConfig();
                if (mapConfig != null) {
                    return mapConfig.getOrDefault(value != null ? value.toString() : "", 
                                                 value != null ? value.toString() : mapping.getDefaultValue());
                }
                return value;

            default:
                return value;
        }
    }

    /**
     * 格式化对话历史
     */
    private String formatConversationHistory(Map<String, Object> context, int maxRounds) {
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> history = (List<Map<String, Object>>) context.get("conversation_history");
        
        if (history == null || history.isEmpty()) {
            return "";
        }

        List<Map<String, Object>> recent = history;
        if (history.size() > maxRounds) {
            recent = history.subList(history.size() - maxRounds, history.size());
        }

        StringBuilder sb = new StringBuilder();
        for (Map<String, Object> item : recent) {
            sb.append("用户：").append(item.get("userInput")).append("\n");
            sb.append("NPC: ").append(item.get("npcResponse")).append("\n");
        }

        return sb.toString().trim();
    }

    /**
     * 渲染模板
     */
    private String renderTemplate(String content, Map<String, Object> variables) {
        String result = content;

        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            String placeholder = "{" + entry.getKey() + "}";
            String value = entry.getValue() != null ? entry.getValue().toString() : "";
            result = result.replace(placeholder, value);
        }

        return result;
    }

    /**
     * 记录使用日志
     */
    public void logUsage(Long templateId, String npcId, String sceneId, String userId, 
                        int tokensUsed, int responseTimeMs) {
        try {
            TemplateUsageLog log = new TemplateUsageLog();
            log.setTemplateId(templateId);
            log.setNpcId(npcId);
            log.setSceneId(sceneId);
            log.setUserId(userId);
            log.setTokensUsed(tokensUsed);
            log.setResponseTimeMs(responseTimeMs);
            log.setCreatedAt(LocalDateTime.now());

            usageMapper.insert(log);
        } catch (Exception e) {
            log.error("记录模板使用日志失败：{}", e.getMessage());
        }
    }

    /**
     * 清除缓存
     */
    public void clearCache(String templateType) {
        if (templateType != null) {
            templateCache.keySet().removeIf(key -> key.startsWith(templateType + ":"));
            log.info("清除模板缓存：{}", templateType);
        } else {
            templateCache.clear();
            log.info("清除所有模板缓存");
        }
    }

    /**
     * 测试模板
     */
    public Map<String, Object> testTemplate(String templateType, Map<String, Object> testContext) {
        Map<String, Object> result = new HashMap<>();
        long startTime = System.currentTimeMillis();

        try {
            String prompt = buildPrompt(templateType, testContext);
            
            result.put("success", true);
            result.put("prompt", prompt);
            result.put("tokensEstimated", prompt.length() / 4);
            result.put("buildTimeMs", System.currentTimeMillis() - startTime);
            result.put("variablesUsed", testContext.size());

        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
            result.put("buildTimeMs", System.currentTimeMillis() - startTime);
        }

        return result;
    }

    /**
     * 缓存的模板
     */
    private static class CachedTemplate {
        PromptTemplate template;
        long timestamp;

        CachedTemplate(PromptTemplate template, long timestamp) {
            this.template = template;
            this.timestamp = timestamp;
        }
    }
}
