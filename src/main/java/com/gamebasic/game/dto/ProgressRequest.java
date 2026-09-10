package com.gamebasic.game.dto;

import com.gamebasic.game.entity.GamePhase;
import com.gamebasic.game.entity.GameStatus;
import com.gamebasic.runcard.dto.RunCardRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

// 완성된 코드입니다. 수정하지 마세요.
@Getter
public class ProgressRequest {
    @NotNull
    @Min(0)
    @Max(99)
    private Integer currentHp;

    @NotNull
    @Min(1)
    @Max(10)
    private Integer currentFloor;

    @NotNull
    private GamePhase phase;

    @NotNull
    private GameStatus status;

    @NotEmpty
    private List<@Valid RunCardRequest> deck;
}
