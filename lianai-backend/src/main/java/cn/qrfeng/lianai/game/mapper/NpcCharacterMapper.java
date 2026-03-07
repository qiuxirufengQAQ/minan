package cn.qrfeng.lianai.game.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.qrfeng.lianai.game.model.NpcCharacter;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface NpcCharacterMapper extends BaseMapper<NpcCharacter> {

    /**
     * 根据 NPC ID 查询
     */
    @Select("SELECT * FROM npc_character WHERE npc_id = #{npcId}")
    NpcCharacter selectByNpcId(String npcId);
}
