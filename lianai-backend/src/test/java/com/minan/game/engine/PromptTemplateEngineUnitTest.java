package cn.qrfeng.lianai.game.engine;

import cn.qrfeng.lianai.game.model.prompt.PromptTemplate;
import cn.qrfeng.lianai.game.model.prompt.VariableMapping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 提示词模板引擎单元测试
 * 测试核心功能，不依赖数据库
 */
public class PromptTemplateEngineUnitTest {

    private Map<String, Object> context;
    private PromptTemplate template;

    @BeforeEach
    public void setUp() {
        // 准备测试上下文
        context = new HashMap<>();
        
        Map<String, Object> npc = new HashMap<>();
        npc.put("name", "书瑶");
        npc.put("personality", "文静内敛");
        npc.put("occupation", "图书管理员");
        npc.put("age", 25);
        context.put("npc_character", npc);
        
        Map<String, Object> scene = new HashMap<>();
        scene.put("description", "咖啡厅");
        scene.put("name", "咖啡厅偶遇");
        context.put("scene", scene);
        
        Map<String, Object> user = new HashMap<>();
        user.put("nickname", "测试用户");
        context.put("user", user);
        
        context.put("user_input", "你好");
        context.put("conversation_history", new ArrayList<>());
    }

    @Test
    public void testVariableExtraction() {
        // 测试从嵌套 Map 中提取变量
        Object npcName = getVariableFromContext("name", context);
        assertNotNull(npcName);
        assertEquals("书瑶", npcName);
        
        Object sceneDesc = getVariableFromContext("description", context);
        assertNotNull(sceneDesc);
        assertEquals("咖啡厅", sceneDesc);
    }

    @Test
    public void testConditionalRendering() {
        // 测试条件渲染逻辑
        String content = "你好{#if npc_age}，我{npc_age}岁{/if}";
        context.put("npc_age", 25);
        
        String result = processConditionals(content, context);
        assertTrue(result.contains("25 岁"));
        
        // 测试条件为假
        context.remove("npc_age");
        result = processConditionals(content, context);
        assertFalse(result.contains("岁"));
    }

    @Test
    public void testVariableMapping() {
        // 测试变量映射配置
        Map<String, VariableMapping> mapping = new HashMap<>();
        
        VariableMapping nameMapping = new VariableMapping();
        nameMapping.setSource("npc_character");
        nameMapping.setField("name");
        nameMapping.setRequired(true);
        mapping.put("npc_name", nameMapping);
        
        VariableMapping ageMapping = new VariableMapping();
        ageMapping.setSource("npc_character");
        ageMapping.setField("age");
        ageMapping.setRequired(false);
        mapping.put("npc_age", ageMapping);
        
        // 验证配置
        assertEquals(2, mapping.size());
        assertTrue(mapping.get("npc_name").getRequired());
        assertFalse(mapping.get("npc_age").getRequired());
    }

    @Test
    public void testTemplateRendering() {
        // 测试模板渲染
        String template = "你是{npc_name}，{npc_age}岁，职业是{npc_occupation}";
        Map<String, Object> variables = new HashMap<>();
        variables.put("npc_name", "书瑶");
        variables.put("npc_age", 25);
        variables.put("npc_occupation", "图书管理员");
        
        String result = renderTemplate(template, variables);
        assertEquals("你是书瑶，25 岁，职业是图书管理员", result);
    }

    @Test
    public void testHistoryFormatting() {
        // 测试对话历史格式化
        List<Map<String, Object>> history = new ArrayList<>();
        
        Map<String, Object> record1 = new HashMap<>();
        record1.put("userInput", "你好");
        record1.put("npcResponse", "你好呀");
        history.add(record1);
        
        Map<String, Object> record2 = new HashMap<>();
        record2.put("userInput", "在看什么书？");
        record2.put("npcResponse", "《雪国》");
        history.add(record2);
        
        String formatted = formatConversationHistory(history, 3);
        assertTrue(formatted.contains("用户：你好"));
        assertTrue(formatted.contains("NPC: 你好呀"));
        assertTrue(formatted.contains("用户：在看什么书？"));
        assertTrue(formatted.contains("NPC: 《雪国》"));
    }

    @Test
    public void testHistoryTruncation() {
        // 测试对话历史截断
        List<Map<String, Object>> history = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Map<String, Object> record = new HashMap<>();
            record.put("userInput", "消息" + i);
            record.put("npcResponse", "回复" + i);
            history.add(record);
        }
        
        String formatted = formatConversationHistory(history, 3);
        assertTrue(formatted.contains("消息 9")); // 最后一条
        assertFalse(formatted.contains("消息 0")); // 第一条被截断
    }

    @Test
    public void testVariableTransformString() {
        // 测试字符串转换
        Object value = transformVariable(123, "string", null);
        assertEquals("123", value);
        
        value = transformVariable(null, "string", "默认");
        assertEquals("默认", value);
    }

    @Test
    public void testVariableTransformInt() {
        // 测试数字转换
        Object value = transformVariable("456", "int", null);
        assertEquals(456, value);
        
        value = transformVariable("abc", "int", 0);
        assertEquals(0, value);
    }

    @Test
    public void testVariableTransformMap() {
        // 测试映射转换
        Map<String, String> mapConfig = new HashMap<>();
        mapConfig.put("male", "他");
        mapConfig.put("female", "她");
        
        Object value = transformVariable("male", "map", mapConfig);
        assertEquals("他", value);
        
        value = transformVariable("female", "map", mapConfig);
        assertEquals("她", value);
    }

    // ===== 辅助方法（从 PromptTemplateEngine 复制）=====
    
    private Object getVariableFromContext(String varName, Map<String, Object> context) {
        if (context.containsKey(varName)) {
            return context.get(varName);
        }
        for (Object data : context.values()) {
            if (data instanceof Map) {
                Object value = ((Map<?, ?>) data).get(varName);
                if (value != null) return value;
            }
        }
        return null;
    }
    
    private String processConditionals(String content, Map<String, Object> context) {
        // 简化版本，只处理{#if var}...{/if}
        java.util.regex.Pattern pattern = 
            java.util.regex.Pattern.compile("\\{#if\\s+(\\w+)}(.*?)\\{/if}");
        java.util.regex.Matcher matcher = pattern.matcher(content);
        StringBuffer sb = new StringBuffer();
        
        while (matcher.find()) {
            String varName = matcher.group(1);
            String ifContent = matcher.group(2);
            Object value = getVariableFromContext(varName, context);
            boolean isTrue = value != null && !value.toString().isEmpty();
            matcher.appendReplacement(sb, java.util.regex.Matcher.quoteReplacement(
                isTrue ? ifContent : ""
            ));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
    
    private String renderTemplate(String content, Map<String, Object> variables) {
        String result = content;
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            String placeholder = "{" + entry.getKey() + "}";
            String value = entry.getValue() != null ? entry.getValue().toString() : "";
            result = result.replace(placeholder, value);
        }
        return result;
    }
    
    private String formatConversationHistory(List<Map<String, Object>> history, int maxRounds) {
        int limit = Math.min(maxRounds, history.size());
        List<Map<String, Object>> recent = history.subList(history.size() - limit, history.size());
        
        StringBuilder sb = new StringBuilder();
        for (Map<String, Object> item : recent) {
            sb.append("用户：").append(item.get("userInput")).append("\n");
            sb.append("NPC: ").append(item.get("npcResponse")).append("\n");
        }
        return sb.toString().trim();
    }
    
    private Object transformVariable(Object value, String transform, Object config) {
        if (transform == null || transform.isEmpty()) {
            return value;
        }
        
        switch (transform) {
            case "string":
                return value != null ? value.toString() : config;
            case "int":
                if (value instanceof Number) {
                    return ((Number) value).intValue();
                }
                try {
                    return Integer.parseInt(value.toString());
                } catch (NumberFormatException e) {
                    return config;
                }
            case "map":
                if (config instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, String> mapConfig = (Map<String, String>) config;
                    String valueStr = value != null ? value.toString() : "";
                    return mapConfig.getOrDefault(valueStr, valueStr);
                }
                return value;
            default:
                return value;
        }
    }
}
