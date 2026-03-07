package cn.qrfeng.lianai.game.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("scene_hint")
public class SceneHint {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String hintId;

    private String hintText;

    private String hintType;

    @TableField("`order`")
    private Integer order;

    private Integer isUnlocked;

    private String unlockCondition;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
