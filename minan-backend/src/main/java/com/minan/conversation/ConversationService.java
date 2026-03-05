package com.minan.conversation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.minan.ai.AiNpcService;
import com.minan.mapper.ConversationMapper;
import java.util.UUID;

/**
 * 对话业务服务
 * 路径：src/main/java/com/minan/conversation/ConversationService.java
 * 设计说明：仅框架设计，无实际业务逻辑
 */
@Service
public class ConversationService {

    @Autowired
    private AiNpcService aiNpcService;
    
    @Autowired
    private ConversationMapper conversationMapper;

    /**
     * 开始对话
     * @return 返回 conversation_id 占位符
     */
    public String startConversation(StartRequest request) {
        // 1. 验证场景是否启用 AI NPC
        // 2. 生成 conversation_id
        // 3. 初始化对话上下文（Redis）
        return "conv_" + UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * 发送消息
     * @return 返回占位响应
     */
    public SendResponse sendMessage(SendRequest request) {
        // 1. 检查轮次上限
        // 2. 调用 aiNpcService 生成回复（框架留空）
        // 3. 保存对话记录（conversationMapper）
        SendResponse response = new SendResponse();
        response.setCurrentRound(1);
        response.setNpcResponse("NPC 回复占位符");
        response.setEmotionTag("开心");
        return response;
    }

    /**
     * 结束对话
     * @return 返回对话 ID 供评估使用
     */
    public EndResponse endConversation(EndRequest request) {
        EndResponse response = new EndResponse();
        response.setConversationId(request.getConversationId());
        response.setCompleted(true);
        return response;
    }
}
