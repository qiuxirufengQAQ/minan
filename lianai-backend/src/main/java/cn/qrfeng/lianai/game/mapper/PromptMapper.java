package cn.qrfeng.lianai.game.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.qrfeng.lianai.game.model.Prompt;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PromptMapper extends BaseMapper<Prompt> {
}