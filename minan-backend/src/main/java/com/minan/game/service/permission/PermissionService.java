package com.minan.game.service.permission;

import com.minan.game.model.permission.Permission;
import com.minan.game.mapper.permission.PermissionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 权限服务
 */
@Service
public class PermissionService {
    
    @Autowired
    private PermissionMapper permissionMapper;
    
    public boolean createPermission(Permission permission) {
        return permissionMapper.insert(permission) > 0;
    }
}