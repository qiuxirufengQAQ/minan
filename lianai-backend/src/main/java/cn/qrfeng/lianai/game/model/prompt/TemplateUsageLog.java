package cn.qrfeng.lianai.game.model.prompt;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 模板使用日志
 */
@Data
public class TemplateUsageLog {

    /**
     * 日志 ID
     */
    private Long id;

    /**
     * 模板 ID
     */
    @JsonProperty("template_id")
    private Long templateId;

    /**
     * NPC ID
     */
    @JsonProperty("npc_id")
    private String npcId;

    /**
     * 场景 ID
     */
    @JsonProperty("scene_id")
    private String sceneId;

    /**
     * 用户 ID
     */
    @JsonProperty("user_id")
    private String userId;

    /**
     * 消耗 Token 数
     */
    @JsonProperty("tokens_used")
    private Integer tokensUsed;

    /**
     * 响应时间（毫秒）
     */
    @JsonProperty("response_time_ms")
    private Integer responseTimeMs;

    /**
     * 创建时间
     */
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
