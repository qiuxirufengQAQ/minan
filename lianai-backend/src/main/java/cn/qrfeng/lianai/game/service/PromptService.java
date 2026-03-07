package cn.qrfeng.lianai.game.service;

import cn.qrfeng.lianai.game.engine.PromptTemplateEngine;
import cn.qrfeng.lianai.game.mapper.NpcCharacterMapper;
import cn.qrfeng.lianai.game.mapper.SceneMapper;
import cn.qrfeng.lianai.game.mapper.UserMapper;
import cn.qrfeng.lianai.game.model.ConversationRecord;
import cn.qrfeng.lianai.game.model.NpcCharacter;
import cn.qrfeng.lianai.game.model.Scene;
import cn.qrfeng.lianai.game.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 提示词服务
 * 封装模板引擎调用逻辑
 */
@Slf4j
@Service
public class PromptService {

    @Autowired
    private PromptTemplateEngine templateEngine;

    @Autowired
    private NpcCharacterMapper npcMapper;

    @Autowired
    private SceneMapper sceneMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 构建角色扮演提示词
     *
     * @param npcId    NPC ID
     * @param sceneId  场景 ID
     * @param userId   用户 ID
     * @param userInput 用户输入
     * @param history  对话历史
     * @return 提示词
     */
    public String buildRolePlayPrompt(String npcId, String sceneId, String userId,
                                       String userInput, List<ConversationRecord> history) {
        long startTime = System.currentTimeMillis();

        try {
            // 1. 准备上下文数据
            Map<String, Object> context = new HashMap<>();

            // 加载 NPC 数据
            NpcCharacter npc = npcMapper.selectByNpcId(npcId);
            if (npc == null) {
                throw new IllegalArgumentException("NPC 不存在：" + npcId);
            }
            context.put("npc_character", npc);

            // 加载场景数据
            Scene scene = sceneMapper.selectBySceneId(sceneId);
            if (scene == null) {
                throw new IllegalArgumentException("场景不存在：" + sceneId);
            }
            context.put("scene", scene);

            // 加载用户数据
            User user = userMapper.selectByUserId(userId);
            if (user == null) {
                throw new IllegalArgumentException("用户不存在：" + userId);
            }
            context.put("user", user);

            // 转换对话历史
            List<Map<String, Object>> historyList = convertHistory(history);
            context.put("conversation_history", historyList);
            context.put("user_input", userInput);

            // 2. 构建提示词
            String prompt = templateEngine.buildPrompt("role_play", context);

            long buildTime = System.currentTimeMillis() - startTime;
            log.info("构建角色扮演提示词，NPC: {}, 场景：{}, 耗时：{}ms, Token 估算：{}",
                    npcId, sceneId, buildTime, prompt.length() / 4);

            // 3. 记录使用日志
            templateEngine.logUsage(
                1L,  // template_id (角色扮演模板)
                npcId,
                sceneId,
                userId,
                prompt.length() / 4,
                (int) buildTime
            );

            return prompt;

        } catch (Exception e) {
            log.error("构建角色扮演提示词失败：{}", e.getMessage(), e);
            throw new RuntimeException("构建提示词失败：" + e.getMessage(), e);
        }
    }

    /**
     * 构建 AI 教练评估提示词
     *
     * @param npcId    NPC ID
     * @param sceneId  场景 ID
     * @param history  完整对话历史
     * @return 提示词
     */
    public String buildCoachEvaluationPrompt(String npcId, String sceneId,
                                              List<ConversationRecord> history) {
        try {
            Map<String, Object> context = new HashMap<>();

            // 加载 NPC 数据
            NpcCharacter npc = npcMapper.selectByNpcId(npcId);
            context.put("npc_character", npc);

            // 加载场景数据
            Scene scene = sceneMapper.selectBySceneId(sceneId);
            context.put("scene", scene);

            // 转换对话历史
            List<Map<String, Object>> historyList = convertHistory(history);
            context.put("conversation_history", historyList);

            // 构建提示词
            String prompt = templateEngine.buildPrompt("coach_evaluation", context);

            log.info("构建 AI 教练评估提示词，NPC: {}, 场景：{}, 历史轮数：{}",
                    npcId, sceneId, history.size());

            return prompt;

        } catch (Exception e) {
            log.error("构建 AI 教练评估提示词失败：{}", e.getMessage(), e);
            throw new RuntimeException("构建提示词失败：" + e.getMessage(), e);
        }
    }

    /**
     * 构建场景介绍提示词
     *
     * @param sceneId 场景 ID
     * @param npcId   NPC ID
     * @return 提示词
     */
    public String buildSceneIntroductionPrompt(String sceneId, String npcId) {
        try {
            Map<String, Object> context = new HashMap<>();

            // 加载场景数据
            Scene scene = sceneMapper.selectBySceneId(sceneId);
            context.put("scene", scene);

            // 加载 NPC 数据
            NpcCharacter npc = npcMapper.selectByNpcId(npcId);
            context.put("npc_character", npc);

            // 构建提示词
            String prompt = templateEngine.buildPrompt("scene_introduction", context);

            log.info("构建场景介绍提示词，场景：{}, NPC: {}", sceneId, npcId);

            return prompt;

        } catch (Exception e) {
            log.error("构建场景介绍提示词失败：{}", e.getMessage(), e);
            throw new RuntimeException("构建提示词失败：" + e.getMessage(), e);
        }
    }

    /**
     * 测试模板
     *
     * @param templateType 模板类型
     * @param testContext  测试上下文
     * @return 测试结果
     */
    public Map<String, Object> testTemplate(String templateType, Map<String, Object> testContext) {
        return templateEngine.testTemplate(templateType, testContext);
    }

    /**
     * 转换对话历史
     */
    private List<Map<String, Object>> convertHistory(List<ConversationRecord> history) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (history == null || history.isEmpty()) {
            return result;
        }

        for (ConversationRecord record : history) {
            Map<String, Object> item = new HashMap<>();
            item.put("userInput", record.getUserInput());
            item.put("npcResponse", record.getNpcResponse());
            result.add(item);
        }

        return result;
    }

    /**
     * 清除模板缓存
     */
    public void clearTemplateCache(String templateType) {
        templateEngine.clearCache(templateType);
        log.info("清除模板缓存：{}", templateType);
    }
}
