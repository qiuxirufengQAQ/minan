package cn.qrfeng.lianai.game.controller;

import cn.qrfeng.lianai.game.dto.Response;
import cn.qrfeng.lianai.game.dto.UserLoginRequest;
import cn.qrfeng.lianai.game.dto.UserRegisterRequest;
import cn.qrfeng.lianai.game.model.User;
import cn.qrfeng.lianai.game.service.UserService;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Response<Map<String, Object>> login(@RequestBody UserLoginRequest request) {
        User user = userService.login(request);
        if (user == null) {
            return Response.error("用户名或密码错误");
        }
        
        // 登录并生成 token
        StpUtil.login(user.getId());
        String token = StpUtil.getTokenValue();
        
        // 返回用户信息和 token
        Map<String, Object> data = new HashMap<>();
        data.put("user", user);
        data.put("token", token);
        
        return Response.success(data);
    }

    @PostMapping("/register")
    public Response<User> register(@RequestBody UserRegisterRequest request) {
        User user = userService.register(request);
        if (user == null) {
            return Response.error("用户名已存在");
        }
        return Response.success(user);
    }

    @GetMapping("/getDetail")
    public Response<User> getDetail(@RequestParam String userId) {
        User user = userService.getUserByUserId(userId);
        if (user == null) {
            return Response.error("用户不存在");
        }
        return Response.success(user);
    }
}
