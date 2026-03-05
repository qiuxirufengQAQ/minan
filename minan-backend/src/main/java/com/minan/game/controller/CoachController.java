package com.minan.game.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.minan.game.dto.Response;
import com.minan.game.mapper.ConversationRecordMapper;
import com.minan.game.mapper.EvaluationMapper;
import com.minan.game.mapper.SceneMapper;
import com.minan.game.model.ConversationRecord;
import com.minan.game.model.Evaluation;
import com.minan.game.model.Scene;
import com.minan.game.service.AiCoachService;
import com.minan.game.service.AiCoachService.EvaluationResult;
import com.minan.game.service.ConversationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI 教练控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/coach")
@Api(tags = "AI 教练评估接口")
public class CoachController {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private AiCoachService aiCoachService;

    @Autowired
    private ConversationRecordMapper conversationRecordMapper;

    @Autowired
    private EvaluationMapper evaluationMapper;

    @Autowired
    private SceneMapper sceneMapper;

    @Autowired
    private ConversationService conversationService;

    /**
     * 评估对话
     */
    @PostMapping("/evaluate")
    @ApiOperation("评估对话")
    public Response<Map<String, Object>> evaluate(
        @RequestBody Map<String, String> request
    ) {
        try {
            String conversationId = request.get("conversationId");
            
            if (conversationId == null) {
                return Response.error("缺少会话 ID");
            }

            // 使用当前登录用户
            Long currentUserId = StpUtil.getLoginIdAsLong();

            // 权限校验：检查是否是对话所有者
            if (!conversationService.isOwner(conversationId, currentUserId)) {
                return Response.error("无权评估此对话");
            }

            // 1. 获取对话记录
            List<ConversationRecord> records = 
                conversationRecordMapper.selectByConversationId(conversationId);

            if (records == null || records.isEmpty()) {
                return Response.error("对话记录为空");
            }

            // 2. 获取场景信息
            String sceneId = records.get(0).getSceneId();
            Scene scene = sceneMapper.selectById(sceneId);

            // 3. AI 教练评估
            EvaluationResult result = aiCoachService.evaluateConversation(records, scene);

            // 4. 保存评估结果
            String npcId = records.get(0).getNpcId();
            Evaluation evaluation = saveEvaluationResult(
                String.valueOf(currentUserId), sceneId, npcId, conversationId, records.size(), result
            );

            // 5. 返回结果
            Map<String, Object> data = new HashMap<>();
            data.put("evaluationId", evaluation.getEvaluationId());
            data.put("totalScore", result.getTotalScore());
            data.put("dimensionScores", result.getDimensionScores());
            data.put("strengths", result.getStrengths());
            data.put("suggestions", result.getSuggestions());
            data.put("exampleDialogue", result.getExampleDialogue());
            data.put("knowledgeRecommendations", result.getKnowledgeRecommendations());
            data.put("conversationRounds", records.size());

            return Response.success(data);

        } catch (Exception e) {
            log.error("评估对话失败", e);
            return Response.error("评估失败：" + e.getMessage());
        }
    }

    /**
     * 获取评估结果
     */
    @GetMapping("/result/{evaluationId}")
    @ApiOperation("获取评估结果")
    public Response<Map<String, Object>> getResult(
        @PathVariable String evaluationId
    ) {
        try {
            Evaluation evaluation = evaluationMapper.selectByEvaluationId(evaluationId);

            if (evaluation == null) {
                return Response.error("评估结果不存在");
            }

            Map<String, Object> data = new HashMap<>();
            data.put("evaluationId", evaluation.getEvaluationId());
            data.put("totalScore", evaluation.getScore());
            data.put("conversationRounds", evaluation.getConversationRounds());
            data.put("createdAt", evaluation.getCreatedAt());

            // 解析 JSON 字段
            if (evaluation.getDimensionScores() != null) {
                data.put("dimensionScores", 
                    objectMapper.readTree(evaluation.getDimensionScores()));
            }
            if (evaluation.getStrengths() != null) {
                data.put("strengths", 
                    objectMapper.readTree(evaluation.getStrengths()));
            }
            if (evaluation.getSuggestions() != null) {
                data.put("suggestions", 
                    objectMapper.readTree(evaluation.getSuggestions()));
            }
            if (evaluation.getExampleDialogue() != null) {
                data.put("exampleDialogue", evaluation.getExampleDialogue());
            }
            if (evaluation.getKnowledgeRecommendations() != null) {
                data.put("knowledgeRecommendations", 
                    objectMapper.readTree(evaluation.getKnowledgeRecommendations()));
            }

            return Response.success(data);

        } catch (Exception e) {
            log.error("获取评估结果失败", e);
            return Response.error("获取评估结果失败：" + e.getMessage());
        }
    }

    /**
     * 保存评估结果
     */
    private Evaluation saveEvaluationResult(
        String userId,
        String sceneId,
        String npcId,
        String conversationId,
        int conversationRounds,
        EvaluationResult result
    ) {
        try {
            Evaluation evaluation = new Evaluation();
            evaluation.setEvaluationId(IdUtil.fastSimpleUUID());
            evaluation.setUserId(userId);
            evaluation.setSceneId(sceneId);
            evaluation.setConversationId(conversationId);
            evaluation.setScore(result.getTotalScore());
            evaluation.setConversationRounds(conversationRounds);
            evaluation.setIsCompleted(1);
            evaluation.setAttemptNumber(1);
            evaluation.setCpGained((int) (result.getTotalScore() / 10)); // 按分数给予 CP
            evaluation.setCreatedAt(LocalDateTime.now());

            // 保存 JSON 数据
            try {
                evaluation.setDimensionScores(objectMapper.writeValueAsString(result.getDimensionScores()));
                evaluation.setStrengths(objectMapper.writeValueAsString(result.getStrengths()));
                evaluation.setSuggestions(objectMapper.writeValueAsString(result.getSuggestions()));
                evaluation.setKnowledgeRecommendations(objectMapper.writeValueAsString(result.getKnowledgeRecommendations()));
            } catch (Exception e) {
                log.error("保存 JSON 数据失败", e);
            }

            evaluation.setExampleDialogue(result.getExampleDialogue());

            evaluationMapper.insert(evaluation);
            log.info("评估结果已保存：evaluationId={}, score={}", 
                evaluation.getEvaluationId(), evaluation.getScore());

            return evaluation;

        } catch (Exception e) {
            log.error("保存评估结果失败", e);
            throw new RuntimeException("保存评估结果失败");
        }
    }
}
