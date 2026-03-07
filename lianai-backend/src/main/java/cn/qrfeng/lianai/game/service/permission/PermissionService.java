package cn.qrfeng.lianai.game.service.permission;

import cn.qrfeng.lianai.game.model.permission.Permission;
import cn.qrfeng.lianai.game.mapper.permission.PermissionMapper;
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