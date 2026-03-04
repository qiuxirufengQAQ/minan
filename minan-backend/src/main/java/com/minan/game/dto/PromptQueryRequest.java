package com.minan.game.dto;

import lombok.Data;

/**
 * 提示词查询请求
 */
@Data
public class PromptQueryRequest {
    /**
     * 页码
     */
    private Integer page = 1;
    
    /**
     * 每页大小
     */
    private Integer pageSize = 10;
    
    /**
     * 提示词ID（可选）
     */
    private String promptId;
    
    /**
     * 关卡ID（可选）
     */
    private String levelId;
    
    /**
     * 场景ID（可选）
     */
    private String sceneId;
    
    /**
     * 提示词类型：start（场景开始）、end（场景结束）（可选）
     */
    private String type;
}