package com.minan.game.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_learning_log")
public class UserLearningLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String logId;

    private String userId;

    private String sceneId;

    private String actionType;

    private String actionDetail;

    private Integer durationSeconds;

    private LocalDateTime createdAt;
}
