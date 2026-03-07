package cn.qrfeng.lianai.game.controller;

import cn.qrfeng.lianai.game.dto.Response;
import cn.qrfeng.lianai.game.model.KnowledgeCategory;
import cn.qrfeng.lianai.game.service.KnowledgeCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/knowledge-categories")
public class KnowledgeCategoryController {

    @Autowired
    private KnowledgeCategoryService categoryService;

    @GetMapping("/list")
    public Response<List<KnowledgeCategory>> list() {
        return Response.success(categoryService.listAll());
    }

    @GetMapping("/tree")
    public Response<List<KnowledgeCategory>> tree() {
        return Response.success(categoryService.listTree());
    }

    @GetMapping("/level/{level}")
    public Response<List<KnowledgeCategory>> listByLevel(@PathVariable Integer level) {
        return Response.success(categoryService.listByLevel(level));
    }

    @GetMapping("/parent/{parentId}")
    public Response<List<KnowledgeCategory>> listByParentId(@PathVariable String parentId) {
        return Response.success(categoryService.listByParentId(parentId));
    }

    @GetMapping("/{id}")
    public Response<KnowledgeCategory> getById(@PathVariable Long id) {
        return Response.success(categoryService.getById(id));
    }

    @PostMapping("/add")
    public Response<Boolean> add(@RequestBody KnowledgeCategory category) {
        return Response.success(categoryService.add(category));
    }

    @PostMapping("/update")
    public Response<Boolean> update(@RequestBody KnowledgeCategory category) {
        return Response.success(categoryService.update(category));
    }

    @PostMapping("/delete")
    public Response<Boolean> delete(@RequestBody IdRequest request) {
        return Response.success(categoryService.delete(request.getId()));
    }

    public static class IdRequest {
        private Long id;
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
    }
}
