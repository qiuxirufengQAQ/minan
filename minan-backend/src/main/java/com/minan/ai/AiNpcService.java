package com.minan.ai;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * AI NPC 服务框架
 * 路径：src/main/java/com/minan/ai/AiNpcService.java
 * 设计说明：仅框架设计，AI 调用留空
 */
@Service
public class AiNpcService {

    @Autowired
    private AiConfigService aiConfigService;

    /**
     * 生成 NPC 回复（框架留空）
     * @param context 对话上下文
     * @return 仅返回占位结构
     */
    public NpcResponse generateResponse(ConversationContext context) {
        // 框架说明：
        // 1. 通过 aiConfigService 获取模型配置
        // 2. 构建 Prompt（scene.ai_npc_prompt_template + 历史）
        // 3. 调用 Qwen API（实际实现留空）
        return NpcResponse.builder()
                .responseText("AI 回复占位符")
                .tokensUsed(0)
                .emotionTag("开心")
                .build();
    }
}
