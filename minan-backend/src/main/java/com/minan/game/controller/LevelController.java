package com.minan.game.controller;

import com.minan.game.dto.Response;
import com.minan.game.model.Level;
import com.minan.game.service.LevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.minan.game.dto.BasePageResponse;
import com.minan.game.dto.LevelQueryRequest;
import com.minan.game.model.Level;
import java.util.List;

@RestController
@RequestMapping("/levels")
public class LevelController {

    @Autowired
    private LevelService levelService;

    /**
     * 获取所有关卡列表（分页）
     */
    @PostMapping("/page")
    public Response<BasePageResponse<Level>> getLevelsPage(@RequestBody LevelQueryRequest request) {
        BasePageResponse<Level> result = levelService.getLevelsPage(request);
        return Response.success(result);
    }

    /**
     * 根据ID获取关卡详情
     */
    @GetMapping("/getDetail")
    public Response<Level> getLevelById(@RequestParam Long id) {
        Level level = levelService.getLevelById(id);
        if (level == null) {
            return Response.error("关卡不存在");
        }
        return Response.success(level);
    }

    /**
     * 根据关卡ID获取关卡详情
     */
    @GetMapping("/getByLevelId")
    public Response<Level> getLevelByLevelId(@RequestParam String levelId) {
        Level level = levelService.getLevelByLevelId(levelId);
        if (level == null) {
            return Response.error("关卡不存在");
        }
        return Response.success(level);
    }

    /**
     * 新增关卡
     */
    @PostMapping("/add")
    public Response<Level> addLevel(@RequestBody Level level) {
        Level newLevel = levelService.addLevel(level);
        return Response.success(newLevel);
    }

    /**
     * 更新关卡
     */
    @PostMapping("/update")
    public Response<Level> updateLevel(@RequestBody Level level) {
        if (level.getId() == null) {
            return Response.error("关卡ID不能为空");
        }
        Level updatedLevel = levelService.updateLevel(level);
        return Response.success(updatedLevel);
    }

    /**
     * 删除关卡
     */
    @PostMapping("/delete")
    public Response<Boolean> deleteLevel(@RequestParam Long id) {
        boolean success = levelService.deleteLevel(id);
        if (!success) {
            return Response.error("删除关卡失败");
        }
        return Response.success(true);
    }

    /**
     * 根据关卡ID删除关卡
     */
    @PostMapping("/deleteByLevelId")
    public Response<Boolean> deleteLevelByLevelId(@RequestParam String levelId) {
        boolean success = levelService.deleteLevelByLevelId(levelId);
        if (!success) {
            return Response.error("删除关卡失败");
        }
        return Response.success(true);
    }
}