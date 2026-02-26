package com.minan.game.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName(value = "knowledge_point", autoResultMap = true)
public class KnowledgePoint {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String pointId;

    private String categoryId;

    private String name;

    private String description;

    private String coreConcept;

    private Integer difficulty;

    private Integer importance;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> prerequisites;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> tags;

    private String iconUrl;

    @TableField("`order`")
    private Integer order;

    private Integer isActive;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @TableField(exist = false)
    private String categoryName;
}
