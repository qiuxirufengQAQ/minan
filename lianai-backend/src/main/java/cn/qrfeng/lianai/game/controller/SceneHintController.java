package cn.qrfeng.lianai.game.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.qrfeng.lianai.game.dto.BasePageResponse;
import cn.qrfeng.lianai.game.dto.Response;
import cn.qrfeng.lianai.game.model.SceneHint;
import cn.qrfeng.lianai.game.service.SceneHintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hints")
public class SceneHintController {

    @Autowired
    private SceneHintService sceneHintService;

    @GetMapping("/all")
    public Response<List<SceneHint>> listAll() {
        return Response.success(sceneHintService.listAll());
    }

    @PostMapping("/page")
    public Response<BasePageResponse<SceneHint>> page(@RequestBody HintPageRequest request) {
        Page<SceneHint> page = sceneHintService.page(request.getPage(), request.getPageSize(), request.getHintType());
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
    public Response<SceneHint> add(@RequestBody SceneHint hint) {
        boolean success = sceneHintService.add(hint);
        if (success) {
            return Response.success(hint);
        } else {
            return Response.error("添加场景提示失败");
        }
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
        private String hintType;

        public Integer getPage() { return page; }
        public void setPage(Integer page) { this.page = page; }
        public Integer getPageSize() { return pageSize; }
        public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }
        public String getHintType() { return hintType; }
        public void setHintType(String hintType) { this.hintType = hintType; }
    }

    public static class IdRequest {
        private Long id;
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
    }
}
