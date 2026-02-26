package com.minan.game.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minan.game.dto.BasePageResponse;
import com.minan.game.dto.SceneQueryRequest;
import com.minan.game.mapper.SceneMapper;
import com.minan.game.model.Scene;
import com.minan.game.utils.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SceneService {

    @Autowired
    private SceneMapper sceneMapper;

    /**
     * 获取所有场景列表（分页）
     */
    public BasePageResponse<Scene> getScenesPage(SceneQueryRequest request) {
        // 创建分页对象
        Page<Scene> pageObj = new Page<>(request.getPage(), request.getPageSize());
        // 构建查询条件
        LambdaQueryWrapper<Scene> queryWrapper = new LambdaQueryWrapper<>();
        // 如果有场景ID，添加查询条件
        if (request.getSceneId() != null && !request.getSceneId().isEmpty()) {
            queryWrapper.eq(Scene::getSceneId, request.getSceneId());
        }
        // 如果有关卡ID，添加查询条件
        if (request.getLevelId() != null && !request.getLevelId().isEmpty()) {
            queryWrapper.eq(Scene::getLevelId, request.getLevelId());
        }
        queryWrapper.orderByAsc(Scene::getOrder);
        // 执行分页查询
        Page<Scene> resultPage = sceneMapper.selectPage(pageObj, queryWrapper);
        // 构建返回结果
        BasePageResponse<Scene> result = new BasePageResponse<>();
        result.setRecords(resultPage.getRecords());
        result.setTotal(resultPage.getTotal());
        result.setCurrent(resultPage.getCurrent());
        result.setSize((int) resultPage.getSize());
        result.setPages(resultPage.getPages());
        return result;
    }

    /**
     * 根据关卡ID获取场景列表
     */
    public List<Scene> getScenesByLevelId(String levelId) {
        LambdaQueryWrapper<Scene> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Scene::getLevelId, levelId)
                .orderByAsc(Scene::getOrder);
        return sceneMapper.selectList(queryWrapper);
    }

    /**
     * 根据ID获取场景详情
     */
    public Scene getSceneById(Long id) {
        return sceneMapper.selectById(id);
    }

    /**
     * 根据场景ID获取场景详情
     */
    public Scene getSceneBySceneId(String sceneId) {
        LambdaQueryWrapper<Scene> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Scene::getSceneId, sceneId);
        return sceneMapper.selectOne(queryWrapper);
    }

    /**
     * 新增场景
     */
    @Transactional
    public Scene addScene(Scene scene) {
        // 生成唯一ID
        scene.setSceneId(IdGenerator.generateSceneId());
        // 设置创建和更新时间
        LocalDateTime now = LocalDateTime.now();
        scene.setCreatedAt(now);
        scene.setUpdatedAt(now);
        // 插入数据
        sceneMapper.insert(scene);
        return scene;
    }

    /**
     * 更新场景
     */
    @Transactional
    public Scene updateScene(Scene scene) {
        // 设置更新时间
        scene.setUpdatedAt(LocalDateTime.now());
        // 更新数据
        sceneMapper.updateById(scene);
        return scene;
    }

    /**
     * 删除场景
     */
    @Transactional
    public boolean deleteScene(Long id) {
        return sceneMapper.deleteById(id) > 0;
    }

    /**
     * 根据场景ID删除场景
     */
    @Transactional
    public boolean deleteSceneBySceneId(String sceneId) {
        LambdaQueryWrapper<Scene> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Scene::getSceneId, sceneId);
        return sceneMapper.delete(queryWrapper) > 0;
    }

    /**
     * 根据关卡ID删除场景
     */
    @Transactional
    public boolean deleteScenesByLevelId(String levelId) {
        LambdaQueryWrapper<Scene> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Scene::getLevelId, levelId);
        return sceneMapper.delete(queryWrapper) > 0;
    }
}