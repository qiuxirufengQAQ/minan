package cn.qrfeng.lianai.game.controller;

import cn.qrfeng.lianai.game.dto.Response;
import cn.qrfeng.lianai.game.model.UserNpcRelation;
import cn.qrfeng.lianai.game.service.UserNpcRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user-npc")
public class UserNpcRelationController {

    @Autowired
    private UserNpcRelationService userNpcRelationService;

    @GetMapping("/list/{userId}")
    public Response<List<UserNpcRelation>> listByUser(@PathVariable String userId) {
        return Response.success(userNpcRelationService.listByUserId(userId));
    }

    @GetMapping("/detail")
    public Response<UserNpcRelation> getDetail(@RequestParam String userId, @RequestParam String npcId) {
        return Response.success(userNpcRelationService.getByUserAndNpc(userId, npcId));
    }

    @PostMapping("/init")
    public Response<Boolean> initRelation(@RequestBody InitRequest request) {
        return Response.success(userNpcRelationService.initRelation(request.getUserId(), request.getNpcId()));
    }

    @PostMapping("/add-score")
    public Response<Boolean> addScore(@RequestBody AddScoreRequest request) {
        return Response.success(userNpcRelationService.addIntimacyScore(request.getUserId(), request.getNpcId(), request.getScore()));
    }

    @GetMapping("/can-unlock")
    public Response<Boolean> canUnlock(@RequestParam String userId, @RequestParam String npcId, @RequestParam int requiredScore) {
        return Response.success(userNpcRelationService.canUnlockScene(userId, npcId, requiredScore));
    }

    public static class InitRequest {
        private String userId;
        private String npcId;

        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getNpcId() { return npcId; }
        public void setNpcId(String npcId) { this.npcId = npcId; }
    }

    public static class AddScoreRequest {
        private String userId;
        private String npcId;
        private Integer score;

        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getNpcId() { return npcId; }
        public void setNpcId(String npcId) { this.npcId = npcId; }
        public Integer getScore() { return score; }
        public void setScore(Integer score) { this.score = score; }
    }
}
