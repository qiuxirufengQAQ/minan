package cn.qrfeng.lianai.game.service;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import cn.qrfeng.lianai.game.model.ConversationRecord;
import cn.qrfeng.lianai.game.model.NpcCharacter;
import cn.qrfeng.lianai.game.model.Scene;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * AI NPC 服务
 */
@Slf4j
@Service
public class AiNpcService {

    @Autowired
    private AiConfigService aiConfigService;

    private final Generation generation = new Generation();

    /**
     * 生成 NPC 回复
     * 
     * @param npc NPC 角色
     * @param scene 场景
     * @param conversationHistory 对话历史
     * @param userInput 用户输入
     * @return NPC 回复
     */
    public String generateNpcResponse(
        NpcCharacter npc,
        Scene scene,
        List<ConversationRecord> conversationHistory,
        String userInput
    ) {
        try {
            // 构建 Prompt
            String systemPrompt = buildSystemPrompt(npc, scene);
            String userPrompt = buildUserPrompt(conversationHistory, userInput);

            // 调用 AI API
            String response = callAiApi(systemPrompt, userPrompt);

            log.info("AI NPC 生成回复成功：{}", response.substring(0, Math.min(50, response.length())));
            return response;

        } catch (Exception e) {
            log.error("AI NPC 生成回复失败", e);
            // 返回默认回复
            return getDefaultResponse(npc);
        }
    }

    /**
     * 构建 System Prompt（角色设定）
     */
    private String buildSystemPrompt(NpcCharacter npc, Scene scene) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("你是一位").append(npc.getOccupation() != null ? npc.getOccupation() : "朋友");
        prompt.append("，名叫").append(npc.getName()).append("。\n");

        if (npc.getPersonality() != null) {
            prompt.append("性格特点：").append(npc.getPersonality()).append("\n");
        }

        if (npc.getBackground() != null) {
            prompt.append("背景故事：").append(npc.getBackground()).append("\n");
        }

        if (npc.getConversationStyle() != null) {
            prompt.append("对话风格：").append(npc.getConversationStyle()).append("\n");
        }

        if (scene != null) {
            prompt.append("\n当前场景：").append(scene.getName()).append("\n");
            if (scene.getBackground() != null) {
                prompt.append("场景背景：").append(scene.getBackground()).append("\n");
            }
            if (scene.getTechnique() != null) {
                prompt.append("知识点：").append(scene.getTechnique()).append("\n");
            }
        }

        prompt.append("\n请始终保持角色设定，与用户进行自然对话。");
        prompt.append("不要跳出角色，不要提及你是 AI。");
        prompt.append("回复要简洁自然，像真人聊天一样，50-150 字左右。");

        return prompt.toString();
    }

    /**
     * 构建 User Prompt（对话历史 + 用户输入）
     */
    private String buildUserPrompt(List<ConversationRecord> history, String userInput) {
        StringBuilder prompt = new StringBuilder();

        if (history != null && !history.isEmpty()) {
            prompt.append("对话历史：\n");
            for (int i = 0; i < history.size(); i++) {
                ConversationRecord record = history.get(i);
                prompt.append("第").append(i + 1).append("轮：\n");
                prompt.append("用户：").append(record.getUserInput()).append("\n");
                prompt.append("NPC：").append(record.getNpcResponse()).append("\n");
            }
            prompt.append("\n");
        }

        prompt.append("用户最新输入：").append(userInput).append("\n");
        prompt.append("\n请以 NPC 的身份回复用户：");

        return prompt.toString();
    }

    /**
     * 生成 NPC 回复（供 ConversationService 使用）
     */
    public String generateResponse(String prompt) throws Exception {
        // 直接使用完整提示词调用 AI
        return callAiApiWithSinglePrompt(prompt);
    }

    /**
     * 调用 AI API（单个提示词）
     */
    private String callAiApiWithSinglePrompt(String prompt) throws Exception {
        int maxRetries = 3;
        int retryDelay = 1000;
        int attempt = 0;
        Exception lastException = null;

        while (attempt < maxRetries) {
            try {
                attempt++;
                log.debug("AI API 调用尝试 {}/{}", attempt, maxRetries);

                List<Message> messages = new ArrayList<>();
                messages.add(Message.builder()
                    .role(Role.USER.getValue())
                    .content(prompt)
                    .build());

                GenerationParam param = GenerationParam.builder()
                    .apiKey(aiConfigService.getApiKey())
                    .model(aiConfigService.getNpcModel())
                    .messages(messages)
                    .maxTokens(aiConfigService.getNpcMaxTokens())
                    .temperature(Float.parseFloat(String.valueOf(aiConfigService.getNpcTemperature())))
                    .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                    .build();

                GenerationResult result = Generation.call(param);
                if (result.getStatusCode() == 200 && result.getOutput() != null) {
                    return result.getOutput().getChoices().get(0).getMessage().getContent();
                } else {
                    throw new Exception("AI API 调用失败：" + result.getMessage());
                }

            } catch (Exception e) {
                lastException = e;
                log.warn("AI API 调用失败 (尝试 {}/{})：{}", attempt, maxRetries, e.getMessage());
                if (attempt < maxRetries) {
                    Thread.sleep(retryDelay * attempt);
                }
            }
        }

        throw new Exception("AI API 调用失败（已重试" + maxRetries + "次）：" + lastException.getMessage(), lastException);
    }

    /**
     * 调用 AI API（带重试机制）
     */
    private String callAiApi(String systemPrompt, String userPrompt) throws Exception {
        int maxRetries = 3;
        int retryDelay = 1000; // 1 秒
        int attempt = 0;
        Exception lastException = null;

        while (attempt < maxRetries) {
            try {
                attempt++;
                log.debug("AI API 调用尝试 {}/{}", attempt, maxRetries);

                List<Message> messages = new ArrayList<>();

                // 添加 System Message
                messages.add(Message.builder()
                    .role(Role.SYSTEM.getValue())
                    .content(systemPrompt)
                    .build());

                // 添加 User Message
                messages.add(Message.builder()
                    .role(Role.USER.getValue())
                    .content(userPrompt)
                    .build());

                // 构建请求参数
                GenerationParam param = GenerationParam.builder()
                    .apiKey(aiConfigService.getApiKey())
                    .model(aiConfigService.getNpcModel())
                    .messages(messages)
                    .maxTokens(aiConfigService.getNpcMaxTokens())
                    .temperature(Float.parseFloat(String.valueOf(aiConfigService.getNpcTemperature())))
                    .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                    .build();

                // 调用 API
                GenerationResult result = generation.call(param);

                if (result != null && result.getOutput() != null && result.getOutput().getChoices() != null) {
                    String content = result.getOutput().getChoices().get(0).getMessage().getContent();
                    if (attempt > 1) {
                        log.info("AI API 调用在第 {} 次重试成功", attempt);
                    }
                    return content;
                } else {
                    throw new Exception("AI API 调用失败：" + (result != null ? result.getRequestId() : "无响应"));
                }

            } catch (Exception e) {
                lastException = e;
                log.warn("AI API 调用失败 (尝试 {}/{}): {}", attempt, maxRetries, e.getMessage());
                
                if (attempt < maxRetries) {
                    try {
                        Thread.sleep(retryDelay * attempt); // 指数退避
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new Exception("重试被中断", ie);
                    }
                }
            }
        }

        throw new Exception("AI API 调用失败，已重试 " + maxRetries + " 次", lastException);
    }

    /**
     * 获取默认回复（API 失败时使用）
     */
    private String getDefaultResponse(NpcCharacter npc) {
        String name = npc != null && npc.getName() != null ? npc.getName() : "我";
        
        String[] defaultResponses = {
            name + "有点没听清楚，能再说一遍吗？",
            "嗯嗯，我在听呢，继续说～",
            "这个话题很有意思，你是怎么想的？",
            "哈哈，你说得对！",
            "让我想想怎么回答比较好..."
        };

        // 随机返回一个
        int index = (int) (Math.random() * defaultResponses.length);
        return defaultResponses[index];
    }

    /**
     * 检查回复质量
     */
    public boolean checkResponseQuality(String response) {
        if (response == null || response.trim().isEmpty()) {
            return false;
        }

        // 检查长度
        if (response.length() < 10 || response.length() > 500) {
            log.warn("回复长度异常：{}", response.length());
            return false;
        }

        // 检查是否包含敏感词
        if (response.contains("AI") || response.contains("人工智能") || response.contains("模型")) {
            log.warn("回复包含敏感词");
            return false;
        }

        return true;
    }
}
