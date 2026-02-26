package com.minan.game.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minan.game.mapper.SceneHintMapper;
import com.minan.game.model.SceneHint;
import com.minan.game.utils.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SceneHintService {

    @Autowired
    private SceneHintMapper sceneHintMapper;

    public List<SceneHint> listBySceneId(String sceneId) {
        QueryWrapper<SceneHint> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("scene_id", sceneId).orderByAsc("`order`");
        return sceneHintMapper.selectList(queryWrapper);
    }

    public Page<SceneHint> page(int pageNum, int pageSize, String sceneId) {
        Page<SceneHint> page = new Page<>(pageNum, pageSize);
        QueryWrapper<SceneHint> queryWrapper = new QueryWrapper<>();
        if (sceneId != null && !sceneId.isEmpty()) {
            queryWrapper.eq("scene_id", sceneId);
        }
        queryWrapper.orderByAsc("scene_id", "`order`");
        return sceneHintMapper.selectPage(page, queryWrapper);
    }

    public SceneHint getById(Long id) {
        return sceneHintMapper.selectById(id);
    }

    public boolean add(SceneHint hint) {
        hint.setHintId(IdGenerator.generateId("HINT"));
        hint.setIsUnlocked(0);
        return sceneHintMapper.insert(hint) > 0;
    }

    public boolean update(SceneHint hint) {
        return sceneHintMapper.updateById(hint) > 0;
    }

    public boolean delete(Long id) {
        return sceneHintMapper.deleteById(id) > 0;
    }
}
