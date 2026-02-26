package com.minan.game.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("user_daily_task")
public class UserDailyTask {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String userTaskId;

    private String userId;

    private String taskId;

    private Integer currentCount;

    private Integer isCompleted;

    private Integer isClaimed;

    private LocalDate taskDate;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
