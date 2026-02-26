package com.minan.game.controller;

import com.minan.game.dto.BasePageResponse;
import com.minan.game.dto.Response;
import com.minan.game.dto.SceneQueryRequest;
import com.minan.game.model.Scene;
import com.minan.game.service.SceneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/scenes")
public class SceneController {

    @Autowired
    private SceneService sceneService;

    /**
     * 获取所有场景列表（分页）
     */
    @PostMapping("/page")
    public Response<BasePageResponse<Scene>> getScenesPage(@RequestBody SceneQueryRequest request) {
        BasePageResponse<Scene> result = sceneService.getScenesPage(request);
        return Response.success(result);
    }

    /**
     * 根据关卡ID获取场景列表
     */
    @GetMapping("/listByLevelId")
    public Response<List<Scene>> getScenesByLevelId(@RequestParam String levelId) {
        List<Scene> scenes = sceneService.getScenesByLevelId(levelId);
        return Response.success(scenes);
    }

    /**
     * 根据ID获取场景详情
     */
    @GetMapping("/getDetail")
    public Response<Scene> getSceneById(@RequestParam Long id) {
        Scene scene = sceneService.getSceneById(id);
        if (scene == null) {
            return Response.error("场景不存在");
        }
        return Response.success(scene);
    }

    /**
     * 根据场景ID获取场景详情
     */
    @GetMapping("/getBySceneId")
    public Response<Scene> getSceneBySceneId(@RequestParam String sceneId) {
        Scene scene = sceneService.getSceneBySceneId(sceneId);
        if (scene == null) {
            return Response.error("场景不存在");
        }
        return Response.success(scene);
    }

    /**
     * 新增场景
     */
    @PostMapping("/add")
    public Response<Scene> addScene(@RequestBody Scene scene) {
        Scene newScene = sceneService.addScene(scene);
        return Response.success(newScene);
    }

    /**
     * 更新场景
     */
    @PostMapping("/update")
    public Response<Scene> updateScene(@RequestBody Scene scene) {
        if (scene.getId() == null) {
            return Response.error("场景ID不能为空");
        }
        Scene updatedScene = sceneService.updateScene(scene);
        return Response.success(updatedScene);
    }

    /**
     * 删除场景
     */
    @PostMapping("/delete")
    public Response<Boolean> deleteScene(@RequestParam Long id) {
        boolean success = sceneService.deleteScene(id);
        if (!success) {
            return Response.error("删除场景失败");
        }
        return Response.success(true);
    }

    /**
     * 根据场景ID删除场景
     */
    @PostMapping("/deleteBySceneId")
    public Response<Boolean> deleteSceneBySceneId(@RequestParam String sceneId) {
        boolean success = sceneService.deleteSceneBySceneId(sceneId);
        if (!success) {
            return Response.error("删除场景失败");
        }
        return Response.success(true);
    }

    /**
     * 根据关卡ID删除场景
     */
    @PostMapping("/deleteByLevelId")
    public Response<Boolean> deleteScenesByLevelId(@RequestParam String levelId) {
        boolean success = sceneService.deleteScenesByLevelId(levelId);
        return Response.success(true);
    }
}