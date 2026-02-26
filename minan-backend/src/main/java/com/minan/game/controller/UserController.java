package com.minan.game.controller;

import com.minan.game.dto.Response;
import com.minan.game.dto.UserLoginRequest;
import com.minan.game.dto.UserRegisterRequest;
import com.minan.game.model.User;
import com.minan.game.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Response<User> login(@RequestBody UserLoginRequest request) {
        User user = userService.login(request);
        if (user == null) {
            return Response.error("用户名或密码错误");
        }
        return Response.success(user);
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
