package com.minan.game.mapper.permission;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minan.game.model.permission.Permission;
import org.apache.ibatis.annotations.Mapper;

/**
 * 权限 Mapper
 */
@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {
}