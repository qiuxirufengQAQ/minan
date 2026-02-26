package com.minan.game.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minan.game.dto.BasePageResponse;
import com.minan.game.dto.Response;
import com.minan.game.model.DailyTask;
import com.minan.game.service.DailyTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class DailyTaskController {

    @Autowired
    private DailyTaskService dailyTaskService;

    @GetMapping("/list")
    public Response<List<DailyTask>> list() {
        return Response.success(dailyTaskService.listAllActive());
    }

    @PostMapping("/page")
    public Response<BasePageResponse<DailyTask>> page(@RequestBody TaskPageRequest request) {
        Page<DailyTask> page = dailyTaskService.page(request.getPage(), request.getPageSize());
        BasePageResponse<DailyTask> response = new BasePageResponse<>();
        response.setRecords(page.getRecords());
        response.setTotal(page.getTotal());
        response.setCurrent(page.getCurrent());
        response.setSize((int) page.getSize());
        return Response.success(response);
    }

    @GetMapping("/{id}")
    public Response<DailyTask> getById(@PathVariable Long id) {
        return Response.success(dailyTaskService.getById(id));
    }

    @PostMapping("/add")
    public Response<Boolean> add(@RequestBody DailyTask task) {
        return Response.success(dailyTaskService.add(task));
    }

    @PostMapping("/update")
    public Response<Boolean> update(@RequestBody DailyTask task) {
        return Response.success(dailyTaskService.update(task));
    }

    @PostMapping("/delete")
    public Response<Boolean> delete(@RequestBody IdRequest request) {
        return Response.success(dailyTaskService.delete(request.getId()));
    }

    public static class TaskPageRequest {
        private Integer page = 1;
        private Integer pageSize = 10;

        public Integer getPage() { return page; }
        public void setPage(Integer page) { this.page = page; }
        public Integer getPageSize() { return pageSize; }
        public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }
    }

    public static class IdRequest {
        private Long id;
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
    }
}
