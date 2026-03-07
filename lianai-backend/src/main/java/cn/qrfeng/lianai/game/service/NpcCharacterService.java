package cn.qrfeng.lianai.game.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.qrfeng.lianai.game.mapper.NpcCharacterMapper;
import cn.qrfeng.lianai.game.model.NpcCharacter;
import cn.qrfeng.lianai.game.utils.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NpcCharacterService {

    @Autowired
    private NpcCharacterMapper npcCharacterMapper;

    public List<NpcCharacter> listAll() {
        QueryWrapper<NpcCharacter> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_active", 1).orderByAsc("id");
        return npcCharacterMapper.selectList(queryWrapper);
    }

    public Page<NpcCharacter> page(int pageNum, int pageSize) {
        Page<NpcCharacter> page = new Page<>(pageNum, pageSize);
        QueryWrapper<NpcCharacter> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("id");
        return npcCharacterMapper.selectPage(page, queryWrapper);
    }

    public NpcCharacter getById(Long id) {
        return npcCharacterMapper.selectById(id);
    }

    public NpcCharacter getByNpcId(String npcId) {
        QueryWrapper<NpcCharacter> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("npc_id", npcId);
        return npcCharacterMapper.selectOne(queryWrapper);
    }

    public boolean add(NpcCharacter npc) {
        npc.setNpcId(IdGenerator.generateId("NPC"));
        npc.setIsActive(1);
        return npcCharacterMapper.insert(npc) > 0;
    }

    public boolean update(NpcCharacter npc) {
        return npcCharacterMapper.updateById(npc) > 0;
    }

    public boolean delete(Long id) {
        return npcCharacterMapper.deleteById(id) > 0;
    }
}
