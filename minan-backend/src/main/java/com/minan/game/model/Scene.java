package com.minan.game.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("scene")
public class Scene {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String sceneId;

    private String levelId;

    private String name;

    @TableField("`order`")
    private Integer order;

    private String background;

    private String imageUrl;

    private String technique;

    private String coreConcept;

    private String dialogueExample;

    private String hintIds;

    private Integer difficulty;

    private Integer requiredIntimacyScore;

    private Integer estimatedTime;

    private String videoUrl;

    private String resourceIds;

    private Double maxScore;

    private Integer isActive;

    @TableField("reference_options")
    private String referenceOptions;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}