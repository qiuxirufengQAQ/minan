package cn.qrfeng.lianai.game.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.qrfeng.lianai.game.mapper.SceneHintMapper;
import cn.qrfeng.lianai.game.model.SceneHint;
import cn.qrfeng.lianai.game.utils.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SceneHintService {

    @Autowired
    private SceneHintMapper sceneHintMapper;

    public List<SceneHint> listAll() {
        QueryWrapper<SceneHint> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("`order`");
        return sceneHintMapper.selectList(queryWrapper);
    }

    public Page<SceneHint> page(int pageNum, int pageSize, String hintType) {
        Page<SceneHint> page = new Page<>(pageNum, pageSize);
        QueryWrapper<SceneHint> queryWrapper = new QueryWrapper<>();
        if (hintType != null && !hintType.isEmpty()) {
            queryWrapper.eq("hint_type", hintType);
        }
        queryWrapper.orderByAsc("`order`");
        return sceneHintMapper.selectPage(page, queryWrapper);
    }

    public Page<SceneHint> page(int pageNum, int pageSize) {
        return page(pageNum, pageSize, null);
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
