package com.minan.game.config;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;

/**
 * Sa-Token 权限验证配置
 */
@Configuration
public class SaTokenAuthConfig {
    
    /**
     * 权限校验方法
     */
    public void checkPermission(String permission) {
        // 校验当前会话是否具有指定权限
        StpUtil.checkPermission(permission);
    }
    
    /**
     * 角色校验方法
     */
    public void checkRole(String role) {
        // 校验当前会话是否具有指定角色
        StpUtil.checkRole(role);
    }
}