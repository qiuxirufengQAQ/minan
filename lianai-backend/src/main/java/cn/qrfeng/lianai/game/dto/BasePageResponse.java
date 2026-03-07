package cn.qrfeng.lianai.game.dto;

import lombok.Data;

import java.util.List;

/**
 * 基础分页响应
 * @param <T> 数据类型
 */
@Data
public class BasePageResponse<T> {
    /**
     * 总记录数
     */
    private Long total;
    
    /**
     * 总页数
     */
    private Long pages;
    
    /**
     * 当前页码
     */
    private Long current;
    
    /**
     * 每页大小
     */
    private Integer size;
    
    /**
     * 数据列表
     */
    private List<T> records;
}
