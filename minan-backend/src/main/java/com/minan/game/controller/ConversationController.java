package com.minan.game.controller;

import com.minan.game.dto.Response;
import com.minan.game.model.ConversationRecord;
import com.minan.game.service.ConversationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 对话控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/conversation")
@Api(tags = "AI NPC 对话接口")
public class ConversationController {

    @Autowired
    private ConversationService conversationService;

    /**
     * 开始对话
     */
    @PostMapping("/start")
    @ApiOperation("开始对话")
    public Response<Map<String, Object>> startConversation(
        @RequestBody Map<String, String> request
    ) {
        try {
            String userId = request.get("userId");
            String sceneId = request.get("sceneId");
            String npcId = request.get("npcId");

            if (userId == null || sceneId == null || npcId == null) {
                return Response.error("缺少必要参数");
            }

            ConversationService.ConversationStartResult result = 
                conversationService.startConversation(userId, sceneId, npcId);

            Map<String, Object> data = new HashMap<>();
            data.put("conversationId", result.getConversationId());
            data.put("currentRound", result.getCurrentRound());
            data.put("maxRounds", result.getMaxRounds());
            data.put("npcGreeting", result.getNpcGreeting());
            data.put("npcName", result.getNpcName());
            data.put("sceneName", result.getSceneName());

            return Response.success(data);

        } catch (Exception e) {
            log.error("开始对话失败", e);
            return Response.error("开始对话失败：" + e.getMessage());
        }
    }

    /**
     * 发送消息
     */
    @PostMapping("/send")
    @ApiOperation("发送消息")
    public Response<Map<String, Object>> sendMessage(
        @RequestBody Map<String, String> request
    ) {
        try {
            String conversationId = request.get("conversationId");
            String userInput = request.get("userInput");

            if (conversationId == null || userInput == null) {
                return Response.error("缺少必要参数");
            }

            ConversationService.ConversationSendResult result = 
                conversationService.sendMessage(conversationId, userInput);

            Map<String, Object> data = new HashMap<>();
            data.put("conversationId", result.getConversationId());
            data.put("currentRound", result.getCurrentRound());
            data.put("maxRounds", result.getMaxRounds());
            data.put("npcResponse", result.getNpcResponse());
            data.put("isCompleted", result.getIsCompleted());

            return Response.success(data);

        } catch (Exception e) {
            log.error("发送消息失败", e);
            return Response.error("发送消息失败：" + e.getMessage());
        }
    }

    /**
     * 获取对话历史
     */
    @GetMapping("/history/{conversationId}")
    @ApiOperation("获取对话历史")
    public Response<List<ConversationRecord>> getHistory(
        @PathVariable String conversationId
    ) {
        try {
            List<ConversationRecord> history = 
                conversationService.getConversationHistory(conversationId);
            return Response.success(history);

        } catch (Exception e) {
            log.error("获取对话历史失败", e);
            return Response.error("获取对话历史失败：" + e.getMessage());
        }
    }

    /**
     * 结束对话
     */
    @PostMapping("/end")
    @ApiOperation("结束对话")
    public Response<Void> endConversation(
        @RequestBody Map<String, String> request
    ) {
        try {
            String conversationId = request.get("conversationId");
            if (conversationId == null) {
                return Response.error("缺少必要参数");
            }

            conversationService.endConversation(conversationId);
            return Response.success(null);

        } catch (Exception e) {
            log.error("结束对话失败", e);
            return Response.error("结束对话失败：" + e.getMessage());
        }
    }
}
