package cn.qrfeng.lianai.game.mapper.prompt;

import cn.qrfeng.lianai.game.model.prompt.PromptTemplate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 提示词模板 Mapper
 */
@Mapper
public interface PromptTemplateMapper {

    /**
     * 根据类型和版本查询模板
     */
    PromptTemplate selectByTypeAndVersion(
        @Param("templateType") String templateType,
        @Param("version") Integer version
    );

    /**
     * 查询最新版本的模板
     */
    PromptTemplate selectLatest(@Param("templateType") String templateType);

    /**
     * 插入新模板
     */
    int insert(PromptTemplate template);

    /**
     * 更新模板
     */
    int update(PromptTemplate template);

    /**
     * 删除模板
     */
    int deleteById(@Param("id") Long id);

    /**
     * 查询所有模板（按类型分组）
     */
    List<PromptTemplate> selectAll();
}
