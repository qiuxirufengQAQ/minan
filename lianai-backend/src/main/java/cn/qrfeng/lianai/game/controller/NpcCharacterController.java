package cn.qrfeng.lianai.game.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.qrfeng.lianai.game.dto.BasePageResponse;
import cn.qrfeng.lianai.game.dto.Response;
import cn.qrfeng.lianai.game.model.NpcCharacter;
import cn.qrfeng.lianai.game.service.NpcCharacterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/npcs")
public class NpcCharacterController {

    @Autowired
    private NpcCharacterService npcCharacterService;

    @GetMapping("/list")
    public Response<List<NpcCharacter>> list() {
        return Response.success(npcCharacterService.listAll());
    }

    @PostMapping("/page")
    public Response<BasePageResponse<NpcCharacter>> page(@RequestBody NpcPageRequest request) {
        Page<NpcCharacter> page = npcCharacterService.page(request.getPage(), request.getPageSize());
        BasePageResponse<NpcCharacter> response = new BasePageResponse<>();
        response.setRecords(page.getRecords());
        response.setTotal(page.getTotal());
        response.setCurrent(page.getCurrent());
        response.setSize((int) page.getSize());
        return Response.success(response);
    }

    @GetMapping("/{id}")
    public Response<NpcCharacter> getById(@PathVariable Long id) {
        return Response.success(npcCharacterService.getById(id));
    }

    @GetMapping("/getByNpcId")
    public Response<NpcCharacter> getByNpcId(@RequestParam String npcId) {
        return Response.success(npcCharacterService.getByNpcId(npcId));
    }

    @PostMapping("/add")
    public Response<Boolean> add(@RequestBody NpcCharacter npc) {
        return Response.success(npcCharacterService.add(npc));
    }

    @PostMapping("/update")
    public Response<Boolean> update(@RequestBody NpcCharacter npc) {
        return Response.success(npcCharacterService.update(npc));
    }

    @PostMapping("/delete")
    public Response<Boolean> delete(@RequestBody IdRequest request) {
        return Response.success(npcCharacterService.delete(request.getId()));
    }

    public static class NpcPageRequest {
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
