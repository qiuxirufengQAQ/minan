package com.minan.game.service;

import com.minan.game.mapper.KnowledgeCategoryMapper;
import com.minan.game.model.KnowledgeCategory;
import com.minan.game.utils.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class KnowledgeCategoryService {

    @Autowired
    private KnowledgeCategoryMapper categoryMapper;

    public List<KnowledgeCategory> listAll() {
        return categoryMapper.selectList(null);
    }

    public List<KnowledgeCategory> listTree() {
        List<KnowledgeCategory> roots = categoryMapper.selectRootCategories();
        for (KnowledgeCategory root : roots) {
            buildChildren(root);
        }
        return roots;
    }

    private void buildChildren(KnowledgeCategory category) {
        List<KnowledgeCategory> children = categoryMapper.selectByParentId(category.getCategoryId());
        if (children != null && !children.isEmpty()) {
            category.setChildren(children);
            for (KnowledgeCategory child : children) {
                buildChildren(child);
            }
        }
    }

    public List<KnowledgeCategory> listByLevel(Integer level) {
        return categoryMapper.selectByLevel(level);
    }

    public List<KnowledgeCategory> listByParentId(String parentId) {
        return categoryMapper.selectByParentId(parentId);
    }

    public KnowledgeCategory getById(Long id) {
        return categoryMapper.selectById(id);
    }

    public KnowledgeCategory getByCategoryId(String categoryId) {
        return categoryMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<KnowledgeCategory>()
                .eq("category_id", categoryId)
        );
    }

    public boolean add(KnowledgeCategory category) {
        category.setCategoryId(IdGenerator.generateCategoryId());
        return categoryMapper.insert(category) > 0;
    }

    public boolean update(KnowledgeCategory category) {
        return categoryMapper.updateById(category) > 0;
    }

    public boolean delete(Long id) {
        return categoryMapper.deleteById(id) > 0;
    }
}
