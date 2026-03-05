package com.minan.conversation;

import com.minan.common.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * AI NPC 对话控制器
 * 路径：src/main/java/com/minan/conversation/ConversationController.java
 * 设计说明：仅框架设计，无实际业务逻辑
 */
@RestController
@RequestMapping("/api/conversation")
public class ConversationController {

    @Autowired
    private ConversationService conversationService;

    /**
     * 开始新对话（初始化 NPC）
     * @apiNote 仅返回会话 ID，不处理实际 AI 调用
     */
    @PostMapping("/start")
    public Response<String> startConversation(@RequestBody StartRequest request) {
        // 框架占位：实际逻辑由 ConversationService 实现
        return Response.success(conversationService.startConversation(request));
    }

    /**
     * 发送消息（获取 NPC 回复）
     * @apiNote 返回基础回复结构，AI 调用留空
     */
    @PostMapping("/send")
    public Response<SendResponse> sendMessage(@RequestBody SendRequest request) {
        return Response.success(conversationService.sendMessage(request));
    }

    /**
     * 结束对话（触发评估）
     * @apiNote 返回对话 ID 供任务集 B 使用
     */
    @PostMapping("/end")
    public Response<EndResponse> endConversation(@RequestBody EndRequest request) {
        return Response.success(conversationService.endConversation(request));
    }
}
