package com.minan.game.controller.permission;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import com.minan.game.model.permission.Permission;
import com.minan.game.model.permission.Role;
import com.minan.game.model.permission.UserRole;
import com.minan.game.service.permission.PermissionService;
import com.minan.game.service.permission.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 权限管理控制器
 */
@RestController
@RequestMapping("/permission")
public class PermissionController {
    
    @Autowired
    private PermissionService permissionService;
    
    @Autowired
    private RoleService roleService;
    
    /**
     * 获取当前用户权限列表
     */
    @GetMapping("/my-permissions")
    public List<String> getMyPermissions() {
        return StpUtil.getPermissionList();
    }
    
    /**
     * 获取当前用户角色列表
     */
    @GetMapping("/my-roles")
    public List<String> getMyRoles() {
        return StpUtil.getRoleList();
    }
    
    /**
     * 创建权限 (需要管理员权限)
     */
    @SaCheckRole("admin")
    @PostMapping("/permissions")
    public boolean createPermission(@RequestBody Permission permission) {
        return permissionService.createPermission(permission);
    }
    
    /**
     * 创建角色 (需要管理员权限)
     */
    @SaCheckRole("admin")
    @PostMapping("/roles")
    public boolean createRole(@RequestBody Role role) {
        return roleService.createRole(role);
    }
    
    /**
     * 分配用户角色 (需要管理员权限)
     */
    @SaCheckRole("admin")
    @PostMapping("/assign-role")
    public boolean assignUserRole(@RequestBody UserRole userRole) {
        return roleService.assignUserRole(userRole.getUserId(), userRole.getRoleId());
    }
}