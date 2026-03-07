package cn.qrfeng.lianai.game.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.qrfeng.lianai.game.model.KnowledgePoint;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface KnowledgePointMapper extends BaseMapper<KnowledgePoint> {

    List<KnowledgePoint> selectByCategoryId(String categoryId);

    List<KnowledgePoint> selectAllWithCategoryName();

    KnowledgePoint selectByIdWithCategoryName(String pointId);

    Page<KnowledgePoint> selectPageWithCategoryName(
        Page<KnowledgePoint> page,
        @Param("categoryId") String categoryId,
        @Param("difficulty") Integer difficulty
    );
}
