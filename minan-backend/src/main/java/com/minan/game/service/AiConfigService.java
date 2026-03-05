package com.minan.game.service;

import com.minan.game.mapper.AiConfigMapper;
import com.minan.game.model.AiConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * AI 配置服务
 */
@Slf4j
@Service
public class AiConfigService {

    @Autowired
    private AiConfigMapper aiConfigMapper;

    /**
     * 获取配置值
     */
    public String getConfigValue(String key) {
        AiConfig config = aiConfigMapper.selectByKey(key);
        if (config == null) {
            log.warn("AI 配置未找到：{}", key);
            return null;
        }
        return config.getConfigValue();
    }

    /**
     * 获取配置值（带默认值）
     */
    public String getConfigValue(String key, String defaultValue) {
        String value = getConfigValue(key);
        return value != null ? value : defaultValue;
    }

    /**
     * 获取 API 密钥
     */
    public String getApiKey() {
        return getConfigValue("qwen_api_key");
    }

    /**
     * 获取 NPC 模型名称
     */
    public String getNpcModel() {
        return getConfigValue("npc_model", "qwen-plus");
    }

    /**
     * 获取 NPC 最大 tokens
     */
    public int getNpcMaxTokens() {
        String value = getConfigValue("npc_max_tokens", "500");
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            log.error("NPC max tokens 配置格式错误：{}", value);
            return 500;
        }
    }

    /**
     * 获取 NPC 温度参数
     */
    public double getNpcTemperature() {
        String value = getConfigValue("npc_temperature", "0.8");
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            log.error("NPC temperature 配置格式错误：{}", value);
            return 0.8;
        }
    }

    /**
     * 获取教练模型名称
     */
    public String getCoachModel() {
        return getConfigValue("coach_model", "qwen-plus");
    }

    /**
     * 获取教练最大 tokens
     */
    public int getCoachMaxTokens() {
        String value = getConfigValue("coach_max_tokens", "1000");
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            log.error("Coach max tokens 配置格式错误：{}", value);
            return 1000;
        }
    }

    /**
     * 获取教练温度参数
     */
    public double getCoachTemperature() {
        String value = getConfigValue("coach_temperature", "0.5");
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            log.error("Coach temperature 配置格式错误：{}", value);
            return 0.5;
        }
    }

    /**
     * 获取默认最大对话轮次
     */
    public int getMaxRoundsDefault() {
        String value = getConfigValue("max_rounds_default", "5");
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            log.error("Max rounds default 配置格式错误：{}", value);
            return 5;
        }
    }

    /**
     * 获取免费用户每日 AI 调用限制
     */
    public int getDailyAiLimitFree() {
        String value = getConfigValue("daily_ai_limit_free", "10");
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            log.error("Daily AI limit free 配置格式错误：{}", value);
            return 10;
        }
    }

    /**
     * 获取 VIP 用户每日 AI 调用限制
     */
    public int getDailyAiLimitVip() {
        String value = getConfigValue("daily_ai_limit_vip", "999");
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            log.error("Daily AI limit VIP 配置格式错误：{}", value);
            return 999;
        }
    }
}
