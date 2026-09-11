package com.gamebasic.ranking.client.dto;

import com.gamebasic.game.entity.BossPhase;

import java.time.LocalDateTime;
import java.util.List;

public record RankingSource(Meta meta, List<Record> records) {
    public record Meta(Season season, LocalDateTime generatedAt, String schemaVersion, Integer totalRecords) {
        public record Season(String id, String name, LocalDateTime startsAt, LocalDateTime endsAt) {
        }
    }

    public record Record(Long id, LocalDateTime submittedAt, Client client, Player player, Run run, BossFight bossFight,
                         Deck deck) {
        public record Client(String version, String platform, String locale) {
        }

        public record Player(String id, String name, String region, List<String> tags) {
        }

        public record Run(String seed, String status, Integer clearedFloor, Integer durationSeconds, Integer finalHp,
                          List<Floor> floors) {
            public record Floor(Integer floor, String enemy, Integer turns, Integer hpAfter, List<Reward> rewards) {
                public record Reward(List<String> offered, String picked) {
                }
            }
        }

        public record BossFight(List<Phase> phases, String finishingCard, Integer totalTurns) {
            public record Phase(BossPhase phase, Integer turns, String damageTaken) {
            }
        }

        public record Deck(Integer size, List<Card> cards) {
            public record Card(String cardType, Integer acquiredFloor) {
            }
        }
    }
}