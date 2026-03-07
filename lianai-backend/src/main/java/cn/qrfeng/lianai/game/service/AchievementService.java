package cn.qrfeng.lianai.game.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.qrfeng.lianai.game.dto.AchievementQueryRequest;
import cn.qrfeng.lianai.game.dto.BasePageResponse;
import cn.qrfeng.lianai.game.mapper.AchievementMapper;
import cn.qrfeng.lianai.game.model.Achievement;
import cn.qrfeng.lianai.game.utils.IdGenerator;

@Service
public class AchievementService {

    @Autowired
    private AchievementMapper achievementMapper;

    /**
     * 获取所有成就列表（分页）
     */
    public BasePageResponse<Achievement> getAchievementsPage(AchievementQueryRequest request) {
        // 创建分页对象
        Page<Achievement> pageObj = new Page<>(request.getPage(), request.getPageSize());
        // 构建查询条件
        LambdaQueryWrapper<Achievement> queryWrapper = new LambdaQueryWrapper<>();
        // 如果有成就ID，添加查询条件
        if (request.getAchievementId() != null && !request.getAchievementId().isEmpty()) {
            queryWrapper.eq(Achievement::getAchievementId, request.getAchievementId());
        }
        // 执行分页查询
        Page<Achievement> resultPage = achievementMapper.selectPage(pageObj, queryWrapper);
        // 构建返回结果
        BasePageResponse<Achievement> result = new BasePageResponse<>();
        result.setRecords(resultPage.getRecords());
        result.setTotal(resultPage.getTotal());
        result.setCurrent(resultPage.getCurrent());
        result.setSize((int) resultPage.getSize());
        result.setPages(resultPage.getPages());
        return result;
    }

    /**
     * 根据ID获取成就详情
     */
    public Achievement getAchievementById(Long id) {
        return achievementMapper.selectById(id);
    }

    /**
     * 根据成就ID获取成就详情
     */
    public Achievement getAchievementByAchievementId(String achievementId) {
        LambdaQueryWrapper<Achievement> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Achievement::getAchievementId, achievementId);
        return achievementMapper.selectOne(queryWrapper);
    }

    /**
     * 新增成就
     */
    @Transactional
    public Achievement addAchievement(Achievement achievement) {
        // 生成唯一ID
        achievement.setAchievementId(IdGenerator.generateAchievementId());
        // 设置创建和更新时间
        LocalDateTime now = LocalDateTime.now();
        achievement.setCreatedAt(now);
        achievement.setUpdatedAt(now);
        // 插入数据
        achievementMapper.insert(achievement);
        return achievement;
    }

    /**
     * 更新成就
     */
    @Transactional
    public Achievement updateAchievement(Achievement achievement) {
        // 设置更新时间
        achievement.setUpdatedAt(LocalDateTime.now());
        // 更新数据
        achievementMapper.updateById(achievement);
        return achievement;
    }

    /**
     * 删除成就
     */
    @Transactional
    public boolean deleteAchievement(Long id) {
        return achievementMapper.deleteById(id) > 0;
    }

    /**
     * 根据成就ID删除成就
     */
    @Transactional
    public boolean deleteAchievementByAchievementId(String achievementId) {
        LambdaQueryWrapper<Achievement> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Achievement::getAchievementId, achievementId);
        return achievementMapper.delete(queryWrapper) > 0;
    }
}