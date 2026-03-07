package cn.qrfeng.lianai.game.dto;

import lombok.Data;

@Data
public class StatisticsResponse {
    private UserStats userStats;
    private LevelStats levelStats;
    private EvaluationStats evaluationStats;

    @Data
    public static class UserStats {
        private Integer totalUsers;
        private Integer activeUsers;
        private Integer avgCp;
        private Integer maxCp;
    }

    @Data
    public static class LevelStats {
        private Integer totalLevels;
        private Integer totalScenes;
        private Integer completedLevels;
        private Double completionRate;
    }

    @Data
    public static class EvaluationStats {
        private Integer totalEvaluations;
        private Double avgScore;
        private Integer highScoreCount;
        private Integer lowScoreCount;
    }
}
