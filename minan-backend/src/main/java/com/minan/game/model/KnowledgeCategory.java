package com.minan.game.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("knowledge_category")
public class KnowledgeCategory {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String categoryId;

    private String name;

    private String parentId;

    private Integer level;

    private String description;

    private String theoryBasis;

    private String learningGoal;

    @TableField("`order`")
    private Integer order;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @TableField(exist = false)
    private java.util.List<KnowledgeCategory> children;
}
