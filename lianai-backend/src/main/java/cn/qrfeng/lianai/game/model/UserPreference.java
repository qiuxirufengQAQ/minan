package cn.qrfeng.lianai.game.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_preference")
public class UserPreference {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户偏好唯一标识
     */
    private String userPreferenceId;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 声音是否开启（1=开启，0=关闭）
     */
    private Integer soundEnabled;

    /**
     * 主题
     */
    private String theme;

    /**
     * 通知是否开启（1=开启，0=关闭）
     */
    private Integer notificationsEnabled;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}