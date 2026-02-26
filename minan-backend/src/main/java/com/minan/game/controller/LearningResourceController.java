package com.minan.game.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minan.game.dto.BasePageResponse;
import com.minan.game.dto.Response;
import com.minan.game.model.LearningResource;
import com.minan.game.service.LearningResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/resources")
public class LearningResourceController {

    @Autowired
    private LearningResourceService learningResourceService;

    @PostMapping("/page")
    public Response<BasePageResponse<LearningResource>> page(@RequestBody ResourcePageRequest request) {
        Page<LearningResource> page = learningResourceService.pageByPointId(request.getPage(), request.getPageSize(), request.getPointId());
        BasePageResponse<LearningResource> response = new BasePageResponse<>();
        response.setRecords(page.getRecords());
        response.setTotal(page.getTotal());
        response.setCurrent(page.getCurrent());
        response.setSize((int) page.getSize());
        return Response.success(response);
    }

    @GetMapping("/point/{pointId}")
    public Response<List<LearningResource>> listByPointId(@PathVariable String pointId) {
        return Response.success(learningResourceService.listByPointId(pointId));
    }

    @GetMapping("/{id}")
    public Response<LearningResource> getById(@PathVariable Long id) {
        return Response.success(learningResourceService.getById(id));
    }

    @PostMapping("/add")
    public Response<Boolean> add(@RequestBody LearningResource resource) {
        return Response.success(learningResourceService.add(resource));
    }

    @PostMapping("/update")
    public Response<Boolean> update(@RequestBody LearningResource resource) {
        return Response.success(learningResourceService.update(resource));
    }

    @PostMapping("/delete")
    public Response<Boolean> delete(@RequestBody IdRequest request) {
        return Response.success(learningResourceService.delete(request.getId()));
    }

    @PostMapping("/by-ids")
    public Response<List<LearningResource>> getByIds(@RequestBody IdsRequest request) {
        return Response.success(learningResourceService.getByIds(request.getIds()));
    }

    @PostMapping("/by-resource-ids")
    public Response<List<LearningResource>> getByResourceIds(@RequestBody ResourceIdsRequest request) {
        return Response.success(learningResourceService.getByResourceIds(request.getResourceIds()));
    }

    public static class ResourcePageRequest {
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

    public static class IdsRequest {
        private List<Long> ids;
        public List<Long> getIds() { return ids; }
        public void setIds(List<Long> ids) { this.ids = ids; }
    }

    public static class ResourceIdsRequest {
        private List<String> resourceIds;
        public List<String> getResourceIds() { return resourceIds; }
        public void setResourceIds(List<String> resourceIds) { this.resourceIds = resourceIds; }
    }
}
