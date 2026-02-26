package com.minan.game.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@TableName(value = "knowledge_quiz", autoResultMap = true)
public class KnowledgeQuiz {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String quizId;

    private String pointId;

    private String question;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<Map<String, String>> options;

    private String correctAnswer;

    private String explanation;

    private Integer difficulty;

    @TableField("`order`")
    private Integer order;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @TableField(exist = false)
    private String pointName;
}
