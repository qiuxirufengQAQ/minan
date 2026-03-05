package com.minan.game.service.permission;

import cn.dev33.satoken.stp.StpInterface;
import com.minan.game.mapper.permission.PermissionMapper;
import com.minan.game.mapper.permission.RoleMapper;
import com.minan.game.mapper.permission.RolePermissionMapper;
import com.minan.game.mapper.permission.UserRoleMapper;
import com.minan.game.model.permission.Permission;
import com.minan.game.model.permission.Role;
import com.minan.game.model.permission.RolePermission;
import com.minan.game.model.permission.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Sa-Token 权限服务实现
 */
@Service
public class StpInterfaceImpl implements StpInterface {
    
    @Autowired
    private UserRoleMapper userRoleMapper;
    
    @Autowired
    private RolePermissionMapper rolePermissionMapper;
    
    @Autowired
    private RoleMapper roleMapper;
    
    @Autowired
    private PermissionMapper permissionMapper;
    
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // 查询用户拥有的所有权限
        List<UserRole> userRoles = userRoleMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<UserRole>()
                .eq("user_id", loginId)
        );
        
        if (userRoles.isEmpty()) {
            return List.of();
        }
        
        List<Long> roleIds = userRoles.stream()
            .map(UserRole::getRoleId)
            .collect(Collectors.toList());
            
        List<RolePermission> rolePermissions = rolePermissionMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<RolePermission>()
                .in("role_id", roleIds)
        );
        
        List<Long> permissionIds = rolePermissions.stream()
            .map(RolePermission::getPermissionId)
            .collect(Collectors.toList());
            
        if (permissionIds.isEmpty()) {
            return List.of();
        }
        
        List<Permission> permissions = permissionMapper.selectBatchIds(permissionIds);
        return permissions.stream()
            .map(Permission::getCode)
            .collect(Collectors.toList());
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        // 查询用户拥有的所有角色
        List<UserRole> userRoles = userRoleMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<UserRole>()
                .eq("user_id", loginId)
        );
        
        if (userRoles.isEmpty()) {
            return List.of();
        }
        
        List<Long> roleIds = userRoles.stream()
            .map(UserRole::getRoleId)
            .collect(Collectors.toList());
            
        List<Role> roles = roleMapper.selectBatchIds(roleIds);
        return roles.stream()
            .map(Role::getCode)
            .collect(Collectors.toList());
    }
}