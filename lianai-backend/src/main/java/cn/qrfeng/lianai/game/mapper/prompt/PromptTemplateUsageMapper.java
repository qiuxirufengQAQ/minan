package cn.qrfeng.lianai.game.mapper.prompt;

import cn.qrfeng.lianai.game.model.prompt.TemplateUsageLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 模板使用日志 Mapper
 */
@Mapper
public interface PromptTemplateUsageMapper {

    /**
     * 插入使用日志
     */
    int insert(TemplateUsageLog log);

    /**
     * 插入使用日志（Map 方式）
     */
    int insertRaw(Map<String, Object> log);

    /**
     * 查询使用统计
     */
    Map<String, Object> selectUsageStats(
        @Param("templateId") Long templateId,
        @Param("days") Integer days
    );

    /**
     * 查询最近 7 天使用情况
     */
    List<Map<String, Object>> selectLast7Days();

    /**
     * 删除旧日志
     */
    int deleteBefore(@Param("before") LocalDateTime before);
}
