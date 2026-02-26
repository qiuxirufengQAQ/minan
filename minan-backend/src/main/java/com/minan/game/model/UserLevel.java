package com.minan.game.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_level")
public class UserLevel {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户关卡唯一标识
     */
    private String userLevelId;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 关卡ID
     */
    private String levelId;

    /**
     * 已完成场景数
     */
    private Integer completedScenesCount;

    /**
     * 总场景数
     */
    private Integer totalScenesCount;

    /**
     * 完成进度
     */
    private Integer progress;

    /**
     * 是否完成（1=完成，0=未完成）
     */
    private Integer isCompleted;

    /**
     * 最后更新时间
     */
    private LocalDateTime lastUpdatedAt;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}