package com.minan.game.service;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.minan.game.mapper.ConversationRecordMapper;
import com.minan.game.mapper.NpcCharacterMapper;
import com.minan.game.mapper.SceneMapper;
import com.minan.game.model.ConversationRecord;
import com.minan.game.model.NpcCharacter;
import com.minan.game.model.Scene;
import com.minan.game.utils.AesEncryptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 对话服务
 */
@Slf4j
@Service
public class ConversationService {

    @Autowired
    private ConversationRecordMapper conversationRecordMapper;

    @Autowired
    private NpcCharacterMapper npcCharacterMapper;

    @Autowired
    private SceneMapper sceneMapper;

    @Autowired
    private AiNpcService aiNpcService;

    @Autowired
    private AiConfigService aiConfigService;

    @Autowired
    private AesEncryptor aesEncryptor;

    // 内存中的对话上下文（生产环境建议用 Redis）
    private final Map<String, ConversationContext> conversationContexts = new HashMap<>();

    /**
     * 开始对话
     */
    @Transactional
    public ConversationStartResult startConversation(Long userId, String sceneId) {
        try {
            // 1. 加载场景信息 - 使用 scene_id 字段查询
            LambdaQueryWrapper<Scene> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Scene::getSceneId, sceneId);
            Scene scene = sceneMapper.selectOne(queryWrapper);
            
            if (scene == null) {
                throw new IllegalArgumentException("场景不存在：" + sceneId);
            }
            
            // 从 resourceIds 解析第一个 NPC ID（格式：NPC_001,NPC_002,...）
            String npcId = null;
            if (scene.getResourceIds() != null && !scene.getResourceIds().isEmpty()) {
                String[] resourceArray = scene.getResourceIds().split(",");
                for (String resource : resourceArray) {
                    if (resource.trim().startsWith("NPC_")) {
                        npcId = resource.trim();
                        break;
                    }
                }
            }
            
            // 如果没有配置 NPC，使用默认 NPC
            if (npcId == null) {
                npcId = "NPC_DEFAULT";
            }
            
            NpcCharacter npc = npcCharacterMapper.selectByNpcId(npcId);
            if (npc == null) {
                throw new IllegalArgumentException("NPC 不存在：" + npcId);
            }

            // 2. 生成对话 ID
            String conversationId = IdUtil.fastSimpleUUID();

            // 3. 获取最大轮次
            int maxRounds = scene.getMaxConversationRounds() != null 
                ? scene.getMaxConversationRounds() 
                : aiConfigService.getMaxRoundsDefault();

            // 4. 保存上下文
            ConversationContext context = new ConversationContext();
            context.setConversationId(conversationId);
            context.setSceneId(sceneId);
            context.setNpcId(npcId);
            context.setUserId(userId);
            context.setMaxRounds(maxRounds);
            context.setCurrentRound(0);
            conversationContexts.put(conversationId, context);

            // 5. 返回结果
            ConversationStartResult result = new ConversationStartResult();
            result.setConversationId(conversationId);
            result.setMaxRounds(maxRounds);
            result.setCurrentRound(0);
            result.setNpcGreeting(npc.getPersonality()); // 临时使用 personality 作为问候语
            result.setNpcName(npc.getName());
            result.setSceneName(scene.getName());

            return result;

        } catch (IllegalArgumentException e) {
            log.error("开始对话失败：{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("开始对话异常", e);
            throw new RuntimeException("开始对话失败", e);
        }
    }

    /**
     * 发送消息
     */
    @Transactional
    public ConversationSendResult sendMessage(String conversationId, String userInput) {
        try {
            ConversationContext context = conversationContexts.get(conversationId);
            if (context == null) {
                throw new IllegalArgumentException("对话不存在：" + conversationId);
            }

            // 检查是否已结束
            if (context.getCurrentRound() >= context.getMaxRounds()) {
                throw new IllegalArgumentException("对话已结束");
            }

            // 增加轮次
            context.setCurrentRound(context.getCurrentRound() + 1);
            int currentRound = context.getCurrentRound();

            // 调用 AI NPC 服务生成回复（临时实现）
            String npcResponse = "这是 AI NPC 的回复（第" + currentRound + "轮）";

            // 保存对话记录
            ConversationRecord record = new ConversationRecord();
            record.setConversationId(conversationId);
            record.setRoundNumber(currentRound);
            record.setUserId(context.getUserId().toString());
            record.setUserInput(userInput);
            record.setNpcResponse(npcResponse);
            record.setCreatedAt(LocalDateTime.now());
            conversationRecordMapper.insert(record);

            // 检查是否结束
            boolean isCompleted = currentRound >= context.getMaxRounds();

            // 返回结果
            ConversationSendResult result = new ConversationSendResult();
            result.setConversationId(conversationId);
            result.setCurrentRound(currentRound);
            result.setMaxRounds(context.getMaxRounds());
            result.setNpcResponse(npcResponse);
            result.setIsCompleted(isCompleted);

            return result;

        } catch (Exception e) {
            log.error("发送消息失败", e);
            throw new RuntimeException("发送消息失败", e);
        }
    }

    /**
     * 检查用户是否是对话所有者
     */
    public boolean isOwner(String conversationId, Long userId) {
        ConversationContext context = conversationContexts.get(conversationId);
        if (context == null) {
            // 尝试从数据库查询
            LambdaQueryWrapper<ConversationRecord> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ConversationRecord::getConversationId, conversationId);
            List<ConversationRecord> records = conversationRecordMapper.selectList(queryWrapper);
            
            if (records.isEmpty()) {
                return false;
            }
            
            // 检查第一条记录的用户 ID
            String recordUserId = records.get(0).getUserId();
            return recordUserId.equals(userId.toString());
        }
        
        return context.getUserId().equals(userId);
    }

    /**
     * 获取对话历史
     */
    public List<ConversationRecord> getConversationHistory(String conversationId) {
        LambdaQueryWrapper<ConversationRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ConversationRecord::getConversationId, conversationId);
        queryWrapper.orderByAsc(ConversationRecord::getRoundNumber);
        return conversationRecordMapper.selectList(queryWrapper);
    }

    /**
     * 结束对话
     */
    public void endConversation(String conversationId) {
        conversationContexts.remove(conversationId);
        log.info("对话结束：{}", conversationId);
    }

    /**
     * 对话上下文
     */
    public static class ConversationContext {
        private String conversationId;
        private String sceneId;
        private String npcId;
        private Long userId;
        private int maxRounds;
        private int currentRound;

        // Getters and Setters
        public String getConversationId() { return conversationId; }
        public void setConversationId(String conversationId) { this.conversationId = conversationId; }
        public String getSceneId() { return sceneId; }
        public void setSceneId(String sceneId) { this.sceneId = sceneId; }
        public String getNpcId() { return npcId; }
        public void setNpcId(String npcId) { this.npcId = npcId; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public int getMaxRounds() { return maxRounds; }
        public void setMaxRounds(int maxRounds) { this.maxRounds = maxRounds; }
        public int getCurrentRound() { return currentRound; }
        public void setCurrentRound(int currentRound) { this.currentRound = currentRound; }
    }

    /**
     * 开始对话结果
     */
    public static class ConversationStartResult {
        private String conversationId;
        private int maxRounds;
        private int currentRound;
        private String npcGreeting;
        private String npcName;
        private String sceneName;

        // Getters and Setters
        public String getConversationId() { return conversationId; }
        public void setConversationId(String conversationId) { this.conversationId = conversationId; }
        public int getMaxRounds() { return maxRounds; }
        public void setMaxRounds(int maxRounds) { this.maxRounds = maxRounds; }
        public int getCurrentRound() { return currentRound; }
        public void setCurrentRound(int currentRound) { this.currentRound = currentRound; }
        public String getNpcGreeting() { return npcGreeting; }
        public void setNpcGreeting(String npcGreeting) { this.npcGreeting = npcGreeting; }
        public String getNpcName() { return npcName; }
        public void setNpcName(String npcName) { this.npcName = npcName; }
        public String getSceneName() { return sceneName; }
        public void setSceneName(String sceneName) { this.sceneName = sceneName; }
    }

    /**
     * 发送消息结果
     */
    public static class ConversationSendResult {
        private String conversationId;
        private int currentRound;
        private int maxRounds;
        private String npcResponse;
        private Boolean isCompleted;

        // Getters and Setters
        public String getConversationId() { return conversationId; }
        public void setConversationId(String conversationId) { this.conversationId = conversationId; }
        public int getCurrentRound() { return currentRound; }
        public void setCurrentRound(int currentRound) { this.currentRound = currentRound; }
        public int getMaxRounds() { return maxRounds; }
        public void setMaxRounds(int maxRounds) { this.maxRounds = maxRounds; }
        public String getNpcResponse() { return npcResponse; }
        public void setNpcResponse(String npcResponse) { this.npcResponse = npcResponse; }
        public Boolean getIsCompleted() { return isCompleted; }
        public void setIsCompleted(Boolean isCompleted) { this.isCompleted = isCompleted; }
    }
}
