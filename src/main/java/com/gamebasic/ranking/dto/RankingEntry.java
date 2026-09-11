package com.gamebasic.ranking.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RankingEntry {
    private final Integer rank;
    private final String playerName;
    private final Integer clearTimeSeconds;
    private final Integer remainingHp;
    private final Integer bossTurns;
    private final Integer deckSize;
}
