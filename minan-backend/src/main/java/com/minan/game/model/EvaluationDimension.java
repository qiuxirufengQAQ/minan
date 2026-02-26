package com.minan.game.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("evaluation_dimension")
public class EvaluationDimension {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String dimensionId;

    private String sceneId;

    private String name;

    private String description;

    private BigDecimal maxScore;

    private BigDecimal weight;

    @TableField("`order`")
    private Integer order;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
