package com.minan.game.service.permission;

import com.minan.game.mapper.permission.RoleMapper;
import com.minan.game.mapper.permission.UserRoleMapper;
import com.minan.game.model.permission.Role;
import com.minan.game.model.permission.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 角色服务
 */
@Service
public class RoleService {
    
    @Autowired
    private RoleMapper roleMapper;
    
    @Autowired
    private UserRoleMapper userRoleMapper;
    
    public boolean createRole(Role role) {
        return roleMapper.insert(role) > 0;
    }
    
    public boolean assignUserRole(Long userId, Long roleId) {
        UserRole userRole = new UserRole();
        userRole.setUserId(userId);
        userRole.setRoleId(roleId);
        return userRoleMapper.insert(userRole) > 0;
    }
}