package cn.qrfeng.lianai.game.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.qrfeng.lianai.game.dto.BasePageResponse;
import cn.qrfeng.lianai.game.dto.PromptQueryRequest;
import cn.qrfeng.lianai.game.mapper.PromptMapper;
import cn.qrfeng.lianai.game.model.Prompt;
import cn.qrfeng.lianai.game.utils.IdGenerator;
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
     * 根据场景ID获取提示词
     */
    public Prompt getPromptBySceneId(String sceneId) {
        LambdaQueryWrapper<Prompt> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Prompt::getSceneId, sceneId);
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
    public Prompt addPrompt(Prompt prompt) {
        // 验证参数
        if (prompt == null) {
            throw new IllegalArgumentException("提示词信息不能为空");
        }
        if (prompt.getSceneId() == null || prompt.getSceneId().isEmpty()) {
            throw new IllegalArgumentException("场景ID不能为空");
        }
        if (prompt.getLevelId() == null || prompt.getLevelId().isEmpty()) {
            throw new IllegalArgumentException("关卡ID不能为空");
        }
        if (prompt.getStartTemplate() == null || prompt.getStartTemplate().isEmpty()) {
            throw new IllegalArgumentException("开始提示词模板不能为空");
        }
        
        // 验证场景是否已经存在提示词
        Prompt existingPrompt = getPromptBySceneId(prompt.getSceneId());
        if (existingPrompt != null) {
            throw new IllegalArgumentException("该场景已存在提示词，请使用编辑功能");
        }
        
        // 设置提示词ID
        String promptId = IdGenerator.generateId();
        prompt.setPromptId(promptId);
        
        // 设置创建时间
        prompt.setCreatedAt(LocalDateTime.now());
        prompt.setUpdatedAt(LocalDateTime.now());
        
        // 保存提示词
        promptMapper.insert(prompt);
        return prompt;
    }

    /**
     * 更新提示词
     */
    public Prompt updatePrompt(Prompt prompt) {
        // 验证参数
        if (prompt == null) {
            throw new IllegalArgumentException("提示词信息不能为空");
        }
        if (prompt.getId() == null) {
            throw new IllegalArgumentException("提示词ID不能为空");
        }
        if (prompt.getStartTemplate() == null || prompt.getStartTemplate().isEmpty()) {
            throw new IllegalArgumentException("开始提示词模板不能为空");
        }
        
        // 获取原提示词
        Prompt existingPrompt = promptMapper.selectById(prompt.getId());
        if (existingPrompt == null) {
            throw new IllegalArgumentException("提示词不存在");
        }
        
        // 设置更新时间
        prompt.setUpdatedAt(LocalDateTime.now());
        
        // 更新提示词
        promptMapper.updateById(prompt);
        return prompt;
    }

    /**
     * 删除提示词
     */
    public boolean deletePrompt(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("提示词ID不能为空");
        }
        return promptMapper.deleteById(id) > 0;
    }

    /**
     * 根据提示词ID删除提示词
     */
    public boolean deletePromptByPromptId(String promptId) {
        if (promptId == null || promptId.isEmpty()) {
            throw new IllegalArgumentException("提示词ID不能为空");
        }
        LambdaQueryWrapper<Prompt> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Prompt::getPromptId, promptId);
        return promptMapper.delete(queryWrapper) > 0;
    }

    /**
     * 根据场景ID删除提示词
     */
    @Transactional
    public boolean deletePromptsBySceneId(String sceneId) {
        if (sceneId == null || sceneId.isEmpty()) {
            throw new IllegalArgumentException("场景ID不能为空");
        }
        LambdaQueryWrapper<Prompt> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Prompt::getSceneId, sceneId);
        return promptMapper.delete(queryWrapper) > 0;
    }
}