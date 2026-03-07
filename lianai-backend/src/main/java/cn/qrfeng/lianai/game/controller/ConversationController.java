package cn.qrfeng.lianai.game.controller;

import cn.qrfeng.lianai.game.dto.Response;
import cn.qrfeng.lianai.game.dto.SendMessageRequest;
import cn.qrfeng.lianai.game.dto.StartConversationRequest;
import cn.qrfeng.lianai.game.model.ConversationRecord;
import cn.qrfeng.lianai.game.service.ConversationService;
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
@RequestMapping("/conversation")
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
        @RequestHeader(value = "Authorization", required = false) String authorization,
        @RequestBody @Validated StartConversationRequest request
    ) {
        try {
            // 从 Authorization header 提取 token
            String token = null;
            if (authorization != null && authorization.startsWith("Bearer ")) {
                token = authorization.substring(7);
            }
            
            // 手动验证 token
            if (token == null || token.isEmpty()) {
                return Response.error("缺少 token，请先登录");
            }
            
            // 通过 token 获取登录用户
            Object loginId = StpUtil.getLoginIdByToken(token);
            if (loginId == null) {
                log.warn("无效的 token: {}", token.substring(0, Math.min(20, token.length())));
                return Response.error("token 无效或已过期");
            }
            
            Long currentUserId = Long.parseLong(loginId.toString());
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
    @PostMapping("/send/{conversationId}")
    @ApiOperation("发送消息")
    public Response<Map<String, Object>> sendMessage(
        @RequestHeader(value = "Authorization", required = false) String authorization,
        @PathVariable String conversationId,
        @RequestBody @Validated SendMessageRequest request
    ) {
        try {
            // 从 Authorization header 提取 token
            String token = null;
            if (authorization != null && authorization.startsWith("Bearer ")) {
                token = authorization.substring(7);
            }
            
            if (token == null) {
                return Response.error("缺少 token");
            }
            
            Object loginIdObj = StpUtil.getLoginIdByToken(token);
            if (loginIdObj == null) {
                return Response.error("token 无效");
            }
            
            Long currentUserId = Long.parseLong(loginIdObj.toString());
            
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
        @RequestHeader(value = "Authorization", required = false) String authorization,
        @PathVariable String conversationId
    ) {
        try {
            // 从 Authorization header 提取 token
            String token = null;
            if (authorization != null && authorization.startsWith("Bearer ")) {
                token = authorization.substring(7);
            }
            
            if (token == null) {
                return Response.error("缺少 token");
            }
            
            Object loginIdObj = StpUtil.getLoginIdByToken(token);
            if (loginIdObj == null) {
                return Response.error("token 无效");
            }
            
            Long currentUserId = Long.parseLong(loginIdObj.toString());
            
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
        @RequestHeader(value = "Authorization", required = false) String authorization,
        @PathVariable String conversationId
    ) {
        try {
            // 从 Authorization header 提取 token
            String token = null;
            if (authorization != null && authorization.startsWith("Bearer ")) {
                token = authorization.substring(7);
            }
            
            if (token == null) {
                return Response.error("缺少 token");
            }
            
            Object loginIdObj = StpUtil.getLoginIdByToken(token);
            if (loginIdObj == null) {
                return Response.error("token 无效");
            }
            
            Long currentUserId = Long.parseLong(loginIdObj.toString());
            
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
