package cn.qrfeng.lianai.game.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * 开始会话请求 DTO
 */
@Data
public class StartConversationRequest {
    
    @NotBlank(message = "场景 ID 不能为空")
    @Pattern(regexp = "^SCENE_[0-9]{10}$", message = "场景 ID 格式错误，应为 SCENE_开头的 10 位数字")
    private String sceneId;
    
    // 不再从前端接收 userId，改用当前登录用户
}
