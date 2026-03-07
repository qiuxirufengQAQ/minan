package cn.qrfeng.lianai.game.controller;

import cn.qrfeng.lianai.game.dto.BasePageResponse;
import cn.qrfeng.lianai.game.dto.PromptQueryRequest;
import cn.qrfeng.lianai.game.dto.Response;
import cn.qrfeng.lianai.game.model.Prompt;
import cn.qrfeng.lianai.game.service.PromptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/prompts")
public class PromptController {

    @Autowired
    private PromptService promptService;

    /**
     * 获取所有提示词列表（分页）
     */
    @PostMapping("/page")
    public Response<BasePageResponse<Prompt>> getPromptsPage(@RequestBody PromptQueryRequest request) {
        BasePageResponse<Prompt> result = promptService.getPromptsPage(request);
        return Response.success(result);
    }

    /**
     * 根据场景ID获取提示词列表
     */
    @GetMapping("/listBySceneId")
    public Response<List<Prompt>> getPromptsBySceneId(@RequestParam String sceneId) {
        List<Prompt> prompts = promptService.getPromptsBySceneId(sceneId);
        return Response.success(prompts);
    }

    /**
     * 根据ID获取提示词详情
     */
    @GetMapping("/getDetail")
    public Response<Prompt> getPromptById(@RequestParam Long id) {
        Prompt prompt = promptService.getPromptById(id);
        if (prompt == null) {
            return Response.error("提示词不存在");
        }
        return Response.success(prompt);
    }

    /**
     * 根据提示词ID获取提示词详情
     */
    @GetMapping("/getByPromptId")
    public Response<Prompt> getPromptByPromptId(@RequestParam String promptId) {
        Prompt prompt = promptService.getPromptByPromptId(promptId);
        if (prompt == null) {
            return Response.error("提示词不存在");
        }
        return Response.success(prompt);
    }

    /**
     * 新增提示词
     */
    @PostMapping("/add")
    public Response<Prompt> addPrompt(@RequestBody Prompt prompt) {
        Prompt newPrompt = promptService.addPrompt(prompt);
        return Response.success(newPrompt);
    }

    /**
     * 更新提示词
     */
    @PostMapping("/update")
    public Response<Prompt> updatePrompt(@RequestBody Prompt prompt) {
        if (prompt.getId() == null) {
            return Response.error("提示词ID不能为空");
        }
        Prompt updatedPrompt = promptService.updatePrompt(prompt);
        return Response.success(updatedPrompt);
    }

    /**
     * 删除提示词
     */
    @PostMapping("/delete")
    public Response<Boolean> deletePrompt(@RequestBody java.util.Map<String, Long> request) {
        Long id = request.get("id");
        boolean success = promptService.deletePrompt(id);
        if (!success) {
            return Response.error("删除提示词失败");
        }
        return Response.success(true);
    }

    /**
     * 根据提示词ID删除提示词
     */
    @PostMapping("/deleteByPromptId")
    public Response<Boolean> deletePromptByPromptId(@RequestParam String promptId) {
        boolean success = promptService.deletePromptByPromptId(promptId);
        if (!success) {
            return Response.error("删除提示词失败");
        }
        return Response.success(true);
    }

    /**
     * 根据场景ID删除提示词
     */
    @PostMapping("/deleteBySceneId")
    public Response<Boolean> deletePromptsBySceneId(@RequestParam String sceneId) {
        boolean success = promptService.deletePromptsBySceneId(sceneId);
        return Response.success(true);
    }
}