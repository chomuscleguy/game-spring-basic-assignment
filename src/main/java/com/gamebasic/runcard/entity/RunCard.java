package com.gamebasic.runcard.entity;

import com.gamebasic.game.entity.Game;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "run_cards", indexes = @Index(name = "idx_run_card_game", columnList = "game_id"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RunCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @Column(nullable = false)
    private String cardType;

    @Column(nullable = false)
    private int acquiredFloor;

    public RunCard(Game game, String cardType, int acquiredFloor) {
        this.game = game;
        this.cardType = cardType;
        this.acquiredFloor = acquiredFloor;
    }
}
