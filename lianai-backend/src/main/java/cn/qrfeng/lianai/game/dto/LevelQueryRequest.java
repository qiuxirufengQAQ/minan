package cn.qrfeng.lianai.game.dto;

import lombok.Data;

/**
 * 关卡查询请求
 */
@Data
public class LevelQueryRequest {
    /**
     * 页码
     */
    private Integer page = 1;
    
    /**
     * 每页大小
     */
    private Integer pageSize = 10;
    
    /**
     * 关卡ID（可选）
     */
    private String levelId;
}
