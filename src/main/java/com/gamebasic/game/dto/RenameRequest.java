package com.gamebasic.game.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RenameRequest {
    @NotBlank()
    @Size(min = 2, max = 12, message = "playerName 값이 올바르지 않습니다: 크기가 2에서 12 사이여야 합니다")
    private final String playerName;
}
