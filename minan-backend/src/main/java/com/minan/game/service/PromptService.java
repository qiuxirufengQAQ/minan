package com.minan.game.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minan.game.dto.BasePageResponse;
import com.minan.game.dto.PromptQueryRequest;
import com.minan.game.mapper.PromptMapper;
import com.minan.game.model.Prompt;
import com.minan.game.utils.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PromptService {

    @Autowired
    private PromptMapper promptMapper;

    /**
     * 获取所有提示词列表（分页）
     */
    public BasePageResponse<Prompt> getPromptsPage(PromptQueryRequest request) {
        // 创建分页对象
        Page<Prompt> pageObj = new Page<>(request.getPage(), request.getPageSize());
        // 构建查询条件
        LambdaQueryWrapper<Prompt> queryWrapper = new LambdaQueryWrapper<>();
        // 如果有提示词ID，添加查询条件
        if (request.getPromptId() != null && !request.getPromptId().isEmpty()) {
            queryWrapper.eq(Prompt::getPromptId, request.getPromptId());
        }
        // 如果有关卡ID，添加查询条件
        if (request.getLevelId() != null && !request.getLevelId().isEmpty()) {
            queryWrapper.eq(Prompt::getLevelId, request.getLevelId());
        }
        // 如果有场景ID，添加查询条件
        if (request.getSceneId() != null && !request.getSceneId().isEmpty()) {
            queryWrapper.eq(Prompt::getSceneId, request.getSceneId());
        }
        // 如果有类型，添加查询条件
        if (request.getType() != null && !request.getType().isEmpty()) {
            queryWrapper.eq(Prompt::getType, request.getType());
        }
        // 执行分页查询
        Page<Prompt> resultPage = promptMapper.selectPage(pageObj, queryWrapper);
        // 构建返回结果
        BasePageResponse<Prompt> result = new BasePageResponse<>();
        result.setRecords(resultPage.getRecords());
        result.setTotal(resultPage.getTotal());
        result.setCurrent(resultPage.getCurrent());
        result.setSize((int) resultPage.getSize());
        result.setPages(resultPage.getPages());
        return result;
    }

    /**
     * 根据场景ID获取提示词列表
     */
    public List<Prompt> getPromptsBySceneId(String sceneId) {
        LambdaQueryWrapper<Prompt> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Prompt::getSceneId, sceneId);
        return promptMapper.selectList(queryWrapper);
    }
    
    /**
     * 根据场景ID和类型获取提示词
     */
    public Prompt getPromptBySceneIdAndType(String sceneId, String type) {
        LambdaQueryWrapper<Prompt> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Prompt::getSceneId, sceneId);
        queryWrapper.eq(Prompt::getType, type);
        return promptMapper.selectOne(queryWrapper);
    }

    /**
     * 根据ID获取提示词详情
     */
    public Prompt getPromptById(Long id) {
        return promptMapper.selectById(id);
    }

    /**
     * 根据提示词ID获取提示词详情
     */
    public Prompt getPromptByPromptId(String promptId) {
        LambdaQueryWrapper<Prompt> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Prompt::getPromptId, promptId);
        return promptMapper.selectOne(queryWrapper);
    }
    
    /**
     * 新增提示词
     */
    @Transactional
    public Prompt addPrompt(Prompt prompt) {
        // 验证关卡、场景和类型是否已存在提示词
        LambdaQueryWrapper<Prompt> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Prompt::getLevelId, prompt.getLevelId());
        queryWrapper.eq(Prompt::getSceneId, prompt.getSceneId());
        queryWrapper.eq(Prompt::getType, prompt.getType());
        Prompt existingPrompt = promptMapper.selectOne(queryWrapper);
        if (existingPrompt != null) {
            throw new RuntimeException("该关卡、场景和类型已存在提示词，无法重复添加");
        }
        // 生成唯一ID
        prompt.setPromptId(IdGenerator.generatePromptId());
        // 设置创建和更新时间
        LocalDateTime now = LocalDateTime.now();
        prompt.setCreatedAt(now);
        prompt.setUpdatedAt(now);
        // 插入数据
        promptMapper.insert(prompt);
        return prompt;
    }

    /**
     * 更新提示词
     */
    @Transactional
    public Prompt updatePrompt(Prompt prompt) {
        // 验证关卡、场景和类型是否已存在其他提示词
        LambdaQueryWrapper<Prompt> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Prompt::getLevelId, prompt.getLevelId());
        queryWrapper.eq(Prompt::getSceneId, prompt.getSceneId());
        queryWrapper.eq(Prompt::getType, prompt.getType());
        queryWrapper.ne(Prompt::getId, prompt.getId());
        Prompt existingPrompt = promptMapper.selectOne(queryWrapper);
        if (existingPrompt != null) {
            throw new RuntimeException("该关卡、场景和类型已存在其他提示词，无法更新");
        }
        // 设置更新时间
        prompt.setUpdatedAt(LocalDateTime.now());
        // 更新数据
        promptMapper.updateById(prompt);
        return prompt;
    }

    /**
     * 删除提示词
     */
    @Transactional
    public boolean deletePrompt(Long id) {
        return promptMapper.deleteById(id) > 0;
    }

    /**
     * 根据提示词ID删除提示词
     */
    @Transactional
    public boolean deletePromptByPromptId(String promptId) {
        LambdaQueryWrapper<Prompt> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Prompt::getPromptId, promptId);
        return promptMapper.delete(queryWrapper) > 0;
    }

    /**
     * 根据场景ID删除提示词
     */
    @Transactional
    public boolean deletePromptsBySceneId(String sceneId) {
        LambdaQueryWrapper<Prompt> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Prompt::getSceneId, sceneId);
        return promptMapper.delete(queryWrapper) > 0;
    }
}