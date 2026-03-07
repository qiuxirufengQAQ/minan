package cn.qrfeng.lianai.game.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import cn.qrfeng.lianai.game.mapper.UserNpcRelationMapper;
import cn.qrfeng.lianai.game.model.UserNpcRelation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserNpcRelationService {

    @Autowired
    private UserNpcRelationMapper userNpcRelationMapper;

    public UserNpcRelation getByUserAndNpc(String userId, String npcId) {
        QueryWrapper<UserNpcRelation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).eq("npc_id", npcId);
        return userNpcRelationMapper.selectOne(queryWrapper);
    }

    public List<UserNpcRelation> listByUserId(String userId) {
        QueryWrapper<UserNpcRelation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).orderByDesc("intimacy_score");
        return userNpcRelationMapper.selectList(queryWrapper);
    }

    public boolean initRelation(String userId, String npcId) {
        UserNpcRelation relation = getByUserAndNpc(userId, npcId);
        if (relation != null) {
            return true;
        }
        relation = new UserNpcRelation();
        relation.setUserId(userId);
        relation.setNpcId(npcId);
        relation.setIntimacyScore(0);
        relation.setInteractionCount(0);
        relation.setUnlockedSceneLevel(1);
        return userNpcRelationMapper.insert(relation) > 0;
    }

    public boolean addIntimacyScore(String userId, String npcId, int score) {
        UserNpcRelation relation = getByUserAndNpc(userId, npcId);
        if (relation == null) {
            initRelation(userId, npcId);
            relation = getByUserAndNpc(userId, npcId);
        }
        relation.setIntimacyScore(relation.getIntimacyScore() + score);
        relation.setInteractionCount(relation.getInteractionCount() + 1);
        return userNpcRelationMapper.updateById(relation) > 0;
    }

    public boolean updateUnlockedSceneLevel(String userId, String npcId, int level) {
        UserNpcRelation relation = getByUserAndNpc(userId, npcId);
        if (relation == null) {
            return false;
        }
        if (level > relation.getUnlockedSceneLevel()) {
            relation.setUnlockedSceneLevel(level);
            return userNpcRelationMapper.updateById(relation) > 0;
        }
        return true;
    }

    public boolean canUnlockScene(String userId, String npcId, int requiredScore) {
        UserNpcRelation relation = getByUserAndNpc(userId, npcId);
        if (relation == null) {
            return requiredScore == 0;
        }
        return relation.getIntimacyScore() >= requiredScore;
    }
}
