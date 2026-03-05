package com.minan.game.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minan.game.model.Evaluation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EvaluationMapper extends BaseMapper<Evaluation> {

    /**
     * 根据 evaluationId 查询
     */
    @Select("SELECT * FROM evaluation WHERE evaluation_id = #{evaluationId}")
    Evaluation selectByEvaluationId(String evaluationId);
}