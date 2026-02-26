package com.minan.game.dto;

import lombok.Data;

/**
 * 场景查询请求
 */
@Data
public class SceneQueryRequest {
    /**
     * 页码
     */
    private Integer page = 1;
    
    /**
     * 每页大小
     */
    private Integer pageSize = 10;
    
    /**
     * 场景ID（可选）
     */
    private String sceneId;
    
    /**
     * 关卡ID（可选）
     */
    private String levelId;
}