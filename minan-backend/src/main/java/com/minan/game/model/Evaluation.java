package com.minan.game.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("evaluation")
public class Evaluation {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 评估唯一标识
     */
    private String evaluationId;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 场景ID
     */
    private String sceneId;

    /**
     * 用户输入的回应
     */
    private String userInput;

    /**
     * 评估结果（JSON格式）
     */
    private String evaluationResult;

    /**
     * 评估得分
     */
    private Double score;

    /**
     * 获得的魅力值
     */
    private Integer cpGained;

    /**
     * 是否完成（1=完成，0=未完成）
     */
    private Integer isCompleted;

    /**
     * 尝试次数
     */
    private Integer attemptNumber;

    /**
     * 对话会话 ID
     */
    private String conversationId;

    /**
     * 对话总轮次
     */
    private Integer conversationRounds;

    /**
     * AI 教练详细反馈 (JSON)
     */
    private String aiFeedbackJson;

    /**
     * 各维度得分 (JSON)
     */
    private String dimensionScores;

    /**
     * 知识点推荐列表 (JSON)
     */
    private String knowledgeRecommendations;

    /**
     * 优点列表 (JSON)
     */
    private String strengths;

    /**
     * 改进建议列表 (JSON)
     */
    private String suggestions;

    /**
     * 示范对话
     */
    private String exampleDialogue;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}