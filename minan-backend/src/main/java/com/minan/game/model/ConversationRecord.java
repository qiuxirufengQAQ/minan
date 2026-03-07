package com.minan.game.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 对话记录实体类
 */
@Data
@TableName("conversation_record")
public class ConversationRecord {

    /**
     * 主键 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 对话记录唯一 ID
     */
    private String recordId;

    /**
     * 对话会话 ID（一次完整对话）
     */
    private String conversationId;

    /**
     * 场景 ID
     */
    private String sceneId;

    /**
     * 用户 ID
     */
    private String userId;

    /**
     * NPC ID
     */
    private String npcId;

    /**
     * 对话轮次 (1,2,3...)
     */
    private Integer roundNumber;

    /**
     * 用户输入
     */
    private String userInput;

    /**
     * NPC 回复
     */
    private String npcResponse;

    /**
     * 使用的 AI 模型
     */
    private String aiModel;

    /**
     * 消耗 tokens 数
     */
    private Integer tokensUsed;

    /**
     * 情绪标签 (开心/尴尬/生气等)
     */
    private String emotionTag;

    /**
     * 是否加密存储 (1=加密，0=未加密)
     */
    private Integer isEncrypted;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
