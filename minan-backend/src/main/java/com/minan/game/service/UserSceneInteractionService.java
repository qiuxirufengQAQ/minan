package com.minan.game.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.minan.game.mapper.UserSceneInteractionMapper;
import com.minan.game.model.UserSceneInteraction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserSceneInteractionService {

    @Autowired
    private UserSceneInteractionMapper userSceneInteractionMapper;

    @Autowired
    private UserNpcRelationService userNpcRelationService;

    public UserSceneInteraction getByUserNpcScene(String userId, String npcId, String sceneId) {
        QueryWrapper<UserSceneInteraction> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .eq("npc_id", npcId)
                   .eq("scene_id", sceneId)
                   .orderByDesc("score")
                   .last("LIMIT 1");
        return userSceneInteractionMapper.selectOne(queryWrapper);
    }

    public List<UserSceneInteraction> listByUserNpcScene(String userId, String npcId, String sceneId) {
        QueryWrapper<UserSceneInteraction> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .eq("npc_id", npcId)
                   .eq("scene_id", sceneId)
                   .orderByDesc("created_at");
        return userSceneInteractionMapper.selectList(queryWrapper);
    }

    public UserSceneInteraction getBestScore(String userId, String npcId, String sceneId) {
        QueryWrapper<UserSceneInteraction> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .eq("npc_id", npcId)
                   .eq("scene_id", sceneId)
                   .orderByDesc("score")
                   .last("LIMIT 1");
        return userSceneInteractionMapper.selectOne(queryWrapper);
    }

    public boolean hasCompleted(String userId, String npcId, String sceneId) {
        QueryWrapper<UserSceneInteraction> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .eq("npc_id", npcId)
                   .eq("scene_id", sceneId);
        return userSceneInteractionMapper.selectCount(queryWrapper) > 0;
    }

    public boolean saveInteraction(String userId, String npcId, String sceneId, Integer score, Integer intimacyGained, String userInput) {
        UserSceneInteraction interaction = new UserSceneInteraction();
        interaction.setUserId(userId);
        interaction.setNpcId(npcId);
        interaction.setSceneId(sceneId);
        interaction.setScore(score);
        interaction.setIntimacyGained(intimacyGained);
        interaction.setUserInput(userInput);
        interaction.setCreatedAt(LocalDateTime.now());
        interaction.setUpdatedAt(LocalDateTime.now());
        
        if (userSceneInteractionMapper.insert(interaction) > 0) {
            UserSceneInteraction bestRecord = getBestScore(userId, npcId, sceneId);
            if (bestRecord != null && bestRecord.getId().equals(interaction.getId())) {
                userNpcRelationService.addIntimacyScore(userId, npcId, intimacyGained);
            }
            return true;
        }
        return false;
    }
}
