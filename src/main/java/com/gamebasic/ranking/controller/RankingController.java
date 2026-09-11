package com.gamebasic.ranking.controller;

import com.gamebasic.ranking.dto.RankingResponse;
import com.gamebasic.ranking.service.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RankingController {

    private final RankingService rankingService;

    @GetMapping("/rankings")
    public ResponseEntity<RankingResponse> getRankings() {
        RankingResponse response = rankingService.getRankings();
        return ResponseEntity.ok(response);
    }
}