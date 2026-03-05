package com.minan.game.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minan.game.model.AiConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * AI 配置 Mapper
 */
@Mapper
public interface AiConfigMapper extends BaseMapper<AiConfig> {

    /**
     * 根据 key 获取配置
     */
    @Select("SELECT * FROM ai_config WHERE config_key = #{configKey} AND is_active = 1")
    AiConfig selectByKey(String configKey);
}
