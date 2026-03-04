package com.minan.game.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("prompt")
public class Prompt {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 提示词唯一标识
     */
    private String promptId;

    /**
     * 关卡ID
     */
    private String levelId;

    /**
     * 场景ID
     */
    private String sceneId;
    
    /**
     * 开始提示词模板
     */
    private String startTemplate;

    /**
     * 结束提示词模板
     */
    private String endTemplate;

    /**
     * 评估维度（JSON格式）
     */
    private String evaluationDimensions;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}