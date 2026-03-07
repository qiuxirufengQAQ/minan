package cn.qrfeng.lianai.game.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.qrfeng.lianai.game.mapper.KnowledgeQuizMapper;
import cn.qrfeng.lianai.game.model.KnowledgeQuiz;
import cn.qrfeng.lianai.game.utils.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KnowledgeQuizService {

    @Autowired
    private KnowledgeQuizMapper quizMapper;

    public Page<KnowledgeQuiz> page(int pageNum, int pageSize, String pointId) {
        Page<KnowledgeQuiz> page = new Page<>(pageNum, pageSize);
        return quizMapper.selectPageWithPointName(page, pointId);
    }

    public List<KnowledgeQuiz> listAll() {
        return quizMapper.selectAllWithPointName();
    }

    public List<KnowledgeQuiz> listByPointId(String pointId) {
        return quizMapper.selectByPointId(pointId);
    }

    public KnowledgeQuiz getById(Long id) {
        return quizMapper.selectById(id);
    }

    public KnowledgeQuiz getByQuizId(String quizId) {
        return quizMapper.selectOne(
            new QueryWrapper<KnowledgeQuiz>().eq("quiz_id", quizId)
        );
    }

    public boolean add(KnowledgeQuiz quiz) {
        quiz.setQuizId(IdGenerator.generateQuizId());
        return quizMapper.insert(quiz) > 0;
    }

    public boolean update(KnowledgeQuiz quiz) {
        return quizMapper.updateById(quiz) > 0;
    }

    public boolean delete(Long id) {
        return quizMapper.deleteById(id) > 0;
    }
}
