package cn.qrfeng.lianai.game.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String userId;

    private String username;

    private String password;

    private String wechatOpenid;

    private String nickname;

    private String avatar;

    private Integer totalCp;

    private Integer level;

    private Integer totalScore;

    private Integer completedScenes;

    private String currentLevelId;

    private String unlockedLevels;

    private Integer totalScenesCompleted;

    private Integer totalTimeSpent;

    private Integer streakDays;

    private LocalDateTime lastLoginAt;

    private String motto;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}