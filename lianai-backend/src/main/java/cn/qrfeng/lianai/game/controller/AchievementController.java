package cn.qrfeng.lianai.game.controller;

import cn.qrfeng.lianai.game.dto.BasePageResponse;
import cn.qrfeng.lianai.game.dto.Response;
import cn.qrfeng.lianai.game.dto.AchievementQueryRequest;
import cn.qrfeng.lianai.game.model.Achievement;
import cn.qrfeng.lianai.game.service.AchievementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/achievements")
public class AchievementController {

    @Autowired
    private AchievementService achievementService;

    /**
     * 获取所有成就列表（分页）
     */
    @PostMapping("/page")
    public Response<BasePageResponse<Achievement>> getAchievementsPage(@RequestBody AchievementQueryRequest request) {
        BasePageResponse<Achievement> result = achievementService.getAchievementsPage(request);
        return Response.success(result);
    }

    /**
     * 根据ID获取成就详情
     */
    @GetMapping("/getDetail")
    public Response<Achievement> getAchievementById(@RequestParam Long id) {
        Achievement achievement = achievementService.getAchievementById(id);
        if (achievement == null) {
            return Response.error("成就不存在");
        }
        return Response.success(achievement);
    }

    /**
     * 根据成就ID获取成就详情
     */
    @GetMapping("/getByAchievementId")
    public Response<Achievement> getAchievementByAchievementId(@RequestParam String achievementId) {
        Achievement achievement = achievementService.getAchievementByAchievementId(achievementId);
        if (achievement == null) {
            return Response.error("成就不存在");
        }
        return Response.success(achievement);
    }

    /**
     * 新增成就
     */
    @PostMapping("/add")
    public Response<Achievement> addAchievement(@RequestBody Achievement achievement) {
        Achievement newAchievement = achievementService.addAchievement(achievement);
        return Response.success(newAchievement);
    }

    /**
     * 更新成就
     */
    @PostMapping("/update")
    public Response<Achievement> updateAchievement(@RequestBody Achievement achievement) {
        if (achievement.getId() == null) {
            return Response.error("成就ID不能为空");
        }
        Achievement updatedAchievement = achievementService.updateAchievement(achievement);
        return Response.success(updatedAchievement);
    }

    /**
     * 删除成就
     */
    @PostMapping("/delete")
    public Response<Boolean> deleteAchievement(@RequestBody IdRequest request) {
        boolean success = achievementService.deleteAchievement(request.getId());
        if (!success) {
            return Response.error("删除成就失败");
        }
        return Response.success(true);
    }
    
    public static class IdRequest {
        private Long id;
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
    }

    /**
     * 根据成就ID删除成就
     */
    @PostMapping("/deleteByAchievementId")
    public Response<Boolean> deleteAchievementByAchievementId(@RequestParam String achievementId) {
        boolean success = achievementService.deleteAchievementByAchievementId(achievementId);
        if (!success) {
            return Response.error("删除成就失败");
        }
        return Response.success(true);
    }
}