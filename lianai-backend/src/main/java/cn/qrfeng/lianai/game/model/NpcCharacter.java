package cn.qrfeng.lianai.game.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("npc_character")
public class NpcCharacter {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String npcId;

    private String name;

    private String avatarUrl;

    private String personality;

    private String background;

    private String gender;

    private String ageRange;

    private String occupation;

    private String interests;

    private String conversationStyle;

    private Integer isActive;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
