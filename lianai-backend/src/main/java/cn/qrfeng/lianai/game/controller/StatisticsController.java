package cn.qrfeng.lianai.game.controller;

import cn.qrfeng.lianai.game.dto.Response;
import cn.qrfeng.lianai.game.dto.StatisticsResponse;
import cn.qrfeng.lianai.game.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/get")
    public Response<StatisticsResponse> getStatistics() {
        StatisticsResponse statistics = statisticsService.getStatistics();
        return Response.success(statistics);
    }
}
