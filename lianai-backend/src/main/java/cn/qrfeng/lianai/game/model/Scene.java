package cn.qrfeng.lianai.game.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("scene")
public class Scene {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String sceneId;

    private String levelId;

    private String name;

    @TableField("`order`")
    private Integer order;

    private String background;

    private String imageUrl;

    private String technique;

    private String coreConcept;

    private String dialogueExample;

    private String hintIds;

    private Integer difficulty;

    private Integer requiredIntimacyScore;

    private Integer estimatedTime;

    private String videoUrl;

    private String resourceIds;

    private Double maxScore;

    private Integer isActive;

    @TableField("reference_options")
    private String referenceOptions;

    /**
     * 最大对话轮次
     */
    private Integer maxConversationRounds;

    /**
     * 目标分数
     */
    private Double targetScore;

    /**
     * 是否启用 AI NPC
     */
    private Integer aiNpcEnabled;

    /**
     * 是否启用 AI 教练
     */
    private Integer aiCoachEnabled;

    /**
     * AI NPC 提示词模板
     */
    private String aiNpcPromptTemplate;

    /**
     * AI 教练提示词模板
     */
    private String aiCoachPromptTemplate;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}