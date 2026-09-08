package com.gamebasic.game.controller;

import com.gamebasic.game.dto.CreateRequest;
import com.gamebasic.game.dto.GameDetailResponse;
import com.gamebasic.game.service.GameService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GameController {
    private final GameService gameService;

    @GetMapping("/games")
    public ResponseEntity<List<Object>> getGames() {
        // List<Object>는 임시 구현이며, Lv 7에서 제대로 고칩니다.
        // List.of()는 빈 목록을 돌려주는 임시 구현이며, Lv 7에서 제대로 고칩니다.
        return ResponseEntity.ok(List.of());
    }

    @PostMapping("/games")
    public ResponseEntity<GameDetailResponse> createGame(@Valid @RequestBody CreateRequest request) {
        GameDetailResponse created = gameService.createGame(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // TODO (Lv 6): 진행과 전체 덱 저장. 주석을 풀고 구현하세요.
    // @PutMapping("/games/{gameId}/progress")
    // public ResponseEntity<?> updateProgress(
    //     @PathVariable Long gameId,
    //     @Valid @RequestBody ProgressRequest request
    // ) {
    //     return ResponseEntity.ok(gameService.updateProgress(gameId, request));
    // }
}
