package com.minan.game.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minan.game.mapper.LearningResourceMapper;
import com.minan.game.model.LearningResource;
import com.minan.game.utils.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LearningResourceService {

    @Autowired
    private LearningResourceMapper learningResourceMapper;

    public Page<LearningResource> page(int pageNum, int pageSize) {
        Page<LearningResource> page = new Page<>(pageNum, pageSize);
        QueryWrapper<LearningResource> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("created_at");
        return learningResourceMapper.selectPage(page, queryWrapper);
    }

    public Page<LearningResource> pageByPointId(int pageNum, int pageSize, String pointId) {
        Page<LearningResource> page = new Page<>(pageNum, pageSize);
        QueryWrapper<LearningResource> queryWrapper = new QueryWrapper<>();
        if (pointId != null && !pointId.isEmpty()) {
            queryWrapper.eq("point_id", pointId);
        }
        queryWrapper.orderByAsc("`order`");
        return learningResourceMapper.selectPage(page, queryWrapper);
    }

    public List<LearningResource> listByPointId(String pointId) {
        QueryWrapper<LearningResource> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("point_id", pointId);
        queryWrapper.orderByAsc("`order`");
        return learningResourceMapper.selectList(queryWrapper);
    }

    public LearningResource getById(Long id) {
        return learningResourceMapper.selectById(id);
    }

    public boolean add(LearningResource resource) {
        resource.setResourceId(IdGenerator.generateResourceId());
        resource.setIsRequired(1);
        return learningResourceMapper.insert(resource) > 0;
    }

    public boolean update(LearningResource resource) {
        return learningResourceMapper.updateById(resource) > 0;
    }

    public boolean delete(Long id) {
        return learningResourceMapper.deleteById(id) > 0;
    }

    public List<LearningResource> getByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        QueryWrapper<LearningResource> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", ids);
        return learningResourceMapper.selectList(queryWrapper);
    }

    public List<LearningResource> getByResourceIds(List<String> resourceIds) {
        if (resourceIds == null || resourceIds.isEmpty()) {
            return List.of();
        }
        QueryWrapper<LearningResource> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("resource_id", resourceIds);
        return learningResourceMapper.selectList(queryWrapper);
    }
}
