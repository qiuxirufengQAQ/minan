package cn.qrfeng.lianai.game.model.prompt;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

/**
 * 变量映射配置
 */
@Data
public class VariableMapping {

    /**
     * 数据源类型：dynamic/npc_character/scene/user/computed
     */
    private String source;

    /**
     * 字段名
     */
    private String field;

    /**
     * 是否必填
     */
    private Boolean required;

    /**
     * 默认值
     */
    private Object defaultValue;

    /**
     * 最大轮数（用于 conversation_history）
     */
    @JsonProperty("max_rounds")
    private Integer maxRounds;

    /**
     * 转换类型：string/int/map
     */
    private String transform;

    /**
     * 转换配置（用于 map 转换）
     */
    @JsonProperty("transform_config")
    private Map<String, String> transformConfig;

    /**
     * 计算表达式（用于 computed 类型）
     */
    @JsonProperty("compute")
    private String computeExpression;
}
