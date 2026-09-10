package com.gamebasic.game.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "games")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 12)
    private String playerName;

    @Column(nullable = false)
    private int currentHp;

    @Column(nullable = false)
    private int currentFloor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private GamePhase phase;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private GameStatus status;

    @Column(nullable = false)
    private int deckSize = 0;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public Game(String playerName) {
        this.playerName = playerName;
        this.currentHp = 99;
        this.currentFloor = 1;
        this.phase = GamePhase.REWARD;
        this.status = GameStatus.PLAYING;
    }

    public void rename(String playerName) {
        this.playerName = playerName;
    }

    public void updateProgress(
        int currentHp,
        int currentFloor,
        GamePhase phase,
        GameStatus status,
        int deckSize
    ) {
        this.currentHp = currentHp;
        this.currentFloor = currentFloor;
        this.phase = phase;
        this.status = status;
        this.deckSize = deckSize;
    }

    public boolean isFinished() {
        return status != GameStatus.PLAYING;
    }
}
