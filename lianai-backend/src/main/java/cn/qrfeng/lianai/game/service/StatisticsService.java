package cn.qrfeng.lianai.game.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import cn.qrfeng.lianai.game.dto.StatisticsResponse;
import cn.qrfeng.lianai.game.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatisticsService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LevelMapper levelMapper;

    @Autowired
    private SceneMapper sceneMapper;

    @Autowired
    private UserLevelMapper userLevelMapper;

    @Autowired
    private EvaluationMapper evaluationMapper;

    public StatisticsResponse getStatistics() {
        StatisticsResponse response = new StatisticsResponse();
        
        // 用户统计
        StatisticsResponse.UserStats userStats = getUserStats();
        response.setUserStats(userStats);
        
        // 关卡统计
        StatisticsResponse.LevelStats levelStats = getLevelStats();
        response.setLevelStats(levelStats);
        
        // 评估统计
        StatisticsResponse.EvaluationStats evaluationStats = getEvaluationStats();
        response.setEvaluationStats(evaluationStats);
        
        return response;
    }

    private StatisticsResponse.UserStats getUserStats() {
        StatisticsResponse.UserStats userStats = new StatisticsResponse.UserStats();
        
        // 总用户数
        Long totalUsersCount = userMapper.selectCount(null);
        userStats.setTotalUsers(totalUsersCount.intValue());
        
        // 活跃用户数（最近7天有登录或评估记录的用户）
        LambdaQueryWrapper<cn.qrfeng.lianai.game.model.User> activeQuery = new LambdaQueryWrapper<>();
        // 这里简化处理，实际应该根据最后登录时间或评估时间判断
        // 暂时将所有用户都视为活跃用户
        userStats.setActiveUsers(totalUsersCount.intValue());
        
        // 平均魅力值
        List<cn.qrfeng.lianai.game.model.User> allUsers = userMapper.selectList(null);
        if (!allUsers.isEmpty()) {
            int totalCp = allUsers.stream().mapToInt(u -> u.getTotalCp() != null ? u.getTotalCp() : 0).sum();
            userStats.setAvgCp(totalCp / allUsers.size());
        } else {
            userStats.setAvgCp(0);
        }
        
        // 最高魅力值
        int maxCp = allUsers.stream().mapToInt(u -> u.getTotalCp() != null ? u.getTotalCp() : 0).max().orElse(0);
        userStats.setMaxCp(maxCp);
        
        return userStats;
    }

    private StatisticsResponse.LevelStats getLevelStats() {
        StatisticsResponse.LevelStats levelStats = new StatisticsResponse.LevelStats();
        
        // 总关卡数
        Long totalLevelsCount = levelMapper.selectCount(null);
        levelStats.setTotalLevels(totalLevelsCount.intValue());
        
        // 总场景数
        Long totalScenesCount = sceneMapper.selectCount(null);
        levelStats.setTotalScenes(totalScenesCount.intValue());
        
        // 已完成关卡数
        Long completedLevelsCount = userLevelMapper.selectCount(
            new LambdaQueryWrapper<cn.qrfeng.lianai.game.model.UserLevel>()
                .eq(cn.qrfeng.lianai.game.model.UserLevel::getIsCompleted, true)
        );
        levelStats.setCompletedLevels(completedLevelsCount.intValue());
        
        // 关卡完成率
        if (totalLevelsCount > 0) {
            double rate = (completedLevelsCount.doubleValue() / totalLevelsCount.doubleValue()) * 100;
            levelStats.setCompletionRate(Math.round(rate * 100.0) / 100.0);
        } else {
            levelStats.setCompletionRate(0.0);
        }
        
        return levelStats;
    }

    private StatisticsResponse.EvaluationStats getEvaluationStats() {
        StatisticsResponse.EvaluationStats evaluationStats = new StatisticsResponse.EvaluationStats();
        
        // 总评估次数
        Long totalEvaluationsCount = evaluationMapper.selectCount(null);
        evaluationStats.setTotalEvaluations(totalEvaluationsCount.intValue());
        
        // 获取所有评估记录
        List<cn.qrfeng.lianai.game.model.Evaluation> allEvaluations = evaluationMapper.selectList(null);
        
        if (!allEvaluations.isEmpty()) {
            // 平均评分
            double avgScore = allEvaluations.stream()
                    .mapToDouble(e -> e.getScore() != null ? e.getScore() : 0.0)
                    .average()
                    .orElse(0.0);
            evaluationStats.setAvgScore(Math.round(avgScore * 100.0) / 100.0);
            
            // 高分次数（>=90分）
            long highScoreCount = allEvaluations.stream()
                    .filter(e -> e.getScore() != null && e.getScore() >= 90.0)
                    .count();
            evaluationStats.setHighScoreCount((int) highScoreCount);
            
            // 低分次数（<60分）
            long lowScoreCount = allEvaluations.stream()
                    .filter(e -> e.getScore() != null && e.getScore() < 60.0)
                    .count();
            evaluationStats.setLowScoreCount((int) lowScoreCount);
        } else {
            evaluationStats.setAvgScore(0.0);
            evaluationStats.setHighScoreCount(0);
            evaluationStats.setLowScoreCount(0);
        }
        
        return evaluationStats;
    }
}
