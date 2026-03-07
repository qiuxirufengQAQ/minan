package cn.qrfeng.lianai.game.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("learning_resource")
public class LearningResource {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String resourceId;

    private String pointId;

    private String title;

    private String content;

    private String summary;

    private String resourceType;

    private String resourceUrl;

    private String thumbnailUrl;

    private Integer duration;

    private Integer difficulty;

    @TableField("`order`")
    private Integer order;

    private Integer isRequired;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @TableField(exist = false)
    private String pointName;
}
