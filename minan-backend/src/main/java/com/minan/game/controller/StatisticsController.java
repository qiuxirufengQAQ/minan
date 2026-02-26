package com.minan.game.controller;

import com.minan.game.dto.Response;
import com.minan.game.dto.StatisticsResponse;
import com.minan.game.service.StatisticsService;
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
