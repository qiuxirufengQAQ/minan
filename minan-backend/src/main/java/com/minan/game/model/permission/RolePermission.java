package com.minan.game.model.permission;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 角色权限关联表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("role_permission")
public class RolePermission {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /** 角色ID */
    private Long roleId;
    
    /** 权限ID */
    private Long permissionId;
}