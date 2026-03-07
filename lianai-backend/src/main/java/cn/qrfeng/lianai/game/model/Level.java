package cn.qrfeng.lianai.game.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("level")
public class Level {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String levelId;

    private String name;

    @TableField("`order`")
    private Integer order;

    private String description;

    private String theme;

    private String theory;

    private Integer cpRangeMin;

    private Integer cpRangeMax;

    private String iconUrl;

    private String unlockCondition;

    private Integer estimatedTime;

    private Integer difficulty;

    private String difficultyLevel;

    @TableField(exist = false)
    private Boolean isUnlocked;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}