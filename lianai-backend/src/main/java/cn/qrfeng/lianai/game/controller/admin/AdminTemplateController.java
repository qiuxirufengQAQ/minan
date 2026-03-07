package cn.qrfeng.lianai.game.controller.admin;

import cn.qrfeng.lianai.game.dto.Response;
import cn.qrfeng.lianai.game.service.PromptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 提示词模板管理后台 Controller
 */
@Slf4j
@RestController
@RequestMapping("/admin/api/templates")
public class AdminTemplateController {

    @Autowired
    private PromptService promptService;

    /**
     * 获取模板列表
     */
    @GetMapping("/list")
    public Response<List<Map<String, Object>>> getTemplateList(
        @RequestParam(required = false) String templateType
    ) {
        try {
            // TODO: 实现从数据库查询模板列表
            List<Map<String, Object>> list = new ArrayList<>();
            
            Map<String, Object> template1 = new HashMap<>();
            template1.put("id", 1);
            template1.put("name", "角色扮演模板 v2");
            template1.put("templateType", "role_play");
            template1.put("version", 2);
            template1.put("active", true);
            template1.put("createdAt", "2026-03-07 23:00:00");
            list.add(template1);
            
            Map<String, Object> template2 = new HashMap<>();
            template2.put("id", 2);
            template2.put("name", "AI 教练评估模板");
            template2.put("templateType", "coach_evaluation");
            template2.put("version", 1);
            template2.put("active", true);
            template2.put("createdAt", "2026-03-07 23:00:00");
            list.add(template2);
            
            Map<String, Object> template3 = new HashMap<>();
            template3.put("id", 3);
            template3.put("name", "场景介绍模板");
            template3.put("templateType", "scene_introduction");
            template3.put("version", 1);
            template3.put("active", true);
            template3.put("createdAt", "2026-03-07 23:00:00");
            list.add(template3);
            
            return Response.success(list);
            
        } catch (Exception e) {
            log.error("获取模板列表失败：{}", e.getMessage());
            return Response.error("获取失败：" + e.getMessage());
        }
    }

    /**
     * 获取模板详情
     */
    @GetMapping("/{id}")
    public Response<Map<String, Object>> getTemplateDetail(@PathVariable Long id) {
        try {
            // TODO: 实现从数据库查询模板详情
            Map<String, Object> template = new HashMap<>();
            template.put("id", id);
            template.put("name", "角色扮演模板 v2");
            template.put("templateType", "role_play");
            template.put("version", 2);
            template.put("content", getSampleTemplate());
            template.put("variableMapping", getSampleVariableMapping());
            template.put("active", true);
            
            return Response.success(template);
            
        } catch (Exception e) {
            log.error("获取模板详情失败：{}", e.getMessage());
            return Response.error("获取失败：" + e.getMessage());
        }
    }

    /**
     * 创建新版本
     */
    @PostMapping("/{id}/versions")
    public Response<Map<String, Object>> createNewVersion(
        @PathVariable Long id,
        @RequestBody Map<String, Object> request
    ) {
        try {
            String content = (String) request.get("content");
            Map<String, Object> variableMapping = (Map<String, Object>) request.get("variableMapping");
            String description = (String) request.get("description");
            
            // TODO: 实现创建新版本逻辑
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("newVersion", 3);
            result.put("message", "新版本创建成功");
            
            log.info("创建模板新版本，模板 ID: {}, 新版本：{}", id, 3);
            
            return Response.success(result);
            
        } catch (Exception e) {
            log.error("创建新版本失败：{}", e.getMessage());
            return Response.error("创建失败：" + e.getMessage());
        }
    }

    /**
     * 切换启用版本
     */
    @PostMapping("/switch")
    public Response<Map<String, Object>> switchVersion(
        @RequestParam String templateType,
        @RequestParam Integer version
    ) {
        try {
            // TODO: 实现切换版本逻辑
            promptService.clearTemplateCache(templateType);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "版本切换成功");
            
            log.info("切换模板版本，类型：{}, 版本：{}", templateType, version);
            
            return Response.success(result);
            
        } catch (Exception e) {
            log.error("切换版本失败：{}", e.getMessage());
            return Response.error("切换失败：" + e.getMessage());
        }
    }

    /**
     * 获取使用统计
     */
    @GetMapping("/stats")
    public Response<Map<String, Object>> getUsageStats(
        @RequestParam(required = false) Long templateId,
        @RequestParam(defaultValue = "7") Integer days
    ) {
        try {
            // TODO: 实现从数据库查询使用统计
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalCalls", 1250);
            stats.put("avgTokens", 280);
            stats.put("avgResponseTime", 150);
            stats.put("successRate", 99.5);
            
            // 7 天趋势
            List<Map<String, Object>> trend = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                Map<String, Object> day = new HashMap<>();
                day.put("date", "2026-03-0" + (i + 1));
                day.put("calls", 150 + i * 20);
                day.put("avgTokens", 275 + i * 5);
                trend.add(day);
            }
            stats.put("trend", trend);
            
            return Response.success(stats);
            
        } catch (Exception e) {
            log.error("获取使用统计失败：{}", e.getMessage());
            return Response.error("获取失败：" + e.getMessage());
        }
    }

    /**
     * 测试模板
     */
    @PostMapping("/test")
    public Response<Map<String, Object>> testTemplate(
        @RequestBody Map<String, Object> request
    ) {
        try {
            String templateType = (String) request.get("templateType");
            @SuppressWarnings("unchecked")
            Map<String, Object> testContext = (Map<String, Object>) request.get("context");
            
            Map<String, Object> result = promptService.testTemplate(templateType, testContext);
            
            return Response.success(result);
            
        } catch (Exception e) {
            log.error("测试模板失败：{}", e.getMessage());
            return Response.error("测试失败：" + e.getMessage());
        }
    }

    /**
     * 清除缓存
     */
    @PostMapping("/cache/clear")
    public Response<Map<String, Object>> clearCache(
        @RequestParam(required = false) String templateType
    ) {
        try {
            promptService.clearTemplateCache(templateType);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "缓存清除成功");
            
            return Response.success(result);
            
        } catch (Exception e) {
            log.error("清除缓存失败：{}", e.getMessage());
            return Response.error("清除失败：" + e.getMessage());
        }
    }

    // ===== 辅助方法 =====

    private String getSampleTemplate() {
        return "# Role\n" +
               "你是{npc_name}，正在与{user_nickname}进行对话。\n" +
               "\n" +
               "## 角色设定\n" +
               "【基本信息】\n" +
               "- 姓名：{npc_name}\n" +
               "{#if npc_age}- 年龄：{npc_age}岁\n" +
               "{/if}- 性格：{npc_personality}\n" +
               "\n" +
               "## 场景\n" +
               "{scene_description}\n" +
               "\n" +
               "## 对话历史\n" +
               "{conversation_history}\n" +
               "\n" +
               "## 当前对话\n" +
               "用户：{user_input}\n" +
               "{npc_name}：";
    }

    private Map<String, Object> getSampleVariableMapping() {
        Map<String, Object> mapping = new HashMap<>();
        
        Map<String, Object> npcName = new HashMap<>();
        npcName.put("source", "npc_character");
        npcName.put("field", "name");
        npcName.put("required", true);
        mapping.put("npc_name", npcName);
        
        Map<String, Object> personality = new HashMap<>();
        personality.put("source", "npc_character");
        personality.put("field", "personality");
        personality.put("required", true);
        mapping.put("npc_personality", personality);
        
        Map<String, Object> sceneDesc = new HashMap<>();
        sceneDesc.put("source", "scene");
        sceneDesc.put("field", "description");
        sceneDesc.put("required", true);
        mapping.put("scene_description", sceneDesc);
        
        return mapping;
    }
}
