package cn.qrfeng.lianai.game.service;

import cn.qrfeng.lianai.game.mapper.prompt.PromptTemplateMapper;
import cn.qrfeng.lianai.game.model.prompt.PromptTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * A/B 测试服务
 */
@Slf4j
@Service
public class ABTestService {

    @Autowired
    private PromptTemplateMapper templateMapper;

    /**
     * 分配用户到测试版本
     * @param testId 测试 ID
     * @param userId 用户 ID
     * @return 版本：A 或 B
     */
    public String assignVariant(Long testId, String userId) {
        // 简化实现：使用哈希分配
        int hash = Math.abs(userId.hashCode() % 100);
        int trafficSplit = 50; // 默认 50% 流量
        
        return hash < trafficSplit ? "A" : "B";
    }

    /**
     * 获取测试版本的模板
     * @param testId 测试 ID
     * @param variant 版本
     * @return 模板
     */
    public PromptTemplate getVariantTemplate(Long testId, String variant) {
        // TODO: 从数据库查询测试配置，返回对应版本的模板
        // 这里返回示例模板
        PromptTemplate template = new PromptTemplate();
        template.setId(testId);
        template.setName("测试模板-" + variant);
        template.setTemplateType("role_play");
        template.setVersion(1);
        return template;
    }

    /**
     * 记录测试结果
     * @param testId 测试 ID
     * @param variant 版本
     * @param templateId 模板 ID
     * @param tokens Token 消耗
     * @param responseTime 响应时间
     * @param completed 是否完成
     */
    public void recordResult(Long testId, String variant, Long templateId, 
                            int tokens, int responseTime, boolean completed) {
        // TODO: 保存到 prompt_ab_test_result 表
        log.info("记录 A/B 测试结果：testId={}, variant={}, completed={}", testId, variant, completed);
    }

    /**
     * 获取测试统计
     * @param testId 测试 ID
     * @return 统计数据
     */
    public Map<String, Object> getTestStats(Long testId) {
        Map<String, Object> stats = new HashMap<>();
        
        // 示例数据
        Map<String, Object> variantA = new HashMap<>();
        variantA.put("exposureCount", 500);
        variantA.put("completionCount", 450);
        variantA.put("completionRate", 90.0);
        variantA.put("avgTokens", 280);
        variantA.put("avgResponseTime", 150);
        
        Map<String, Object> variantB = new HashMap<>();
        variantB.put("exposureCount", 500);
        variantB.put("completionCount", 470);
        variantB.put("completionRate", 94.0);
        variantB.put("avgTokens", 275);
        variantB.put("avgResponseTime", 145);
        
        stats.put("variantA", variantA);
        stats.put("variantB", variantB);
        
        // 计算提升
        double improvement = (94.0 - 90.0) / 90.0 * 100;
        stats.put("improvement", improvement);
        stats.put("winner", improvement > 0 ? "B" : "A");
        
        return stats;
    }

    /**
     * 创建 A/B 测试
     * @param testName 测试名称
     * @param templateType 模板类型
     * @param variantAId A 版本模板 ID
     * @param variantBId B 版本模板 ID
     * @param trafficSplit 流量分配（A 版本占比）
     * @return 测试 ID
     */
    public Long createTest(String testName, String templateType, 
                          Long variantAId, Long variantBId, Integer trafficSplit) {
        // TODO: 保存到 prompt_ab_test 表
        log.info("创建 A/B 测试：{}, 类型：{}, 流量分配：{}", testName, templateType, trafficSplit);
        return System.currentTimeMillis() % 10000L; // 示例 ID
    }

    /**
     * 启动测试
     * @param testId 测试 ID
     */
    public void startTest(Long testId) {
        // TODO: 更新状态为 running
        log.info("启动 A/B 测试：{}", testId);
    }

    /**
     * 停止测试
     * @param testId 测试 ID
     */
    public void stopTest(Long testId) {
        // TODO: 更新状态为 stopped
        log.info("停止 A/B 测试：{}", testId);
    }

    /**
     * 获取所有测试列表
     * @return 测试列表
     */
    public List<Map<String, Object>> listTests() {
        List<Map<String, Object>> tests = new ArrayList<>();
        
        Map<String, Object> test1 = new HashMap<>();
        test1.put("id", 1);
        test1.put("testName", "角色扮演模板 v2 vs v3");
        test1.put("templateType", "role_play");
        test1.put("status", "running");
        test1.put("trafficSplit", 50);
        test1.put("createdAt", "2026-03-08 00:00:00");
        tests.add(test1);
        
        Map<String, Object> test2 = new HashMap<>();
        test2.put("id", 2);
        test2.put("testName", "开场白模板 A/B");
        test2.put("templateType", "ice_breaker");
        test2.put("status", "draft");
        test2.put("trafficSplit", 50);
        test2.put("createdAt", "2026-03-08 00:00:00");
        tests.add(test2);
        
        return tests;
    }
}
