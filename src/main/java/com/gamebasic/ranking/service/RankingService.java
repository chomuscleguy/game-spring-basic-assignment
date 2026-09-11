package com.gamebasic.ranking.service;

import com.gamebasic.ranking.client.RankingClient;
import com.gamebasic.ranking.client.dto.RankingSource;
import com.gamebasic.ranking.dto.RankingEntry;
import com.gamebasic.ranking.dto.RankingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class RankingService {
    private final RankingClient rankingClient;
    private final RankingValidator rankingValidator;

    public RankingResponse getRankings() {
        RankingSource source = rankingClient.fetch();
        Integer totalRecords = source.meta().totalRecords();

        List<RankingSource.Record> allRecords = source.records();

        List<RankingSource.Record> targetRecords = allRecords.stream()
                .filter(r -> r.run() != null
                        && "CLEARED".equalsIgnoreCase(r.run().status())
                        && r.run().clearedFloor() != null
                        && r.run().clearedFloor() == 10)
                .toList();

        List<RankingSource.Record> validRecords = rankingValidator.validate(targetRecords);

        Comparator<RankingSource.Record> comparator = Comparator
                .comparing((RankingSource.Record r) -> r.run().durationSeconds())
                .thenComparing(Comparator.comparing((RankingSource.Record r) -> r.run().finalHp()).reversed())
                .thenComparing(RankingSource.Record::id);

        List<RankingSource.Record> sortedAndUniqueRecords = validRecords.stream()
                .sorted(comparator)
                .collect(Collectors.toMap(
                        r -> r.player().id(),
                        Function.identity(),
                        (existing, replacement) -> existing,
                        java.util.LinkedHashMap::new
                ))
                .values()
                .stream()
                .toList();

        int excludeCount = targetRecords.size() - validRecords.size();

        List<RankingEntry> entries = IntStream.range(0, sortedAndUniqueRecords.size())
                .mapToObj(index -> {
                    RankingSource.Record record = sortedAndUniqueRecords.get(index);
                    return new RankingEntry(
                            (index + 1),
                            record.player().name(),
                            record.run().durationSeconds(),
                            record.run().finalHp(),
                            record.bossFight().totalTurns(),
                            record.deck().size()
                    );
                })
                .toList();

        return new RankingResponse(
                source.meta().season().id(),
                totalRecords,
                excludeCount,
                entries
        );
    }
}