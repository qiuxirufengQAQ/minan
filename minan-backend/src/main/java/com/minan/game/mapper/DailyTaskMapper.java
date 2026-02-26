package com.minan.game.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minan.game.model.DailyTask;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DailyTaskMapper extends BaseMapper<DailyTask> {
}
