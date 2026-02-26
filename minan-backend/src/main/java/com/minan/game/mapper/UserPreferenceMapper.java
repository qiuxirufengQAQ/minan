package com.minan.game.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minan.game.model.UserPreference;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserPreferenceMapper extends BaseMapper<UserPreference> {
}