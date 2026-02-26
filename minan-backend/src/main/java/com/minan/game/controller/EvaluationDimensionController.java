package com.minan.game.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minan.game.dto.BasePageResponse;
import com.minan.game.dto.Response;
import com.minan.game.model.EvaluationDimension;
import com.minan.game.service.EvaluationDimensionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dimensions")
public class EvaluationDimensionController {

    @Autowired
    private EvaluationDimensionService evaluationDimensionService;

    @GetMapping("/scene/{sceneId}")
    public Response<List<EvaluationDimension>> listByScene(@PathVariable String sceneId) {
        return Response.success(evaluationDimensionService.listBySceneId(sceneId));
    }

    @PostMapping("/page")
    public Response<BasePageResponse<EvaluationDimension>> page(@RequestBody DimensionPageRequest request) {
        Page<EvaluationDimension> page = evaluationDimensionService.page(request.getPage(), request.getPageSize(), request.getSceneId());
        BasePageResponse<EvaluationDimension> response = new BasePageResponse<>();
        response.setRecords(page.getRecords());
        response.setTotal(page.getTotal());
        response.setCurrent(page.getCurrent());
        response.setSize((int) page.getSize());
        return Response.success(response);
    }

    @GetMapping("/{id}")
    public Response<EvaluationDimension> getById(@PathVariable Long id) {
        return Response.success(evaluationDimensionService.getById(id));
    }

    @PostMapping("/add")
    public Response<Boolean> add(@RequestBody EvaluationDimension dimension) {
        return Response.success(evaluationDimensionService.add(dimension));
    }

    @PostMapping("/update")
    public Response<Boolean> update(@RequestBody EvaluationDimension dimension) {
        return Response.success(evaluationDimensionService.update(dimension));
    }

    @PostMapping("/delete")
    public Response<Boolean> delete(@RequestBody IdRequest request) {
        return Response.success(evaluationDimensionService.delete(request.getId()));
    }

    public static class DimensionPageRequest {
        private Integer page = 1;
        private Integer pageSize = 10;
        private String sceneId;

        public Integer getPage() { return page; }
        public void setPage(Integer page) { this.page = page; }
        public Integer getPageSize() { return pageSize; }
        public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }
        public String getSceneId() { return sceneId; }
        public void setSceneId(String sceneId) { this.sceneId = sceneId; }
    }

    public static class IdRequest {
        private Long id;
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
    }
}
