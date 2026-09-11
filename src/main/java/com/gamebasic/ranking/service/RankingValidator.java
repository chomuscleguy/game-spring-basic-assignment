package com.gamebasic.ranking.service;

import com.gamebasic.game.entity.BossPhase;
import com.gamebasic.ranking.client.dto.RankingSource;
import com.gamebasic.runcard.entity.CardType;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class RankingValidator {

    private final Set<String> VALID_CARD_TYPES = Arrays.stream(CardType.values())
            .map(Enum::name)
            .collect(Collectors.toSet());

    private final List<BossPhase> EXPECTED_PHASES = List.of(
            BossPhase.THRONE,
            BossPhase.UNBOUND,
            BossPhase.ECLIPSE
    );

    public List<RankingSource.Record> validate(List<RankingSource.Record> records) {
        return records.stream()
                .filter(this::isValidRecord)
                .toList();
    }

    private boolean isValidRecord(RankingSource.Record record) {
        if (!isValidRun(record)) {
            //System.out.println("Run 탈락 ID: " + record.id());
            return false;
        }
        if (!isValidDeck(record)) {
            //System.out.println("Deck 탈락 ID: " + record.id());
            return false;
        }
        if (!isValidBossFight(record)) {
            //System.out.println("BossFight 탈락 ID: " + record.id());
            return false;
        }
        return true;
    }
//    private boolean isValidRecord(RankingSource.Record record) {
//        return isValidRun(record)
//                && isValidDeck(record)
//                && isValidBossFight(record);
//    }

    private boolean isValidRun(RankingSource.Record record) {
        RankingSource.Record.Run run = record.run();

        int minDuration = run.clearedFloor() * 30;
        if (run.durationSeconds() < minDuration) {
            //System.out.println("최소 시간보다 작음");

            return false;
        }

        if(run.finalHp()<1 || run.finalHp() > 99)
        {
            //System.out.println("체력문제");
            return false;
        }

        return true;
    }

    private boolean isValidDeck(RankingSource.Record record) {
        RankingSource.Record.Deck deck = record.deck();

        List<RankingSource.Record.Deck.Card> cards = deck.cards();
        int cardCount = cards.size();

        if (cardCount < 9 || cardCount > 20 || deck.size() != cardCount) {
            //System.out.println("덱 카운트 문제");
            return false;
        }

        for (RankingSource.Record.Deck.Card card : cards) {
            if (card == null)
                return false;

            if (!isValidCardType(card.cardType())) {
                //System.out.println("카드 타입 문제");
                return false;
            }

            Integer acquiredFloor = card.acquiredFloor();

            if (acquiredFloor == null || acquiredFloor < 0 || acquiredFloor > 9) {
                //System.out.println("획득 층수 문제");
                return false;
            }
        }
        return true;
    }

    private boolean isValidBossFight(RankingSource.Record record) {
        RankingSource.Record.BossFight bossFight = record.bossFight();

        List<RankingSource.Record.BossFight.Phase> phases = bossFight.phases();
        if (phases.size() != 3) {
            //System.out.println("보스페이즈가 3개 이하");
            return false;
        }

        int calculatedTotalTurns = 0;

        for (int i = 0; i < 3; i++) {
            RankingSource.Record.BossFight.Phase phase = phases.get(i);

            if(phase.phase() != EXPECTED_PHASES.get(i)) {
                //System.out.println("보스 페이즈가 일치하지 않음");
                return false;
            }
            if (phase.turns() < 1) {
                //System.out.println("턴이 0임");
                return false;
            }

            calculatedTotalTurns += phase.turns();
        }

        if (bossFight.totalTurns() != calculatedTotalTurns) {
            //System.out.println("토탈 턴이 다름");
            return false;
        }

        String finishingCard = bossFight.finishingCard();
        if (!isValidCardType(finishingCard) || record.deck() == null || record.deck().cards() == null) {
            //System.out.println("피니쉬 카드 타입이 다름");
            return false;
        }

        boolean hasFinishingCard = record.deck().cards().stream()
                .anyMatch(card -> finishingCard.equals(card.cardType()));

        if (!hasFinishingCard) {
            //System.out.println("피니쉬 카드가 덱에 없음");
            return false;
        }

        return true;
    }

    private boolean isValidCardType(String cardTypeStr) {
        if (cardTypeStr == null)
            return false;
        return VALID_CARD_TYPES.contains(cardTypeStr);
    }
}