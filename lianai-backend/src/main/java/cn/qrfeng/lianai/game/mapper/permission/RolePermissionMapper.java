package cn.qrfeng.lianai.game.mapper.permission;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.qrfeng.lianai.game.model.permission.RolePermission;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色权限关联 Mapper
 */
@Mapper
public interface RolePermissionMapper extends BaseMapper<RolePermission> {
}