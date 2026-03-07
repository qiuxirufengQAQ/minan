package cn.qrfeng.lianai.game.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.qrfeng.lianai.game.mapper.DailyTaskMapper;
import cn.qrfeng.lianai.game.model.DailyTask;
import cn.qrfeng.lianai.game.utils.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DailyTaskService {

    @Autowired
    private DailyTaskMapper dailyTaskMapper;

    public List<DailyTask> listAllActive() {
        QueryWrapper<DailyTask> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_active", 1);
        return dailyTaskMapper.selectList(queryWrapper);
    }

    public Page<DailyTask> page(int pageNum, int pageSize) {
        Page<DailyTask> page = new Page<>(pageNum, pageSize);
        QueryWrapper<DailyTask> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("id");
        return dailyTaskMapper.selectPage(page, queryWrapper);
    }

    public DailyTask getById(Long id) {
        return dailyTaskMapper.selectById(id);
    }

    public DailyTask getByTaskId(String taskId) {
        QueryWrapper<DailyTask> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("task_id", taskId);
        return dailyTaskMapper.selectOne(queryWrapper);
    }

    public boolean add(DailyTask task) {
        task.setTaskId(IdGenerator.generateId("TASK"));
        task.setIsActive(1);
        return dailyTaskMapper.insert(task) > 0;
    }

    public boolean update(DailyTask task) {
        return dailyTaskMapper.updateById(task) > 0;
    }

    public boolean delete(Long id) {
        return dailyTaskMapper.deleteById(id) > 0;
    }
}
