package com.minan.game.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minan.game.model.KnowledgeCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface KnowledgeCategoryMapper extends BaseMapper<KnowledgeCategory> {

    @Select("SELECT * FROM knowledge_category WHERE parent_id IS NULL ORDER BY `order`")
    List<KnowledgeCategory> selectRootCategories();

    @Select("SELECT * FROM knowledge_category WHERE parent_id = #{parentId} ORDER BY `order`")
    List<KnowledgeCategory> selectByParentId(String parentId);

    @Select("SELECT * FROM knowledge_category WHERE level = #{level} ORDER BY `order`")
    List<KnowledgeCategory> selectByLevel(Integer level);
}
