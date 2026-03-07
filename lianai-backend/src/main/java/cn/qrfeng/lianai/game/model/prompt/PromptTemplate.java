package cn.qrfeng.lianai.game.model.prompt;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Map;

/**
 * 提示词模板实体
 */
@Data
public class PromptTemplate {

    /**
     * 模板 ID
     */
    private Long id;

    /**
     * 模板名称
     */
    private String name;

    /**
     * 模板类型：role_play/coach_evaluation/scene_introduction
     */
    @JsonProperty("template_type")
    private String templateType;

    /**
     * 模板内容
     */
    @JsonProperty("template_content")
    private String content;

    /**
     * 变量映射配置（JSON）
     */
    @JsonProperty("variable_mapping")
    private String variableMappingJson;

    /**
     * 获取解析后的变量映射（延迟解析）
     */
    @SuppressWarnings("unchecked")
    public Map<String, VariableMapping> getVariableMappingMap() {
        if (variableMappingJson == null || variableMappingJson.isEmpty()) {
            return new HashMap<>();
        }
        
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> rawMap = mapper.readValue(variableMappingJson, Map.class);
            Map<String, VariableMapping> result = new HashMap<>();
            
            for (Map.Entry<String, Object> entry : rawMap.entrySet()) {
                if (entry.getValue() instanceof Map) {
                    Map<?, ?> rawConfig = (Map<?, ?>) entry.getValue();
                    VariableMapping config = new VariableMapping();
                    config.setSource(rawConfig.get("source") != null ? rawConfig.get("source").toString() : null);
                    config.setField(rawConfig.get("field") != null ? rawConfig.get("field").toString() : null);
                    config.setRequired(rawConfig.get("required") instanceof Boolean ? (Boolean) rawConfig.get("required") : null);
                    config.setDefaultValue(rawConfig.get("default"));
                    config.setMaxRounds(rawConfig.get("max_rounds") instanceof Number ? ((Number) rawConfig.get("max_rounds")).intValue() : null);
                    config.setTransform(rawConfig.get("transform") != null ? rawConfig.get("transform").toString() : null);
                    result.put(entry.getKey().toString(), config);
                }
            }
            return result;
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    /**
     * 版本号
     */
    private Integer version;

    /**
     * 版本描述
     */
    private String description;

    /**
     * 是否启用
     */
    @JsonProperty("is_active")
    private Boolean active;

    /**
     * 创建时间
     */
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}
