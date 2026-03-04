package com.minan.game.mapper.permission;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minan.game.model.permission.UserRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户角色关联 Mapper
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {
}