package com.minan.game.service;

import cn.hutool.core.util.IdUtil;
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
            // 1. 加载场景信息
            Scene scene = sceneMapper.selectById(sceneId);
            if (scene == null) {
                throw new IllegalArgumentException("场景不存在");
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
            
            // 如果没有配置 NPC，使用默认 NPC（需要数据库中有 NPC_DEFAULT 记录）
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

            // 4. 保存对话上下文
            ConversationContext context = new ConversationContext();
            context.setConversationId(conversationId);
            context.setUserId(String.valueOf(userId));
            context.setSceneId(sceneId);
            context.setNpcId(npcId);
            context.setCurrentRound(0);
            context.setMaxRounds(maxRounds);
            context.setIsCompleted(false);
            conversationContexts.put(conversationId, context);

            // 5. 生成 NPC 欢迎语
            String greeting = generateGreeting(npc, scene);

            // 6. 保存第一条记录（NPC 欢迎语）- NPC 回复需要加密
            ConversationRecord greetingRecord = new ConversationRecord();
            greetingRecord.setRecordId(IdUtil.fastSimpleUUID());
            greetingRecord.setConversationId(conversationId);
            greetingRecord.setSceneId(sceneId);
            greetingRecord.setUserId(String.valueOf(userId));
            greetingRecord.setNpcId(npcId);
            greetingRecord.setRoundNumber(0);
            greetingRecord.setUserInput(""); // 空字符串不需要加密
            greetingRecord.setNpcResponse(aesEncryptor.encrypt(greeting)); // 加密 NPC 回复
            greetingRecord.setCreatedAt(LocalDateTime.now());
            conversationRecordMapper.insert(greetingRecord);

            log.info("对话开始：conversationId={}, userId={}, sceneId={}, npcId={}", 
                conversationId, userId, sceneId, npcId);

            // 7. 返回结果
            ConversationStartResult result = new ConversationStartResult();
            result.setConversationId(conversationId);
            result.setCurrentRound(0);
            result.setMaxRounds(maxRounds);
            result.setNpcGreeting(greeting);
            result.setNpcName(npc.getName());
            result.setSceneName(scene.getName());
            return result;

        } catch (IllegalArgumentException e) {
            throw e; // 保留参数错误
        } catch (Exception e) {
            log.error("开始对话失败", e);
            throw new RuntimeException("开始对话失败：" + e.getMessage());
        }
    }

    /**
     * 检查用户是否是对话所有者
     */
    public boolean isOwner(String conversationId, Long userId) {
        ConversationContext context = conversationContexts.get(conversationId);
        if (context == null) {
            // 尝试从数据库查询
            List<ConversationRecord> records = conversationRecordMapper.selectByConversationId(conversationId);
            if (records.isEmpty()) {
                return false;
            }
            return String.valueOf(userId).equals(records.get(0).getUserId());
        }
        return String.valueOf(userId).equals(context.getUserId());
    }

    /**
     * 发送消息（用户输入 → AI NPC 回复）
     */
    @Transactional
    public ConversationSendResult sendMessage(String conversationId, String userInput) {
        try {
            // 1. 获取对话上下文
            ConversationContext context = conversationContexts.get(conversationId);
            if (context == null) {
                throw new IllegalArgumentException("对话不存在或已结束");
            }

            if (context.getIsCompleted()) {
                throw new IllegalStateException("对话已结束");
            }

            // 2. 检查轮次
            if (context.getCurrentRound() >= context.getMaxRounds()) {
                context.setIsCompleted(true);
                throw new IllegalStateException("已达到最大对话轮次");
            }

            // 3. 加载场景和 NPC
            Scene scene = sceneMapper.selectById(context.getSceneId());
            NpcCharacter npc = npcCharacterMapper.selectByNpcId(context.getNpcId());

            // 4. 获取对话历史
            List<ConversationRecord> history = conversationRecordMapper.selectByConversationId(conversationId);

            // 5. 生成 AI NPC 回复
            String npcResponse = aiNpcService.generateNpcResponse(npc, scene, history, userInput);

            // 6. 轮次 +1
            context.setCurrentRound(context.getCurrentRound() + 1);
            int currentRound = context.getCurrentRound();

            // 7. 保存对话记录 - 用户输入和 AI 回复都需要加密
            ConversationRecord record = new ConversationRecord();
            record.setRecordId(IdUtil.fastSimpleUUID());
            record.setConversationId(conversationId);
            record.setSceneId(context.getSceneId());
            record.setUserId(context.getUserId());
            record.setNpcId(context.getNpcId());
            record.setRoundNumber(currentRound);
            record.setUserInput(aesEncryptor.encrypt(userInput)); // 加密用户输入
            record.setNpcResponse(aesEncryptor.encrypt(npcResponse)); // 加密 AI 回复
            record.setAiModel(aiConfigService.getNpcModel());
            record.setCreatedAt(LocalDateTime.now());
            conversationRecordMapper.insert(record);

            // 8. 检查是否完成
            boolean isCompleted = currentRound >= context.getMaxRounds();
            if (isCompleted) {
                context.setIsCompleted(true);
            }

            log.info("对话消息：conversationId={}, round={}, isCompleted={}", 
                conversationId, currentRound, isCompleted);

            // 9. 返回结果
            ConversationSendResult result = new ConversationSendResult();
            result.setConversationId(conversationId);
            result.setCurrentRound(currentRound);
            result.setMaxRounds(context.getMaxRounds());
            result.setNpcResponse(npcResponse);
            result.setIsCompleted(isCompleted);
            return result;

        } catch (Exception e) {
            log.error("发送消息失败", e);
            throw new RuntimeException("发送消息失败：" + e.getMessage());
        }
    }

    /**
     * 获取对话历史（自动解密）
     */
    public List<ConversationRecord> getConversationHistory(String conversationId) {
        List<ConversationRecord> records = conversationRecordMapper.selectByConversationId(conversationId);
        // 解密每条记录的内容
        for (ConversationRecord record : records) {
            if (record.getUserInput() != null && !record.getUserInput().isEmpty()) {
                record.setUserInput(aesEncryptor.decrypt(record.getUserInput()));
            }
            if (record.getNpcResponse() != null && !record.getNpcResponse().isEmpty()) {
                record.setNpcResponse(aesEncryptor.decrypt(record.getNpcResponse()));
            }
        }
        return records;
    }

    /**
     * 结束对话
     */
    public void endConversation(String conversationId) {
        ConversationContext context = conversationContexts.get(conversationId);
        if (context != null) {
            context.setIsCompleted(true);
            log.info("对话结束：conversationId={}", conversationId);
        }
    }

    /**
     * 生成 NPC 欢迎语
     */
    private String generateGreeting(NpcCharacter npc, Scene scene) {
        StringBuilder greeting = new StringBuilder();

        greeting.append("你好呀～我是").append(npc.getName());
        
        if (npc.getOccupation() != null) {
            greeting.append("，是这里的").append(npc.getOccupation());
        }
        
        greeting.append("。");
        
        if (scene != null && scene.getBackground() != null) {
            greeting.append("\n").append(scene.getBackground());
        }
        
        greeting.append("\n今天想聊点什么呢？(微笑)");

        return greeting.toString();
    }

    // ==================== 内部类 ====================

    /**
     * 对话上下文
     */
    public static class ConversationContext {
        private String conversationId;
        private String userId;
        private String sceneId;
        private String npcId;
        private int currentRound;
        private int maxRounds;
        private boolean isCompleted;

        // Getters and Setters
        public String getConversationId() { return conversationId; }
        public void setConversationId(String conversationId) { this.conversationId = conversationId; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getSceneId() { return sceneId; }
        public void setSceneId(String sceneId) { this.sceneId = sceneId; }
        public String getNpcId() { return npcId; }
        public void setNpcId(String npcId) { this.npcId = npcId; }
        public int getCurrentRound() { return currentRound; }
        public void setCurrentRound(int currentRound) { this.currentRound = currentRound; }
        public int getMaxRounds() { return maxRounds; }
        public void setMaxRounds(int maxRounds) { this.maxRounds = maxRounds; }
        public boolean getIsCompleted() { return isCompleted; }
        public void setIsCompleted(boolean isCompleted) { this.isCompleted = isCompleted; }
    }

    /**
     * 开始对话结果
     */
    public static class ConversationStartResult {
        private String conversationId;
        private int currentRound;
        private int maxRounds;
        private String npcGreeting;
        private String npcName;
        private String sceneName;

        // Getters and Setters
        public String getConversationId() { return conversationId; }
        public void setConversationId(String conversationId) { this.conversationId = conversationId; }
        public int getCurrentRound() { return currentRound; }
        public void setCurrentRound(int currentRound) { this.currentRound = currentRound; }
        public int getMaxRounds() { return maxRounds; }
        public void setMaxRounds(int maxRounds) { this.maxRounds = maxRounds; }
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
        private boolean isCompleted;

        // Getters and Setters
        public String getConversationId() { return conversationId; }
        public void setConversationId(String conversationId) { this.conversationId = conversationId; }
        public int getCurrentRound() { return currentRound; }
        public void setCurrentRound(int currentRound) { this.currentRound = currentRound; }
        public int getMaxRounds() { return maxRounds; }
        public void setMaxRounds(int maxRounds) { this.maxRounds = maxRounds; }
        public String getNpcResponse() { return npcResponse; }
        public void setNpcResponse(String npcResponse) { this.npcResponse = npcResponse; }
        public boolean getIsCompleted() { return isCompleted; }
        public void setIsCompleted(boolean isCompleted) { this.isCompleted = isCompleted; }
    }
}
