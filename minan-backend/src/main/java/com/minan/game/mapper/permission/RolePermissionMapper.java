package com.minan.game.mapper.permission;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minan.game.model.permission.RolePermission;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色权限关联 Mapper
 */
@Mapper
public interface RolePermissionMapper extends BaseMapper<RolePermission> {
}