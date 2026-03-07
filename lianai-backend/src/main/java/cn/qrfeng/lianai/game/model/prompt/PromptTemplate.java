package cn.qrfeng.lianai.game.model.prompt;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
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
    private Map<String, VariableMapping> variableMapping;

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
