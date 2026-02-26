package com.minan.game.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minan.game.model.KnowledgeQuiz;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface KnowledgeQuizMapper extends BaseMapper<KnowledgeQuiz> {

    List<KnowledgeQuiz> selectByPointId(String pointId);

    List<KnowledgeQuiz> selectAllWithPointName();

    Page<KnowledgeQuiz> selectPageWithPointName(
        Page<KnowledgeQuiz> page,
        @Param("pointId") String pointId
    );
}
