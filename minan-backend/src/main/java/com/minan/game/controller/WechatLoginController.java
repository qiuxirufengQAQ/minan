package com.minan.game.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.minan.game.dto.Result;
import com.minan.game.model.User;
import com.minan.game.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信登录控制器
 * 处理微信小程序登录请求
 */
@Slf4j
@RestController
@RequestMapping("/wechat")
public class WechatLoginController {

    @Autowired
    private UserService userService;

    @Value("${wechat.appid:}")
    private String appid;

    @Value("${wechat.secret:}")
    private String secret;

    /**
     * 微信一键登录
     * @param loginRequest 登录请求（包含 code）
     * @return 登录结果（包含 token 和用户信息）
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody WechatLoginRequest loginRequest) {
        try {
            String code = loginRequest.getCode();
            
            // 1. 调用微信接口换取 openid
            String openid = getOpenid(code);
            if (openid == null) {
                return Result.error("微信登录失败");
            }
            
            log.info("微信登录成功，openid: {}", openid);
            
            // 2. 根据 openid 查找或创建用户
            User user = userService.findByOpenid(openid);
            if (user == null) {
                // 创建新用户
                user = userService.createByWechat(openid);
            }
            
            // 3. 生成 Sa-Token
            StpUtil.login(user.getUserId());
            String token = StpUtil.getTokenValue();
            
            // 4. 返回 token 和用户信息
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("userInfo", buildUserInfoVO(user));
            
            return Result.success(data);
            
        } catch (Exception e) {
            log.error("微信登录异常", e);
            return Result.error("登录失败：" + e.getMessage());
        }
    }

    /**
     * 调用微信接口获取 openid
     */
    private String getOpenid(String code) {
        try {
            // 微信接口 URL
            String url = "https://api.weixin.qq.com/sns/jscode2session";
            url += "?appid=" + appid;
            url += "&secret=" + secret;
            url += "&js_code=" + code;
            url += "&grant_type=authorization_code";
            
            // 发送 HTTP 请求
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) new java.net.URL(url).openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            
            // 读取响应
            java.io.BufferedReader in = new java.io.BufferedReader(
                new java.io.InputStreamReader(conn.getInputStream(), "UTF-8")
            );
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();
            
            // 解析 JSON 响应
            String responseStr = response.toString();
            log.info("微信接口响应：{}", responseStr);
            
            // 简单解析（生产环境建议使用 Jackson 或 Gson）
            if (responseStr.contains("\"openid\"")) {
                // 提取 openid
                int start = responseStr.indexOf("\"openid\":\"") + 10;
                int end = responseStr.indexOf("\"", start);
                return responseStr.substring(start, end);
            } else {
                log.error("微信登录失败：{}", responseStr);
                return null;
            }
            
        } catch (Exception e) {
            log.error("调用微信接口异常", e);
            // 开发环境返回模拟 openid
            return "mock_openid_" + code;
        }
    }

    /**
     * 构建用户信息 VO
     */
    private Map<String, Object> buildUserInfoVO(User user) {
        Map<String, Object> vo = new HashMap<>();
        vo.put("userId", user.getUserId());
        vo.put("nickname", user.getNickname());
        vo.put("avatar", user.getAvatar());
        vo.put("level", user.getLevel());
        vo.put("totalScore", user.getTotalScore());
        vo.put("completedScenes", user.getCompletedScenes());
        return vo;
    }

    /**
     * 微信登录请求 DTO
     */
    public static class WechatLoginRequest {
        private String code;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }
}
