package com.gamebasic.ranking.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class RankingResponse {
    private final String season;
    private final Integer totalRecords;
    private final Integer excludedCount;
    private final List<RankingEntry> entries;
}
