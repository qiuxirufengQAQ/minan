package com.minan.game.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minan.game.model.ConversationRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 对话记录 Mapper
 */
@Mapper
public interface ConversationRecordMapper extends BaseMapper<ConversationRecord> {

    /**
     * 根据对话 ID 获取记录
     */
    @Select("SELECT * FROM conversation_record WHERE conversation_id = #{conversationId} ORDER BY round_number ASC")
    List<ConversationRecord> selectByConversationId(String conversationId);

    /**
     * 根据场景和用户获取对话记录
     */
    @Select("SELECT * FROM conversation_record WHERE scene_id = #{sceneId} AND user_id = #{userId} ORDER BY created_at DESC")
    List<ConversationRecord> selectBySceneAndUser(String sceneId, String userId);
}
