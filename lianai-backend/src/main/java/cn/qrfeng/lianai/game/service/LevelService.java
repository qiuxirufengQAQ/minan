package cn.qrfeng.lianai.game.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import cn.qrfeng.lianai.game.mapper.LevelMapper;
import cn.qrfeng.lianai.game.model.Level;
import cn.qrfeng.lianai.game.utils.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.qrfeng.lianai.game.dto.BasePageResponse;
import cn.qrfeng.lianai.game.dto.LevelQueryRequest;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LevelService {

    @Autowired
    private LevelMapper levelMapper;

    /**
     * 获取所有关卡列表（分页）
     */
    public BasePageResponse<Level> getLevelsPage(LevelQueryRequest request) {
        // 从请求对象中获取参数
        Integer page = request.getPage();
        Integer pageSize = request.getPageSize();
        String levelId = request.getLevelId();
        
        // 创建分页对象
        Page<Level> pageObj = new Page<>(page, pageSize);
        // 构建查询条件
        LambdaQueryWrapper<Level> queryWrapper = new LambdaQueryWrapper<>();
        
        // 如果提供了levelId，则添加筛选条件
        if (levelId != null && !levelId.isEmpty()) {
            queryWrapper.eq(Level::getLevelId, levelId);
        }
        
        queryWrapper.orderByAsc(Level::getOrder);
        // 执行分页查询
        Page<Level> resultPage = levelMapper.selectPage(pageObj, queryWrapper);
        // 构建返回结果
        BasePageResponse<Level> response = new BasePageResponse<>();
        response.setTotal(resultPage.getTotal());
        response.setPages(resultPage.getPages());
        response.setCurrent(resultPage.getCurrent());
        response.setSize((int) resultPage.getSize());
        response.setRecords(resultPage.getRecords());
        return response;
    }

    /**
     * 根据ID获取关卡详情
     */
    public Level getLevelById(Long id) {
        return levelMapper.selectById(id);
    }

    /**
     * 根据关卡ID获取关卡详情
     */
    public Level getLevelByLevelId(String levelId) {
        LambdaQueryWrapper<Level> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Level::getLevelId, levelId);
        return levelMapper.selectOne(queryWrapper);
    }

    /**
     * 新增关卡
     */
    @Transactional
    public Level addLevel(Level level) {
        // 生成唯一ID
        level.setLevelId(IdGenerator.generateLevelId());
        // 设置创建和更新时间
        LocalDateTime now = LocalDateTime.now();
        level.setCreatedAt(now);
        level.setUpdatedAt(now);
        // 插入数据
        levelMapper.insert(level);
        return level;
    }

    /**
     * 更新关卡
     */
    @Transactional
    public Level updateLevel(Level level) {
        // 设置更新时间
        level.setUpdatedAt(LocalDateTime.now());
        // 更新数据
        levelMapper.updateById(level);
        return level;
    }

    /**
     * 删除关卡
     */
    @Transactional
    public boolean deleteLevel(Long id) {
        return levelMapper.deleteById(id) > 0;
    }

    /**
     * 根据关卡ID删除关卡
     */
    @Transactional
    public boolean deleteLevelByLevelId(String levelId) {
        LambdaQueryWrapper<Level> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Level::getLevelId, levelId);
        return levelMapper.delete(queryWrapper) > 0;
    }
}