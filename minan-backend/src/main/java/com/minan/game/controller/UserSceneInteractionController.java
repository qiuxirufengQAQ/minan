package com.minan.game.controller;

import com.minan.game.dto.Response;
import com.minan.game.model.UserSceneInteraction;
import com.minan.game.service.UserSceneInteractionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/scene-interaction")
public class UserSceneInteractionController {

    @Autowired
    private UserSceneInteractionService userSceneInteractionService;

    @GetMapping("/check")
    public Response<Boolean> checkCompleted(@RequestParam String userId, @RequestParam String npcId, @RequestParam String sceneId) {
        return Response.success(userSceneInteractionService.hasCompleted(userId, npcId, sceneId));
    }

    @GetMapping("/detail")
    public Response<UserSceneInteraction> getDetail(@RequestParam String userId, @RequestParam String npcId, @RequestParam String sceneId) {
        return Response.success(userSceneInteractionService.getByUserNpcScene(userId, npcId, sceneId));
    }

    @GetMapping("/list")
    public Response<List<UserSceneInteraction>> getList(@RequestParam String userId, @RequestParam String npcId, @RequestParam String sceneId) {
        return Response.success(userSceneInteractionService.listByUserNpcScene(userId, npcId, sceneId));
    }

    @GetMapping("/best-score")
    public Response<UserSceneInteraction> getBestScore(@RequestParam String userId, @RequestParam String npcId, @RequestParam String sceneId) {
        return Response.success(userSceneInteractionService.getBestScore(userId, npcId, sceneId));
    }

    @PostMapping("/save")
    public Response<Boolean> saveInteraction(@RequestBody SaveRequest request) {
        return Response.success(userSceneInteractionService.saveInteraction(
            request.getUserId(),
            request.getNpcId(),
            request.getSceneId(),
            request.getScore(),
            request.getIntimacyGained(),
            request.getUserInput()
        ));
    }

    public static class SaveRequest {
        private String userId;
        private String npcId;
        private String sceneId;
        private Integer score;
        private Integer intimacyGained;
        private String userInput;

        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getNpcId() { return npcId; }
        public void setNpcId(String npcId) { this.npcId = npcId; }
        public String getSceneId() { return sceneId; }
        public void setSceneId(String sceneId) { this.sceneId = sceneId; }
        public Integer getScore() { return score; }
        public void setScore(Integer score) { this.score = score; }
        public Integer getIntimacyGained() { return intimacyGained; }
        public void setIntimacyGained(Integer intimacyGained) { this.intimacyGained = intimacyGained; }
        public String getUserInput() { return userInput; }
        public void setUserInput(String userInput) { this.userInput = userInput; }
    }
}
