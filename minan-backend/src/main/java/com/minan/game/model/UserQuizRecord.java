package com.minan.game.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_quiz_record")
public class UserQuizRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String recordId;

    private String userId;

    private String quizId;

    private String userAnswer;

    private Integer isCorrect;

    private Integer attemptCount;

    private LocalDateTime createdAt;
}
