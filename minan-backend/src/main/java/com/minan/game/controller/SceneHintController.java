package com.minan.game.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minan.game.dto.BasePageResponse;
import com.minan.game.dto.Response;
import com.minan.game.model.SceneHint;
import com.minan.game.service.SceneHintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hints")
public class SceneHintController {

    @Autowired
    private SceneHintService sceneHintService;

    @GetMapping("/scene/{sceneId}")
    public Response<List<SceneHint>> listByScene(@PathVariable String sceneId) {
        return Response.success(sceneHintService.listBySceneId(sceneId));
    }

    @PostMapping("/page")
    public Response<BasePageResponse<SceneHint>> page(@RequestBody HintPageRequest request) {
        Page<SceneHint> page = sceneHintService.page(request.getPage(), request.getPageSize(), request.getSceneId());
        BasePageResponse<SceneHint> response = new BasePageResponse<>();
        response.setRecords(page.getRecords());
        response.setTotal(page.getTotal());
        response.setCurrent(page.getCurrent());
        response.setSize((int) page.getSize());
        return Response.success(response);
    }

    @GetMapping("/{id}")
    public Response<SceneHint> getById(@PathVariable Long id) {
        return Response.success(sceneHintService.getById(id));
    }

    @PostMapping("/add")
    public Response<Boolean> add(@RequestBody SceneHint hint) {
        return Response.success(sceneHintService.add(hint));
    }

    @PostMapping("/update")
    public Response<Boolean> update(@RequestBody SceneHint hint) {
        return Response.success(sceneHintService.update(hint));
    }

    @PostMapping("/delete")
    public Response<Boolean> delete(@RequestBody IdRequest request) {
        return Response.success(sceneHintService.delete(request.getId()));
    }

    public static class HintPageRequest {
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
