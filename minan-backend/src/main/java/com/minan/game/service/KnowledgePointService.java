package com.minan.game.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minan.game.mapper.KnowledgePointMapper;
import com.minan.game.model.KnowledgePoint;
import com.minan.game.utils.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KnowledgePointService {

    @Autowired
    private KnowledgePointMapper pointMapper;

    public Page<KnowledgePoint> page(int pageNum, int pageSize, String categoryId, Integer difficulty) {
        Page<KnowledgePoint> page = new Page<>(pageNum, pageSize);
        return pointMapper.selectPageWithCategoryName(page, categoryId, difficulty);
    }

    public List<KnowledgePoint> listAll() {
        return pointMapper.selectAllWithCategoryName();
    }

    public List<KnowledgePoint> listByCategoryId(String categoryId) {
        return pointMapper.selectByCategoryId(categoryId);
    }

    public KnowledgePoint getById(Long id) {
        return pointMapper.selectById(id);
    }

    public KnowledgePoint getByPointId(String pointId) {
        return pointMapper.selectByIdWithCategoryName(pointId);
    }

    public boolean add(KnowledgePoint point) {
        point.setPointId(IdGenerator.generatePointId());
        return pointMapper.insert(point) > 0;
    }

    public boolean update(KnowledgePoint point) {
        return pointMapper.updateById(point) > 0;
    }

    public boolean delete(Long id) {
        return pointMapper.deleteById(id) > 0;
    }

    public List<KnowledgePoint> getByIds(List<String> pointIds) {
        if (pointIds == null || pointIds.isEmpty()) {
            return List.of();
        }
        QueryWrapper<KnowledgePoint> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("point_id", pointIds);
        List<KnowledgePoint> points = pointMapper.selectList(queryWrapper);
        for (KnowledgePoint point : points) {
            KnowledgePoint fullPoint = pointMapper.selectByIdWithCategoryName(point.getPointId());
            point.setCategoryName(fullPoint.getCategoryName());
        }
        return points;
    }
}
