package com.minan.game.controller;

import com.minan.game.dto.Response;
import com.minan.game.dto.SendMessageRequest;
import com.minan.game.dto.StartConversationRequest;
import com.minan.game.model.ConversationRecord;
import com.minan.game.service.ConversationService;
import cn.dev33.satoken.stp.StpUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
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
        @RequestBody @Validated StartConversationRequest request
    ) {
        try {
            // 使用当前登录用户 ID，而非前端传入
            Long currentUserId = StpUtil.getLoginIdAsLong();
            String sceneId = request.getSceneId();
            
            log.info("用户 {} 开始场景 {} 的对话", currentUserId, sceneId);

            ConversationService.ConversationStartResult result = 
                conversationService.startConversation(currentUserId, sceneId);

            Map<String, Object> data = new HashMap<>();
            data.put("conversationId", result.getConversationId());
            data.put("currentRound", result.getCurrentRound());
            data.put("maxRounds", result.getMaxRounds());
            data.put("npcGreeting", result.getNpcGreeting());
            data.put("npcName", result.getNpcName());
            data.put("sceneName", result.getSceneName());

            return Response.success(data);

        } catch (IllegalArgumentException e) {
            log.warn("参数错误：{}", e.getMessage());
            return Response.error("参数错误：" + e.getMessage());
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
        @PathVariable String conversationId,
        @RequestBody @Validated SendMessageRequest request
    ) {
        try {
            // 验证当前用户是否有权操作此会话
            Long currentUserId = StpUtil.getLoginIdAsLong();
            
            // 检查会话归属
            if (!conversationService.isOwner(conversationId, currentUserId)) {
                return Response.error("无权操作此对话");
            }

            log.info("用户 {} 在会话 {} 发送消息", currentUserId, conversationId);

            ConversationService.ConversationSendResult result = 
                conversationService.sendMessage(conversationId, request.getContent());

            Map<String, Object> data = new HashMap<>();
            data.put("conversationId", result.getConversationId());
            data.put("currentRound", result.getCurrentRound());
            data.put("maxRounds", result.getMaxRounds());
            data.put("npcResponse", result.getNpcResponse());
            data.put("isCompleted", result.getIsCompleted());

            return Response.success(data);

        } catch (IllegalArgumentException e) {
            log.warn("参数错误：{}", e.getMessage());
            return Response.error("参数错误：" + e.getMessage());
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
            Long currentUserId = StpUtil.getLoginIdAsLong();
            
            // 检查会话归属
            if (!conversationService.isOwner(conversationId, currentUserId)) {
                return Response.error("无权查看此对话");
            }

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
    @PostMapping("/end/{conversationId}")
    @ApiOperation("结束对话")
    public Response<Void> endConversation(
        @PathVariable String conversationId
    ) {
        try {
            Long currentUserId = StpUtil.getLoginIdAsLong();
            
            // 检查会话归属
            if (!conversationService.isOwner(conversationId, currentUserId)) {
                return Response.error("无权操作此对话");
            }

            log.info("用户 {} 结束会话 {}", currentUserId, conversationId);
            
            conversationService.endConversation(conversationId);
            return Response.success(null);

        } catch (Exception e) {
            log.error("结束对话失败", e);
            return Response.error("结束对话失败：" + e.getMessage());
        }
    }
}
