package com.minan.game.dto;

import lombok.Data;

/**
 * 成就查询请求
 */
@Data
public class AchievementQueryRequest {
    /**
     * 页码
     */
    private Integer page = 1;
    
    /**
     * 每页大小
     */
    private Integer pageSize = 10;
    
    /**
     * 成就ID（可选）
     */
    private String achievementId;
}