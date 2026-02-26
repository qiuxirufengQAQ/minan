package com.minan.game.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minan.game.dto.BasePageResponse;
import com.minan.game.dto.Response;
import com.minan.game.model.KnowledgeQuiz;
import com.minan.game.service.KnowledgeQuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/knowledge-quizzes")
public class KnowledgeQuizController {

    @Autowired
    private KnowledgeQuizService quizService;

    @PostMapping("/page")
    public Response<BasePageResponse<KnowledgeQuiz>> page(@RequestBody PageRequest request) {
        Page<KnowledgeQuiz> page = quizService.page(request.getPage(), request.getPageSize(), request.getPointId());
        BasePageResponse<KnowledgeQuiz> response = new BasePageResponse<>();
        response.setRecords(page.getRecords());
        response.setTotal(page.getTotal());
        response.setCurrent(page.getCurrent());
        response.setSize((int) page.getSize());
        return Response.success(response);
    }

    @GetMapping("/list")
    public Response<List<KnowledgeQuiz>> list() {
        return Response.success(quizService.listAll());
    }

    @GetMapping("/point/{pointId}")
    public Response<List<KnowledgeQuiz>> listByPointId(@PathVariable String pointId) {
        return Response.success(quizService.listByPointId(pointId));
    }

    @GetMapping("/{id}")
    public Response<KnowledgeQuiz> getById(@PathVariable Long id) {
        return Response.success(quizService.getById(id));
    }

    @PostMapping("/add")
    public Response<Boolean> add(@RequestBody KnowledgeQuiz quiz) {
        return Response.success(quizService.add(quiz));
    }

    @PostMapping("/update")
    public Response<Boolean> update(@RequestBody KnowledgeQuiz quiz) {
        return Response.success(quizService.update(quiz));
    }

    @PostMapping("/delete")
    public Response<Boolean> delete(@RequestBody IdRequest request) {
        return Response.success(quizService.delete(request.getId()));
    }

    public static class PageRequest {
        private Integer page = 1;
        private Integer pageSize = 10;
        private String pointId;

        public Integer getPage() { return page; }
        public void setPage(Integer page) { this.page = page; }
        public Integer getPageSize() { return pageSize; }
        public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }
        public String getPointId() { return pointId; }
        public void setPointId(String pointId) { this.pointId = pointId; }
    }

    public static class IdRequest {
        private Long id;
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
    }
}
