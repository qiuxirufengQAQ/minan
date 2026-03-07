package cn.qrfeng.lianai.game.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_npc_relation")
public class UserNpcRelation {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String userId;

    private String npcId;

    private Integer intimacyScore;

    private Integer interactionCount;

    private Integer unlockedSceneLevel;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
