package cn.qrfeng.lianai.game.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_scene_interaction")
public class UserSceneInteraction {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String userId;

    private String npcId;

    private String sceneId;

    private Integer score;

    private Integer intimacyGained;

    private String userInput;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
