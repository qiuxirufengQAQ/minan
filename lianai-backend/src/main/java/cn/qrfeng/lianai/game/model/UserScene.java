package cn.qrfeng.lianai.game.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_scene")
public class UserScene {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户场景唯一标识
     */
    private String userSceneId;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 场景ID
     */
    private String sceneId;

    /**
     * 用户选择的NPC
     */
    private String npcId;

    /**
     * 是否完成（1=完成，0=未完成）
     */
    private Integer isCompleted;

    /**
     * 尝试次数
     */
    private Integer attemptCount;

    /**
     * 最高得分
     */
    private Double highestScore;

    /**
     * 最后尝试时间
     */
    private LocalDateTime lastAttemptAt;

    /**
     * 完成时间
     */
    private LocalDateTime completedAt;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}