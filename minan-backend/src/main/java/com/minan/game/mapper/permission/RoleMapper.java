package com.minan.game.mapper.permission;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minan.game.model.permission.Role;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色 Mapper
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {
}