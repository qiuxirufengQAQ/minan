package com.minan.game.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minan.game.dto.BasePageResponse;
import com.minan.game.dto.Response;
import com.minan.game.model.KnowledgePoint;
import com.minan.game.service.KnowledgePointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/knowledge-points")
public class KnowledgePointController {

    @Autowired
    private KnowledgePointService pointService;

    @PostMapping("/page")
    public Response<BasePageResponse<KnowledgePoint>> page(@RequestBody PageRequest request) {
        Page<KnowledgePoint> page = pointService.page(request.getPage(), request.getPageSize(), request.getCategoryId(), request.getDifficulty());
        BasePageResponse<KnowledgePoint> response = new BasePageResponse<>();
        response.setRecords(page.getRecords());
        response.setTotal(page.getTotal());
        response.setCurrent(page.getCurrent());
        response.setSize((int) page.getSize());
        return Response.success(response);
    }

    @GetMapping("/list")
    public Response<List<KnowledgePoint>> list() {
        return Response.success(pointService.listAll());
    }

    @GetMapping("/category/{categoryId}")
    public Response<List<KnowledgePoint>> listByCategoryId(@PathVariable String categoryId) {
        return Response.success(pointService.listByCategoryId(categoryId));
    }

    @GetMapping("/{id}")
    public Response<KnowledgePoint> getById(@PathVariable Long id) {
        return Response.success(pointService.getById(id));
    }

    @PostMapping("/add")
    public Response<Boolean> add(@RequestBody KnowledgePoint point) {
        return Response.success(pointService.add(point));
    }

    @PostMapping("/update")
    public Response<Boolean> update(@RequestBody KnowledgePoint point) {
        return Response.success(pointService.update(point));
    }

    @PostMapping("/delete")
    public Response<Boolean> delete(@RequestBody IdRequest request) {
        return Response.success(pointService.delete(request.getId()));
    }

    @PostMapping("/by-ids")
    public Response<List<KnowledgePoint>> getByIds(@RequestBody IdsRequest request) {
        return Response.success(pointService.getByIds(request.getIds()));
    }

    public static class PageRequest {
        private Integer page = 1;
        private Integer pageSize = 10;
        private String categoryId;
        private Integer difficulty;

        public Integer getPage() { return page; }
        public void setPage(Integer page) { this.page = page; }
        public Integer getPageSize() { return pageSize; }
        public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }
        public String getCategoryId() { return categoryId; }
        public void setCategoryId(String categoryId) { this.categoryId = categoryId; }
        public Integer getDifficulty() { return difficulty; }
        public void setDifficulty(Integer difficulty) { this.difficulty = difficulty; }
    }

    public static class IdRequest {
        private Long id;
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
    }

    public static class IdsRequest {
        private List<String> ids;
        public List<String> getIds() { return ids; }
        public void setIds(List<String> ids) { this.ids = ids; }
    }
}
