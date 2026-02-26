package com.minan.game.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minan.game.mapper.EvaluationDimensionMapper;
import com.minan.game.model.EvaluationDimension;
import com.minan.game.utils.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EvaluationDimensionService {

    @Autowired
    private EvaluationDimensionMapper evaluationDimensionMapper;

    public List<EvaluationDimension> listBySceneId(String sceneId) {
        QueryWrapper<EvaluationDimension> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("scene_id", sceneId).orderByAsc("`order`");
        return evaluationDimensionMapper.selectList(queryWrapper);
    }

    public Page<EvaluationDimension> page(int pageNum, int pageSize, String sceneId) {
        Page<EvaluationDimension> page = new Page<>(pageNum, pageSize);
        QueryWrapper<EvaluationDimension> queryWrapper = new QueryWrapper<>();
        if (sceneId != null && !sceneId.isEmpty()) {
            queryWrapper.eq("scene_id", sceneId);
        }
        queryWrapper.orderByAsc("scene_id", "`order`");
        return evaluationDimensionMapper.selectPage(page, queryWrapper);
    }

    public EvaluationDimension getById(Long id) {
        return evaluationDimensionMapper.selectById(id);
    }

    public boolean add(EvaluationDimension dimension) {
        dimension.setDimensionId(IdGenerator.generateId("DIM"));
        return evaluationDimensionMapper.insert(dimension) > 0;
    }

    public boolean update(EvaluationDimension dimension) {
        return evaluationDimensionMapper.updateById(dimension) > 0;
    }

    public boolean delete(Long id) {
        return evaluationDimensionMapper.deleteById(id) > 0;
    }
}
