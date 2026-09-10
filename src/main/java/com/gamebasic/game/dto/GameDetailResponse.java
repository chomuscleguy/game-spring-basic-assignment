package com.gamebasic.game.dto;

import com.gamebasic.game.entity.GamePhase;
import com.gamebasic.game.entity.GameStatus;
import com.gamebasic.runcard.dto.CardResponse;
import lombok.Getter;

import java.util.List;

@Getter
public class GameDetailResponse {
    private final Long id;
    private final String playerName;
    private final int currentHp;
    private final int currentFloor;
    private final GamePhase phase;
    private final GameStatus status;
    private final List<CardResponse> deck;

    public GameDetailResponse(
        Long id,
        String playerName,
        int currentHp,
        int currentFloor,
        GamePhase phase,
        GameStatus status,
        List<CardResponse> deck
    ) {
        this.id = id;
        this.playerName = playerName;
        this.currentHp = currentHp;
        this.currentFloor = currentFloor;
        this.phase = phase;
        this.status = status;
        this.deck = List.copyOf(deck);
    }
}
