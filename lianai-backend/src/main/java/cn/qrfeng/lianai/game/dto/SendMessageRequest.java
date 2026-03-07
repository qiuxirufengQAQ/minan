package cn.qrfeng.lianai.game.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 发送消息请求 DTO
 */
@Data
public class SendMessageRequest {
    
    @NotBlank(message = "消息内容不能为空")
    @Size(max = 2000, message = "消息内容不能超过 2000 字")
    private String content;
    
    // 不再从前端接收 conversationId，从上下文获取
}
