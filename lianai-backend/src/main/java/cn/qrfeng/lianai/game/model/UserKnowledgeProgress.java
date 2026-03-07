package cn.qrfeng.lianai.game.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_knowledge_progress")
public class UserKnowledgeProgress {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String progressId;

    private String userId;

    private String pointId;

    private String status;

    private Integer progress;

    private Integer studyTime;

    private Integer masteryLevel;

    private LocalDateTime lastStudyAt;

    private LocalDateTime completedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
